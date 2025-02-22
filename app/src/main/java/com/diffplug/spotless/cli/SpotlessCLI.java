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

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.jetbrains.annotations.NotNull;

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
        description = "Runs spotless",
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
        subcommandsRepeatable = true,
        subcommands = {LicenseHeader.class, GoogleJavaFormat.class, Prettier.class})
public class SpotlessCLI implements SpotlessAction, SpotlessCommand, SpotlessActionContextProvider {

    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec; // injected by picocli

    @CommandLine.Option(
            names = {"--mode", "-m"},
            defaultValue = "APPLY",
            description = "The mode to run spotless in." + OptionConstants.VALID_AND_DEFAULT_VALUES_SUFFIX)
    SpotlessMode spotlessMode;

    @CommandLine.Option(
            names = {"--basedir"},
            hidden = true,
            description = "The base directory to run spotless in. Intended for testing purposes only.")
    Path baseDir;

    @CommandLine.Option(
            names = {"--target", "-t"},
            description = "The target files to format.")
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

    @Override
    public Integer executeSpotlessAction(FormatterStepsSupplier formatterSteps) {

        validateTargets();
        TargetResolver targetResolver = targetResolver();

        try (FormatterFactory formatterFactory =
                        new ThreadLocalFormatterFactory(lineEnding.createPolicy(), encoding, formatterSteps);
                ExecutorService executor = createExecutorServiceForFormatting(formatterFactory)) {

            List<Future<Result>> stepResults = targetResolver
                    .resolveTargets()
                    .map(path -> {
                        return executor.submit(() -> {
                            Formatter formatter = formatterFactory.createFormatter();
                            return new Result(path, LintState.of(formatter, path.toFile()), formatter);
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

    private @NotNull ExecutorService createExecutorServiceForFormatting(FormatterFactory formatterFactory) {
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
            //			System.out.println("File is clean: " + result.target.toFile().getName());
            return ResultType.CLEAN;
        }
        if (result.lintState().getDirtyState().didNotConverge()) {
            System.err.println("File did not converge: "
                    + result.target().toFile().getName()); // TODO: where to print the output to?
            return ResultType.DID_NOT_CONVERGE;
        }
        return this.spotlessMode.handleResult(result);
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
