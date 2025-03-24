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

import com.diffplug.spotless.cli.logging.output.LoggingConfigurer;

import picocli.CommandLine;

class LoggingOptions {

    @CommandLine.ArgGroup(exclusive = true, multiplicity = "0..1")
    LoggingLevelOptions loggingLevelOptions;

    static class LoggingLevelOptions {
        @CommandLine.Spec(CommandLine.Spec.Target.MIXEE)
        CommandLine.Model.CommandSpec spec;

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
        boolean quiet;

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
}
