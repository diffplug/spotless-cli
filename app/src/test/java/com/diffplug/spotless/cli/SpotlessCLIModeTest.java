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

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.diffplug.spotless.cli.steps.GoogleJavaFormat;

import static org.assertj.core.api.Assertions.assertThat;

public class SpotlessCLIModeTest extends CLIIntegrationHarness {

    @Test
    void applyModeChangesFilesAndExitsWithCode0() throws IOException {
        setFile("Java.java").toResource("java/googlejavaformat/JavaCodeUnformatted.test");

        SpotlessCLIRunner.Result result = cliRunner()
                .withTargets("*.java")
                .withStep(GoogleJavaFormat.class)
                .run();

        selfie().expectResource("Java.java").toMatchDisk();
        assertThat(result.exitCode()).isEqualTo(0);
    }

    @Test
    void checkModeLeavesFilesUnchangedAndExitsWithCode1() throws IOException {
        setFile("Java.java").toResource("java/googlejavaformat/JavaCodeUnformatted.test");

        SpotlessCLIRunner.Result result = cliRunner()
                .withTargets("*.java")
                .withOption("--mode", "check")
                .withStep(GoogleJavaFormat.class)
                .runAndFail();

        selfie().expectResource("Java.java").toMatchDisk();
        assertThat(result.exitCode()).isEqualTo(1);
    }
}
