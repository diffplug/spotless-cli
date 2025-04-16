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
package com.diffplug.spotless.cli;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import com.diffplug.selfie.Selfie;
import com.diffplug.spotless.ProcessRunner;
import com.diffplug.spotless.ResourceHarness;

import static org.assertj.core.api.Assertions.assertThat;

class ForeignExeMockTest extends ResourceHarness {

    @Test
    void itWritesDefaultExecutable() throws Exception {
        ForeignExeMock mock = ForeignExeMock.builder("clang-format", "1.0.0").build();

        assertThat(mock.getContent()).contains("--version");
    }

    @Test
    void itWritesClangFormatExecutable() throws Exception {
        ForeignExeMock mock = ForeignExeMock.builder("clang-format", "1.0.0")
                .withStringConsumingOption("--style", List.of("LLVM", "Chrome", "Google", "Mozilla", "WebKit"))
                .withStringReturningOption("--help", "This is the help text")
                .withReadFromStdin()
                .withWriteToStdout()
                .build();

        assertThat(mock.getContent()).contains("--style");
    }

    @Test
    void itWritesAExecutableForeignExeMockThatWritesVersion() throws IOException, InterruptedException {
        File mock = createClangFormatForeignExeMock();

        try (ProcessRunner runner = new ProcessRunner()) {
            ProcessRunner.Result result = runner.exec(rootFolder(), null, null, List.of(mock.getName(), "--version"));
            String output = result.assertExitZero(StandardCharsets.UTF_8);
            assertThat(output).contains("11.0.1");
        }
    }

    @Test
    void itWritesAExecutableForeignExeMockThatChecksValidOptions() throws IOException, InterruptedException {
        File mock = createClangFormatForeignExeMock();

        try (ProcessRunner runner = new ProcessRunner()) {
            ProcessRunner.Result result =
                    runner.exec(rootFolder(), null, null, List.of(mock.getName(), "--style", "invalid_style_value"));
            assertThat(result.exitCode()).isNotEqualTo(0);
            assertThat(result.stdOutUtf8()).contains("invalid_style_value");
        }
    }

    @Test
    void itWritesAExecutableForeignExeMockThatConsumesValidOption() throws IOException, InterruptedException {
        File mock = createClangFormatForeignExeMock();

        try (ProcessRunner runner = new ProcessRunner()) {
            ProcessRunner.Result result =
                    runner.exec(rootFolder(), null, null, List.of(mock.getName(), "--style", "LLVM"));
            String output = result.assertExitZero(StandardCharsets.UTF_8);
        }
    }

    @Test
    void itWritesAExecutableForeignExeMockThatConsumesValidOptionAndExecutesReformatting()
            throws IOException, InterruptedException {
        File mock = createClangFormatForeignExeMock();

        try (ProcessRunner runner = new ProcessRunner()) {
            String input =
                    """
                    int main(){
                    return 0;
                    }
                    """
                            .stripLeading();
            ProcessRunner.Result result = runner.exec(
                    rootFolder(),
                    null,
                    input.getBytes(StandardCharsets.UTF_8),
                    List.of(mock.getName(), "--style", "LLVM"));
            String output = result.assertExitZero(StandardCharsets.UTF_8);
            Selfie.expectSelfie(output).toBe("""
int main(){   \s
return 0;   \s
}   \s
   \s
""");
        }
    }

    @Test
    void itWritesAWindowsBatFile() throws IOException, InterruptedException {
        File mock = createClangFormatForeignExeMock(ForeignExeMock.TargetOs.WINDOWS);
        String mockContent = Files.readString(mock.toPath());
        assertThat(mockContent).contains("--version");
    }

    private @NotNull File createClangFormatForeignExeMock() throws IOException {
        return createClangFormatForeignExeMock(ForeignExeMock.TargetOs.current());
    }

    private @NotNull File createClangFormatForeignExeMock(@NotNull ForeignExeMock.TargetOs targetOs)
            throws IOException {
        ForeignExeMock mock = ForeignExeMock.builder("clang-format", "11.0.1")
                .withStringConsumingOption("--style", List.of("LLVM", "Chrome", "Google", "Mozilla", "WebKit"))
                .withStringReturningOption("--help", "This is the help text")
                .withReadFromStdin()
                .withWriteToStdout()
                .build(targetOs);

        return setFile(mock.getFileName())
                .toContent(mock.getContent())
                .makeExecutable()
                .getFile();
    }
}
