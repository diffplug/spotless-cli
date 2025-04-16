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

import java.io.File;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.diffplug.spotless.cli.CLIIntegrationHarness;
import com.diffplug.spotless.cli.ForeignExeMock;
import com.diffplug.spotless.cpp.ClangFormatStep;
import com.diffplug.spotless.tag.CliNativeTest;
import com.diffplug.spotless.tag.CliProcessTest;

@CliProcessTest
@CliNativeTest
class ClangFormatTest extends CLIIntegrationHarness {

    File clangFormatExec;

    @BeforeEach
    void prepareClangFormatExecMock() {
        ForeignExeMock mock = ForeignExeMock.builder("clang-format", ClangFormatStep.defaultVersion())
                .withStringConsumingOption("--style", List.of("LLVM", "Google", "Mozilla"))
                .withStringConsumingOption("--assume-filename")
                .withReadFromStdin()
                .withWriteToStdout()
                .build();

        clangFormatExec = setFile(mock.getFileName())
                .toContent(mock.getContent())
                .makeExecutable()
                .getFile();
    }

    @ParameterizedTest
    @MethodSource
    void itFormatsFileType(String testFileName, String resourceName) throws Exception {
        File testFile = setFile(testFileName).toResource(resourceName).getFile();

        cliRunner()
                .withTargets(testFileName)
                .withStep(ClangFormat.class)
                .withOption("--clang-version", ClangFormatStep.defaultVersion())
                .withOption("--path-to-exec", clangFormatExec.getAbsolutePath())
                .run();

        assertFile(testFile).notSameSasResource(resourceName);
        selfie().expectFile(testFile).toMatchDisk(testFileName);
    }

    static Stream<Arguments> itFormatsFileType() {
        return Stream.of(
                Arguments.of("Test.java", "clang/example.java.dirty"),
                Arguments.of("test.cs", "clang/example.cs"),
                Arguments.of("test.c", "clang/example.c"),
                Arguments.of("Test.js", "clang/example.js"),
                Arguments.of("Test.m", "clang/example.m"),
                Arguments.of("Test.proto", "clang/example.proto"));
    }

    @Test
    void itFormatsWithSpecificStyle() throws Exception {
        File testFile =
                setFile("Test.java").toResource("clang/example.java.dirty").getFile();

        cliRunner()
                .withTargets("Test.java")
                .withStep(ClangFormat.class)
                .withOption("--clang-version", ClangFormatStep.defaultVersion())
                .withOption("--style", "Google")
                .withOption("--path-to-exec", clangFormatExec.getAbsolutePath())
                .run();

        assertFile(testFile).notSameSasResource("clang/example.java.dirty");
        selfie().expectFile(testFile).toMatchDisk();
    }
}
