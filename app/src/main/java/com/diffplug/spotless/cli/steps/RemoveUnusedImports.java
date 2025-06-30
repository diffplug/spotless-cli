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

import com.diffplug.spotless.FormatterStep;
import com.diffplug.spotless.cli.core.SpotlessActionContext;
import com.diffplug.spotless.cli.help.AdditionalInfoLinks;
import com.diffplug.spotless.cli.help.OptionConstants;
import com.diffplug.spotless.cli.help.SupportedFileTypes;
import com.diffplug.spotless.java.CleanthatJavaStep;
import com.diffplug.spotless.java.RemoveUnusedImportsStep;
import org.jetbrains.annotations.NotNull;
import picocli.CommandLine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@CommandLine.Command(name = "remove-unused-imports", description = "Removes unused imports from Java files.")
@SupportedFileTypes("Java")
@AdditionalInfoLinks("https://github.com/diffplug/spotless/tree/main/plugin-gradle#removeunusedimports")
public class RemoveUnusedImports extends SpotlessFormatterStep {

    @CommandLine.Option(
            names = {"--engine", "-e"},
            defaultValue = "GOOGLE_JAVA_FORMAT",
            description =
                    "The backing engine to use for detecting and removing unused imports." + OptionConstants.VALID_AND_DEFAULT_VALUES_SUFFIX)
    Engine engine;

    public enum Engine {
        GOOGLE_JAVA_FORMAT {
            @Override
            String formatterName() {
                return RemoveUnusedImportsStep.defaultFormatter();

            }
        },
        CLEAN_THAT {
            @Override
            String formatterName() {
                return "cleanthat-javaparser-unnecessaryimport";
            }
        };


        abstract String formatterName();
    }

    @Override
    public @NotNull List<FormatterStep> prepareFormatterSteps(SpotlessActionContext context) {
        return Collections.singletonList(RemoveUnusedImportsStep.create(engine.formatterName(), context.provisioner()));
    }
}
