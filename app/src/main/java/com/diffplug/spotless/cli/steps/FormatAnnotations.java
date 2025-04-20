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

import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.diffplug.spotless.FormatterStep;
import com.diffplug.spotless.cli.help.OptionConstants;
import com.diffplug.spotless.java.FormatAnnotationsStep;

import picocli.CommandLine;

@CommandLine.Command(
        name = "format-annotations",
        description = "Corrects line break formatting of type annotations in java files.")
public class FormatAnnotations extends SpotlessFormatterStep {

    @CommandLine.Option(
            names = {"--add-type-annotation", "-a"},
            arity = "0..*",
            split = OptionConstants.OPTION_LIST_SPLIT,
            paramLabel = "annotation",
            description = "Add annotations to the list of type annotations to keep on the same line as the type.")
    List<String> addedTypeAnnotations;

    @CommandLine.Option(
            names = {"--remove-type-annotation", "-r"},
            arity = "0..*",
            split = OptionConstants.OPTION_LIST_SPLIT,
            paramLabel = "annotation",
            description = "Remove annotations from the list of type annotations to keep on the same line as the type.")
    List<String> removedTypeAnnotations;

    @Override
    public @NotNull List<FormatterStep> prepareFormatterSteps() {
        FormatterStep formatAnnotationsStep = FormatAnnotationsStep.create(
                addedTypeAnnotations == null ? Collections.emptyList() : addedTypeAnnotations,
                removedTypeAnnotations == null ? Collections.emptyList() : removedTypeAnnotations);
        return List.of(formatAnnotationsStep);
    }
}
