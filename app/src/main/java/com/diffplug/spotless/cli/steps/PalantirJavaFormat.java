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

import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.diffplug.spotless.FormatterStep;
import com.diffplug.spotless.cli.core.SpotlessActionContext;
import com.diffplug.spotless.cli.help.FormatterStepConstants;
import com.diffplug.spotless.cli.help.OptionConstants;
import com.diffplug.spotless.java.PalantirJavaFormatStep;

import picocli.CommandLine;

@CommandLine.Command(
        name = "palantir-java-format",
        description = "Runs palantir java format",
        footer = {
            "",
            FormatterStepConstants.SUPPORTED_FILETYPES_INTRO + PalantirJavaFormat.SUPPORTED_FILETYPES,
            "",
            FormatterStepConstants.HOMEPAGE + PalantirJavaFormat.HOMEPAGE
        })
public class PalantirJavaFormat extends SpotlessFormatterStep {
    public static final String SUPPORTED_FILETYPES = "Java";

    public static final String HOMEPAGE = "https://github.com/palantir/palantir-java-format";

    @CommandLine.Option(
            names = {"--style", "-s"},
            defaultValue = "PALANTIR",
            description =
                    "The style to use for the palantir java format." + OptionConstants.VALID_AND_DEFAULT_VALUES_SUFFIX)
    Style style;

    @CommandLine.Option(
            names = {"--format-javadoc", "-j"},
            defaultValue = "false",
            description = "Format javadoc." + OptionConstants.DEFAULT_VALUE_SUFFIX)
    boolean formatJavadoc;

    public enum Style {
        PALANTIR,
        AOSP,
        GOOGLE
    }

    @Override
    public @NotNull List<FormatterStep> prepareFormatterSteps(SpotlessActionContext context) {
        return List.of(PalantirJavaFormatStep.create(
                PalantirJavaFormatStep.defaultVersion(), style.name(), formatJavadoc, context.provisioner()));
    }
}
