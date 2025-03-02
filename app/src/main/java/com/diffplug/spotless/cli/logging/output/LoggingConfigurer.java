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
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.diffplug.spotless.ThrowingEx;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class LoggingConfigurer {

    public static void configureLogging(@NotNull CLIOutputLevel cliOutputLevel, @Nullable File logFile) {
        configureJdkLogging(cliOutputLevel, logFile);
    }

    private static void configureJdkLogging(@NotNull CLIOutputLevel cliOutputLevel, @Nullable File logFile) {

        // Reset the logging configuration to remove any default handlers
        LogManager.getLogManager().reset();

        // Create a new console handler
        Handler rootHandler = createRootHandler(logFile);

        // Set the root logger level to OFF
        Logger rootLogger = Logger.getLogger("");
        rootLogger.setLevel(Level.OFF); // only enable specifics
        rootLogger.addHandler(rootHandler); // Add the configured handler

        Logger spotlessLibLogger = Logger.getLogger("com.diffplug.spotless");
        Logger spotlessCliLogger = Logger.getLogger("com.diffplug.spotless.cli");

        ConsoleHandler outputConsoleHandler = new ConsoleHandler();
        outputConsoleHandler.setLevel(Level.ALL); // Set logging level
        outputConsoleHandler.setFormatter(new PlainMessageFormatter()); // Set formatter

        Logger outputLogger = Logger.getLogger(Output.OUTPUT_LOGGER_NAME);
        outputLogger.setLevel(Level.ALL);
        outputLogger.setUseParentHandlers(false);
        outputLogger.addHandler(outputConsoleHandler);

        if (cliOutputLevel == CLIOutputLevel.VVVVV) {
            rootLogger.setLevel(Level.ALL);
        } else if (cliOutputLevel == CLIOutputLevel.VVVV) {
            rootLogger.setLevel(Level.INFO);
            spotlessLibLogger.setLevel(Level.ALL);
        } else if (cliOutputLevel == CLIOutputLevel.VVV) {
            spotlessLibLogger.setLevel(Level.ALL);
        } else if (cliOutputLevel == CLIOutputLevel.VV) {
            spotlessLibLogger.setLevel(Level.INFO);
        } else if (cliOutputLevel == CLIOutputLevel.V) {
            //            spotlessLibLogger.setLevel(Level.OFF);
            spotlessCliLogger.setLevel(Level.INFO);
        } else if (cliOutputLevel == CLIOutputLevel.DEFAULT) {
            //            spotlessLibLogger.setLevel(Level.OFF);
            spotlessCliLogger.setLevel(Level.WARNING);
        } else if (cliOutputLevel == CLIOutputLevel.QUIET) {
            //            spotlessLibLogger.setLevel(Level.OFF);
            spotlessCliLogger.setLevel(Level.SEVERE);
        }
    }

    private static @NotNull Handler createRootHandler(@Nullable File logFile) {
        if (logFile == null) {
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(Level.ALL); // Set logging level
            consoleHandler.setFormatter(new SimpleFormatter()); // Set formatter
            return consoleHandler;
        }
        FileHandler fileHandler = ThrowingEx.get(() -> new FileHandler(logFile.getAbsolutePath(), false));
        fileHandler.setLevel(Level.ALL); // Set logging level
        fileHandler.setFormatter(new SimpleFormatter()); // Set formatter
        return fileHandler;
    }

    public enum CLIOutputLevel {
        VVVVV, // everything, even debug levels
        VVVV, // spotless on debug, everything else non- debug levels
        VVV, // everything spotless, even debug levels
        VV, // everything spotless, except debug levels
        V, // only info and above
        DEFAULT, // only warnings and above
        QUIET, // only errors and above
    }
}
