/*
 * Copyright 2025 DiffPlug
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
package com.diffplug.spotless.cli.logging.output;

import java.io.File;
import java.io.PrintWriter;
import java.util.function.Supplier;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.diffplug.spotless.ThrowingEx;

import picocli.CommandLine;

public final class LoggingConfigurer {

    public static Output configureLogging(
            @NotNull CLIOutputLevel cliOutputLevel,
            @Nullable File logFile,
            @NotNull Supplier<PrintWriter> stdErr,
            @NotNull Supplier<PrintWriter> stdOut) {
        return configureJdkLogging(cliOutputLevel, logFile, stdErr, stdOut);
    }

    private static Output configureJdkLogging(
            @NotNull CLIOutputLevel cliOutputLevel,
            @Nullable File logFile,
            @NotNull Supplier<PrintWriter> stdErr,
            @NotNull Supplier<PrintWriter> stdOut) {
        // Set the output to Output
        Output output = new Output().with(stdErr.get());

        // Reset the logging configuration to remove any default handlers
        LogManager.getLogManager().reset();

        // Create a new console handler
        final Handler rootHandler = createRootHandler(logFile, stdErr);

        // Set the root logger level to OFF
        final Logger rootLogger = Logger.getLogger("");
        rootLogger.setLevel(Level.OFF); // only enable specifics
        rootLogger.addHandler(rootHandler); // Add the configured handler

        final Logger spotlessLibLogger = Logger.getLogger("com.diffplug.spotless");
        final Logger spotlessCliLogger = Logger.getLogger("com.diffplug.spotless.cli");

        // set the logging level per logger
        switch (cliOutputLevel) {
            case QUIET -> {
                output = output.with(Level.SEVERE);
                spotlessCliLogger.setLevel(Level.SEVERE);
                spotlessLibLogger.setLevel(Level.SEVERE);
                rootLogger.setLevel(Level.SEVERE);
            }
            case DEFAULT -> {
                spotlessCliLogger.setLevel(Level.WARNING);
                spotlessLibLogger.setLevel(Level.WARNING);
                rootLogger.setLevel(Level.SEVERE);
            }
            case V -> {
                spotlessCliLogger.setLevel(Level.INFO);
                spotlessLibLogger.setLevel(Level.WARNING);
                rootLogger.setLevel(Level.SEVERE);
            }
            case VV -> {
                spotlessLibLogger.setLevel(Level.INFO);
                spotlessCliLogger.setLevel(Level.INFO);
                rootLogger.setLevel(Level.SEVERE);
            }
            case VVV -> {
                output = output.with(Level.ALL);
                spotlessCliLogger.setLevel(Level.ALL);
                spotlessLibLogger.setLevel(Level.ALL);
                rootLogger.setLevel(Level.SEVERE);
            }
            case VVVV -> {
                output = output.with(Level.ALL);
                spotlessCliLogger.setLevel(Level.ALL);
                spotlessLibLogger.setLevel(Level.ALL);
                rootLogger.setLevel(Level.INFO);
            }
            case VVVVV -> {
                output = output.with(Level.ALL);
                spotlessCliLogger.setLevel(Level.ALL);
                spotlessLibLogger.setLevel(Level.ALL);
                rootLogger.setLevel(Level.ALL);
            }
        }

        return output;
    }

    private static @NotNull Handler createRootHandler(@Nullable File logFile, @NotNull Supplier<PrintWriter> stdErr) {
        if (logFile == null) {
            PicocliConsoleHandler consoleHandler = new PicocliConsoleHandler(stdErr);
            consoleHandler.setLevel(Level.ALL); // Set logging level
            LogfmtFormatter.KeyDecorator keyDecorator = CommandLine.Help.Ansi.AUTO.enabled()
                    ? LogfmtFormatter.KeyDecorator.MULTI_COLOR
                    : LogfmtFormatter.KeyDecorator.NONE;
            consoleHandler.setFormatter(new LogfmtFormatter(keyDecorator)); // Set formatter
            return consoleHandler;
        }
        FileHandler fileHandler = ThrowingEx.get(() -> new FileHandler(logFile.getAbsolutePath(), false));
        fileHandler.setLevel(Level.ALL); // Set logging level
        fileHandler.setFormatter(new LogfmtFormatter()); // Set formatter
        return fileHandler;
    }

    public enum CLIOutputLevel {
        VVVVV(5), // everything, even debug levels
        VVVV(4), // spotless on debug, everything else non- debug levels
        VVV(3), // everything spotless, even debug levels
        VV(2), // everything spotless, except debug levels
        V(1), // only info and above
        DEFAULT, // only warnings and above
        QUIET; // only errors and above

        private final int verbosity;

        CLIOutputLevel(int verbosity) {
            this.verbosity = verbosity;
        }

        CLIOutputLevel() {
            this(-1);
        }

        public static CLIOutputLevel verbosity(int verbosity) {
            for (CLIOutputLevel level : CLIOutputLevel.values()) {
                if (level.verbosity == verbosity) {
                    return level;
                }
            }
            throw new IllegalArgumentException("Unknown verbosity " + verbosity);
        }
    }
}
