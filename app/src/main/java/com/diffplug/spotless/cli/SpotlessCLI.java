/*
 * Copyright 2024 DiffPlug
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.diffplug.spotless.cli;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.diffplug.spotless.Formatter;
import com.diffplug.spotless.LineEnding;
import com.diffplug.spotless.LintState;
import com.diffplug.spotless.ThrowingEx;
import com.diffplug.spotless.cli.core.FileResolver;
import com.diffplug.spotless.cli.core.SpotlessActionContext;
import com.diffplug.spotless.cli.core.SpotlessCommandLineStream;
import com.diffplug.spotless.cli.core.TargetFileTypeInferer;
import com.diffplug.spotless.cli.core.TargetResolver;
import com.diffplug.spotless.cli.execution.FormatterStepsSupplier;
import com.diffplug.spotless.cli.execution.SpotlessExecutionStrategy;
import com.diffplug.spotless.cli.help.OptionConstants;
import com.diffplug.spotless.cli.logging.output.LoggingConfigurer;
import com.diffplug.spotless.cli.logging.output.Output;
import com.diffplug.spotless.cli.steps.GoogleJavaFormat;
import com.diffplug.spotless.cli.steps.LicenseHeader;
import com.diffplug.spotless.cli.steps.Prettier;
import com.diffplug.spotless.cli.version.SpotlessCLIVersionProvider;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(
        name = "spotless",
        mixinStandardHelpOptions = true,
        usageHelpAutoWidth = true,
        versionProvider = SpotlessCLIVersionProvider.class,
        description =
                "%n@|magenta spotless|@ is a command line interface (CLI) for the spotless code formatter. "
                        + "%nIt can either check if your files are formatted according to your configuration or apply the formatting to the files.%n",
        header =
                """
                                 __  __             \s
               _________  ____  / /_/ /__  __________
              / ___/ __ \\/ __ \\/ __/ / _ \\/ ___/ ___/
             (__  ) /_/ / /_/ / /_/ /  __(__  |__  )\s
            /____/ .___/\\____/\\__/_/\\___/____/____/ \s Spotless CLI
                /_/                                 \s

            """,
        synopsisSubcommandLabel = "[FORMATTING_STEPS]",
        commandListHeading = "%nAvailable formatting steps:%n",
        exitCodeListHeading = "%nPossible exit codes:%n",
        exitCodeOnExecutionException = -2,
        exitCodeList = {
            """
            0:Successful formatting.
            In @|yellow APPLY|@ mode, this means all files were formatted successfully.
            In @|yellow CHECK|@ mode, this means all files were already formatted properly.""",
            """
            1:Some files need to be formatted.
            In @|yellow APPLY|@ mode, this means some files failed to be formatted (see output for details).
            In @|yellow CHECK|@ mode, this means some files are currently not formatted properly (and might be fixed in APPLY mode).""",
            """
            -1:Some files did not converge. This can happen when one formatter does not converge on the file content.
            You can find more about this special case here:
              <https://github.com/diffplug/spotless/blob/main/PADDEDCELL.md>""",
            "-2:An exception occurred during execution."
        },
        subcommandsRepeatable = true,
        subcommands = {LicenseHeader.class, GoogleJavaFormat.class, Prettier.class})
public class SpotlessCLI implements SpotlessAction, SpotlessCommand, SpotlessActionContextProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpotlessCLI.class);

    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec; // injected by picocli

    private @NotNull Output output;

    @CommandLine.Option(
            names = {"--mode", "-m"},
            defaultValue = "APPLY",
            description =
                    "The mode to run spotless in." + OptionConstants.VALID_AND_DEFAULT_VALUES_SUFFIX
                            + OptionConstants.NEW_LINE
                            + """
                    APPLY: Apply the correct formatting where needed (replace file contents with formatted content).
                    CHECK: Check if the files are formatted or show the diff of the formatting.""")
    SpotlessMode spotlessMode;

    @CommandLine.Option(
            names = {"--basedir"},
            hidden = true,
            description = "The base directory to run spotless in. Intended for testing purposes only.")
    Path baseDir;

    @CommandLine.Option(
            names = {"--target", "-t"},
            description =
                    """
        The target files to format. Blobs are supported.
        Examples:
        -t 'src/**/*.java'
        -t 'src/**/*.kt'
        -t 'README.md'""")
    public List<String> targets;

    @CommandLine.Option(
            names = {"--encoding", "-e"},
            defaultValue = "UTF-8",
            description = "The encoding of the files to format." + OptionConstants.DEFAULT_VALUE_SUFFIX)
    public Charset encoding;

    @CommandLine.Option(
            names = {"--line-ending", "-l"},
            defaultValue = "UNIX",
            description = "The line ending of the files to format." + OptionConstants.VALID_AND_DEFAULT_VALUES_SUFFIX)
    public LineEnding lineEnding;

    private int parallelity;

    @CommandLine.Option(
            names = {"--parallelity", "-p"},
            paramLabel = "N",
            description =
                    "The number of parallel formatter threads to run. " + OptionConstants.DEFAULT_VALUE_SUFFIX_BEGIN
                            + "#cores * 0.5" + OptionConstants.DEFAULT_VALUE_SUFFIX_END)
    public void setParallelity(int parallelity) {
        if (parallelity < 1) {
            throw new CommandLine.ParameterException(spec.commandLine(), "Error: --parallelity must be > 0");
        }
        this.parallelity = parallelity;
    }

    @CommandLine.ArgGroup(exclusive = true, multiplicity = "0..1")
    LoggingLevelOptions loggingLevelOptions;

    public static class LoggingLevelOptions {

        @CommandLine.Spec
        CommandLine.Model.CommandSpec spec; // injected by picocli

        private boolean[] verbosity;

        @CommandLine.Option(
                names = {"-v"},
                description = "Enable verbose output. Multiple -v options increase the verbosity (max 5).",
                arity = "0")
        public void setVerbose(boolean[] verbosity) {
            if (verbosity.length > 5) {
                throw new CommandLine.ParameterException(
                        spec.commandLine(), "Error: --verbose can be used at most 5 times");
            }
            this.verbosity = verbosity;
        }

        @CommandLine.Option(
                names = {"--quiet", "-q"},
                description = "Disable as much output as possible.",
                arity = "0")
        public boolean quiet;

        LoggingConfigurer.CLIOutputLevel toCliOutputLevel() {
            if (quiet) {
                return LoggingConfigurer.CLIOutputLevel.QUIET;
            }
            if (verbosity == null) {
                return LoggingConfigurer.CLIOutputLevel.DEFAULT;
            }
            int verbosityCount = this.verbosity.length;
            return LoggingConfigurer.CLIOutputLevel.verbosity(verbosityCount);
        }
    }

    @CommandLine.Option(
            names = {"--log-file"},
            description = "The log file to write the output to.")
    File logFile;

    @CommandLine.Option(
            names = {"--zzz"},
            hidden = true,
            defaultValue = "true",
            description = "Just a hook to be able to initialize logging.")
    void setFinal(boolean finalFlag) {
        // this is a hack to make sure that the logging is initialized before any processing occurs outside of parsing
        // the command line.
        // Maybe there is a picocli hook I've missed for this?
        this.output = setupLogging(spec, loggingLevelOptions, logFile);
    }

    private static @NotNull Output setupLogging(
            CommandLine.Model.CommandSpec spec, LoggingLevelOptions loggingLevelOptions, File logFile) {
        CommandLine commandLine = spec.commandLine();
        LoggingConfigurer.CLIOutputLevel outputLevel = loggingLevelOptions != null
                ? loggingLevelOptions.toCliOutputLevel()
                : LoggingConfigurer.CLIOutputLevel.DEFAULT;
        Output output =
                LoggingConfigurer.configureLogging(outputLevel, logFile, commandLine::getErr, commandLine::getOut);
        // the following logs are to make sure that the logging is configured correctly
        logMetaStatements();
        return output;
    }

    private static void logMetaStatements() {
        Logger spotlessCliLogger = LoggerFactory.getLogger("com.diffplug.spotless.cli.meta");
        spotlessCliLogger.info("Meta: spotless cli loggers on level info enabled.");
        spotlessCliLogger.debug("Meta: spotless cli loggers on level debug enabled.");
        Logger spotlessLibLogger = LoggerFactory.getLogger("com.diffplug.spotless.meta");
        spotlessLibLogger.info("Meta: spotless loggers on level info enabled.");
        spotlessLibLogger.debug("Meta: spotless loggers on level debug enabled.");
        Logger nonSpotlessLogger = LoggerFactory.getLogger("meta");
        nonSpotlessLogger.info("Meta: non-spotless loggers on level info enabled.");
        nonSpotlessLogger.debug("Meta: non-spotless loggers on level debug enabled.");
    }

    public @NotNull Output output() {
        return this.output;
    }

    @Override
    public @NotNull Integer executeSpotlessAction(@NotNull FormatterStepsSupplier formatterSteps) {
        Objects.requireNonNull(output);
        Objects.requireNonNull(formatterSteps);
        validateTargets();
        TargetResolver targetResolver = targetResolver();

        try (FormatterFactory formatterFactory =
                        new ThreadLocalFormatterFactory(lineEnding.createPolicy(), encoding, formatterSteps);
                ExecutorService executor = createExecutorServiceForFormatting()) {

            List<Future<Result>> stepResults = targetResolver
                    .resolveTargets()
                    .map(path -> {
                        return executor.submit(() -> {
                            Formatter formatter = formatterFactory.createFormatter();
                            // actual formatting
                            LOGGER.debug("Formatting file: {}", path);
                            LintState lintState = LintState.of(formatter, path.toFile());
                            LOGGER.debug("LintState for file {}: {}", path, lintState);
                            return new Result(path, lintState, formatter);
                        });
                    })
                    .toList();
            ResultType resultType = stepResults.stream()
                    .map(future -> ThrowingEx.get(future::get))
                    .map(this::handleResult)
                    .reduce(ResultType.CLEAN, ResultType::combineWith);
            return spotlessMode.translateResultTypeToExitCode(resultType);
        }
    }

    private @NotNull ExecutorService createExecutorServiceForFormatting() {
        return Executors.newFixedThreadPool(numberOfParallelThreads());
    }

    private int numberOfParallelThreads() {
        return parallelity == 0 ? Math.max(Runtime.getRuntime().availableProcessors() / 2, 1) : parallelity;
    }

    private void validateTargets() {
        if (targets == null || targets.isEmpty()) { // cannot use `required = true` because of the subcommands
            throw new CommandLine.ParameterException(
                    spec.commandLine(),
                    "Error: Missing required argument (specify one of these): (--target=<targets> | -t)");
        }
    }

    private ResultType handleResult(Result result) {
        if (result.lintState().isClean()) {
            LOGGER.debug("File is clean: {}", result.target().toFile());
            return ResultType.CLEAN;
        }
        if (result.lintState().getDirtyState().didNotConverge()) {
            LOGGER.warn("File did not converge: {}", result.target().toFile());
            return ResultType.DID_NOT_CONVERGE;
        }
        return this.spotlessMode.handleResult(output, result);
    }

    private TargetResolver targetResolver() {
        return new TargetResolver(baseDir(), targets);
    }

    private Path baseDir() {
        return baseDir == null ? Path.of(System.getProperty("user.dir")) : baseDir;
    }

    @Override
    public SpotlessActionContext spotlessActionContext(SpotlessCommandLineStream commandLineStream) {
        validateTargets();
        TargetResolver targetResolver = targetResolver();
        TargetFileTypeInferer targetFileTypeInferer = new TargetFileTypeInferer(targetResolver);
        return SpotlessActionContext.builder()
                .targetFileType(targetFileTypeInferer.inferTargetFileType())
                .fileResolver(new FileResolver(baseDir()))
                .commandLineStream(commandLineStream)
                .build();
    }

    public static void main(String... args) {
        int exitCode = createCommandLine(createInstance()).execute(args);
        System.exit(exitCode);
    }

    static SpotlessCLI createInstance() {
        return new SpotlessCLI();
    }

    static CommandLine createCommandLine(SpotlessCLI spotlessCLI) {
        return new CommandLine(spotlessCLI)
                .setExecutionStrategy(new SpotlessExecutionStrategy())
                .setCaseInsensitiveEnumValuesAllowed(true);
    }
}
