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
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.diffplug.spotless.ProcessRunner;
import com.diffplug.spotless.cli.CLIIntegrationHarness;
import com.diffplug.spotless.tag.CliNativeTest;
import com.diffplug.spotless.tag.CliProcessTest;

@CliProcessTest
@CliNativeTest
@EnabledIf("isClangFormatExecAvailable")
class ClangFormatTest extends CLIIntegrationHarness {

    static boolean isClangFormatExecAvailable() {
        try (ProcessRunner processRunner = new ProcessRunner()) {
            ProcessRunner.Result result = processRunner.exec("clang-format", "--version");
            return result.exitCode() == 0;
        } catch (Exception e) {
            return false;
        }
    }

    static String clangFormatVersion() throws Exception {
        try (ProcessRunner processRunner = new ProcessRunner()) {
            ProcessRunner.Result result = processRunner.exec("clang-format", "--version");
            // extract semver from output
            String versionOut = result.stdOutUtf8();
            Pattern semVerPattern = Pattern.compile("\\d+\\.\\d+\\.\\d+");
            java.util.regex.Matcher matcher = semVerPattern.matcher(versionOut);
            if (matcher.find()) {
                return matcher.group();
            } else {
                throw new IllegalStateException("Could not find version in output: " + versionOut);
            }
        }
    }

    @ParameterizedTest
    @MethodSource
    void itFormatsFileType(String testFileName, String resourceName) throws Exception {
        File testFile = setFile(testFileName).toResource(resourceName).getFile();

        cliRunner()
                .withTargets(testFileName)
                .withStep(ClangFormat.class)
                .withOption("--clang-version", clangFormatVersion())
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
                .withOption("--clang-version", clangFormatVersion())
                .withOption("--style", "Google")
                .run();

        assertFile(testFile).notSameSasResource("clang/example.java.dirty");
        selfie().expectFile(testFile).toMatchDisk();
    }
}
