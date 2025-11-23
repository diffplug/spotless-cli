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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import com.diffplug.spotless.cli.CLIIntegrationHarness;
import com.diffplug.spotless.cli.SpotlessCLIRunner;
import com.diffplug.spotless.tag.CliProcessTest;

@CliProcessTest
public class PalantirJavaFormatTest extends CLIIntegrationHarness {

    @Test
    void itFormatsWithDefaultOptions() {
        setFile("Java.java").toResource("java/palantirjavaformat/JavaCodeWithJavaDocUnformatted.test");

        SpotlessCLIRunner.Result result = cliRunner()
                .withTargets("*.java")
                .withStep(PalantirJavaFormat.class)
                .run();

        selfie().expectResource("Java.java").toMatchDisk();
    }

    @ParameterizedTest
    @EnumSource(PalantirJavaFormat.Style.class)
    void itFormatsWithSelectedStyle(PalantirJavaFormat.Style style) {
        setFile("Java.java").toResource("java/palantirjavaformat/JavaCodeWithJavaDocUnformatted.test");

        SpotlessCLIRunner.Result result = cliRunner()
                .withTargets("*.java")
                .withStep(PalantirJavaFormat.class)
                .withOption("--style", style.name())
                .run();

        selfie().expectResource("Java.java").toMatchDisk(style.name());
    }

    @Test
    void itFormatsJavadoc() {
        setFile("Java.java").toResource("java/palantirjavaformat/JavaCodeWithJavaDocUnformatted.test");

        SpotlessCLIRunner.Result result = cliRunner()
                .withTargets("*.java")
                .withStep(PalantirJavaFormat.class)
                .withOption("--format-javadoc", "true")
                .run();

        selfie().expectResource("Java.java").toMatchDisk();
    }

    @Test
    void itFormatsTextBlocks() {
        setFile("Java.java").toResource("java/palantirjavaformat/TextBlock.dirty");

        SpotlessCLIRunner.Result result = cliRunner()
                .withTargets("*.java")
                .withStep(PalantirJavaFormat.class)
                .run();

        selfie().expectResource("Java.java").toMatchDisk();
    }
}
