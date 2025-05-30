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
package com.diffplug.spotless.cli.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.diffplug.spotless.ResourceHarness;

import static org.assertj.core.api.Assertions.assertThat;

class ExecutionLayoutTest extends ResourceHarness {

    SpotlessRunCleanup cleanup;

    @BeforeEach
    void setUp() throws IOException {
        cleanup = SpotlessRunCleanup.INSTANCE;
    }

    @AfterEach
    void cleanup() {
        cleanup.clearCleanables();
        cleanup = null;
    }

    @Test
    void itResolvesGradleBuildDir() {
        setFile("settings.gradle").toLines("rootProject.name = 'test'");
        ExecutionLayout layout = ExecutionLayout.create(fileResolver(), commandLineStream());

        Path buildDir = layout.buildDir();

        assertThat(buildDir.toString())
                .matches("\\Q%s\\E.*\\Q%s\\E.*"
                        .formatted(
                                rootFolder().toPath().toString(),
                                Path.of("build", "spotless-cli").toString()));
    }

    @Test
    void itResolvesMavenBuildDir() {
        setFile("pom.xml").toLines("<project><modelVersion>4.0.0</modelVersion></project>");
        ExecutionLayout layout = ExecutionLayout.create(fileResolver(), commandLineStream());

        Path buildDir = layout.buildDir();

        assertThat(buildDir.toString())
                .matches("\\Q%s\\E.*\\Q%s\\E.*"
                        .formatted(
                                rootFolder().toPath().toString(),
                                Path.of("target", "spotless-cli").toString()));
    }

    @Test
    void itResolvesTmpBuildDir() {
        ExecutionLayout layout = ExecutionLayout.create(fileResolver(), commandLineStream());

        Path buildDir = layout.buildDir();

        assertThat(buildDir.toString())
                .doesNotStartWith(rootFolder().toPath().toString())
                .startsWith(System.getProperty("java.io.tmpdir"));
    }

    @Test
    void itCleansUpTmpBuildDir() throws IOException {
        ExecutionLayout layout = ExecutionLayout.create(fileResolver(), commandLineStream());
        Path buildDir = layout.buildDir();
        buildDir.toFile().mkdirs(); // make sure it's ready
        Path tmpFile = buildDir.resolve("tmpFile.txt");
        Files.writeString(tmpFile, "Test %s".formatted(buildDir));

        cleanup.clearCleanables();

        assertThat(Files.exists(buildDir)).isFalse();
    }

    private FileResolver fileResolver() {
        return new FileResolver(rootFolder().toPath());
    }

    private SpotlessCommandLineStream commandLineStream() {
        return Mockito.mock(SpotlessCommandLineStream.class);
    }
}
