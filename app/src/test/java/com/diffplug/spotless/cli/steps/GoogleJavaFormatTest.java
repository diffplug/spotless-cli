/*
 * Copyright 2024 DiffPlug
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

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.diffplug.spotless.cli.CLIIntegrationHarness;
import com.diffplug.spotless.cli.SpotlessCLIRunner;
import com.diffplug.spotless.tag.CliProcessTest;

@CliProcessTest
public class GoogleJavaFormatTest extends CLIIntegrationHarness {

    @Test
    void formattingWithGoogleJavaFormatWorks() throws IOException {
        setFile("Java.java").toResource("java/googlejavaformat/JavaCodeUnformatted.test");

        SpotlessCLIRunner.Result result = cliRunner()
                .withTargets("*.java")
                .withStep(GoogleJavaFormat.class)
                .run();

        selfie().expectResource("Java.java").toMatchDisk();
    }

    @Test
    void formattingWithAOSPStyleWorks() throws IOException {
        setFile("Java.java").toResource("java/googlejavaformat/JavaCodeUnformatted.test");

        SpotlessCLIRunner.Result result = cliRunner()
                .withTargets("*.java")
                .withStep(GoogleJavaFormat.class)
                .withOption("--style=aosp")
                .run();

        selfie().expectResource("Java.java").toMatchDisk();
    }

    @Test
    void disablingFormattingJavadocWithGoogleJavaFormatWorks() throws IOException {
        setFile("Java.java").toResource("java/googlejavaformat/JavaCodeUnformatted.test");

        SpotlessCLIRunner.Result result = cliRunner()
                .withTargets("*.java")
                .withStep(GoogleJavaFormat.class)
                .withOption("--format-javadoc=false")
                .run();

        selfie().expectResource("Java.java").toMatchDisk();
    }

    @Test
    void reflowLongStringsWithGoogleJavaFormatWorks() throws IOException {
        setFile("Java.java").toResource("java/googlejavaformat/JavaCodeUnformatted.test");

        SpotlessCLIRunner.Result result = cliRunner()
                .withTargets("*.java")
                .withStep(GoogleJavaFormat.class)
                .withOption("--reflow-long-strings=true")
                .run();

        selfie().expectResource("Java.java").toMatchDisk();
    }

    @Test
    void reorderImportsWithGoogleJavaFormatWorks() throws IOException {
        setFile("Java.java").toResource("java/googlejavaformat/JavaCodeUnformatted.test");

        SpotlessCLIRunner.Result result = cliRunner()
                .withTargets("*.java")
                .withStep(GoogleJavaFormat.class)
                .withOption("--reorder-imports=true")
                .run();

        selfie().expectResource("Java.java").toMatchDisk();
    }
}
