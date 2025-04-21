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

import java.nio.file.Path;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.diffplug.spotless.FormatterStep;
import com.diffplug.spotless.cli.help.FormatterStepConstants;
import com.diffplug.spotless.cli.help.OptionConstants;
import com.diffplug.spotless.cpp.ClangFormatStep;

import picocli.CommandLine;

@CommandLine.Command(
        name = "clang-format",
        description = "Runs clang-format",
        footer = {
            "",
            FormatterStepConstants.SUPPORTED_FILETYPES_INTRO + ClangFormat.SUPPORTED_FILETYPES,
            "",
            FormatterStepConstants.HOMEPAGE + ClangFormat.HOMEPAGE
        })
public class ClangFormat extends SpotlessFormatterStep {

    public static final String SUPPORTED_FILETYPES = "C, C++, Java, JavaScript, JSON, Objective-C, Protobuf, C#";

    public static final String HOMEPAGE = "https://clang.llvm.org/docs/ClangFormat.html";

    @CommandLine.Option(
            names = {"--clang-version", "-v"},
            required = true,
            defaultValue = "10.0.1",
            description = "The version of clang-format to use." + OptionConstants.DEFAULT_VALUE_SUFFIX)
    String version;

    @CommandLine.Option(
            names = {"--clang-format-exec", "-c"},
            description = "The path to the clang-format executable." + OptionConstants.DEFAULT_VALUE_SUFFIX_BEGIN
                    + "looks on your PATH" + OptionConstants.DEFAULT_VALUE_SUFFIX_END)
    Path pathToExec;

    @CommandLine.Option(
            names = {"--style", "-s"},
            description = "The style to use for clang-format.")
    String style;

    @Override
    public @NotNull List<FormatterStep> prepareFormatterSteps() {
        ClangFormatStep clangFormatStep = ClangFormatStep.withVersion(version);
        if (pathToExec != null) {
            clangFormatStep = clangFormatStep.withPathToExe(pathToExec.toString());
        }
        if (style != null) {
            clangFormatStep = clangFormatStep.withStyle(style);
        }
        return List.of(clangFormatStep.create());
    }
}
