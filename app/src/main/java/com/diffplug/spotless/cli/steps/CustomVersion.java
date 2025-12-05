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
package com.diffplug.spotless.cli.steps;

import java.util.Objects;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;

import com.diffplug.spotless.cli.help.OptionConstants;

import picocli.CommandLine;

public class CustomVersion {

    private static final String LONG_OPTION = "--use-version";
    private static final String SHORT_OPTION = "-v";

    @CommandLine.Option(
            names = {LONG_OPTION, SHORT_OPTION},
            description = "The version of ${COMMAND-NAME} to use." + OptionConstants.DEFAULT_VALUE_SUFFIX)
    String useVersion;

    abstract static class CustomVersionDefaultValueProvider implements CommandLine.IDefaultValueProvider {
        private final Supplier<String> defaultVersionSupplier;

        protected CustomVersionDefaultValueProvider(@NotNull Supplier<String> defaultVersionSupplier) {
            this.defaultVersionSupplier = Objects.requireNonNull(defaultVersionSupplier);
        }

        @Override
        public String defaultValue(CommandLine.Model.ArgSpec argSpec) throws Exception {
            // if it is the use-version option, provide the default version, otherwise null
            if (!argSpec.isOption()) {
                return null;
            }
            if (!(argSpec instanceof CommandLine.Model.OptionSpec optionSpec)) {
                return null;
            }
            if (!optionSpec.longestName().equals(LONG_OPTION)) {
                return null;
            }
            return defaultVersionSupplier.get();
        }
    }
}
