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

import java.util.Map;
import java.util.function.Supplier;

import picocli.CommandLine;

abstract class OptionDefaultValueProvider implements CommandLine.IDefaultValueProvider {
    private final Map<String, Supplier<String>> optionsToDefaults;

    protected OptionDefaultValueProvider(Map<String, Supplier<String>> optionsToDefaults) {
        this.optionsToDefaults = Map.copyOf(optionsToDefaults);
    }

    @Override
    public String defaultValue(CommandLine.Model.ArgSpec argSpec) throws Exception {
        if (!argSpec.isOption()) {
            return null;
        }
        if (!(argSpec instanceof CommandLine.Model.OptionSpec optionSpec)) {
            return null;
        }
        Supplier<String> supplier = optionsToDefaults.get(optionSpec.longestName());
        return supplier != null ? supplier.get() : null;
    }
}
