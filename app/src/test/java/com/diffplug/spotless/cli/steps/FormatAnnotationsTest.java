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

import com.diffplug.spotless.cli.CLIIntegrationHarness;

public class FormatAnnotationsTest extends CLIIntegrationHarness {

    @Test
    void itFormatsAnnotations() {
        setFile("Test.java").toResource("java/formatannotations/FormatAnnotationsTestInput.test");

        cliRunner().withTargets("Test.java").withStep(FormatAnnotations.class).run();

        assertFile("Test.java").notSameSasResource("java/formatannotations/FormatAnnotationsTestInput.test");
        selfie().expectResource("Test.java").toMatchDisk();
    }

    @Test
    void itHandlesAnnotationsInComments() {
        setFile("Test.java").toResource("java/formatannotations/FormatAnnotationsInCommentsInput.test");

        cliRunner().withTargets("Test.java").withStep(FormatAnnotations.class).run();

        assertFile("Test.java").notSameSasResource("java/formatannotations/FormatAnnotationsInCommentsInput.test");
        selfie().expectResource("Test.java").toMatchDisk();
    }

    @Test
    void itWorksWithAddingAndRemovingCommentsAsRepeatedOption() {
        setFile("Test.java").toResource("java/formatannotations/FormatAnnotationsAddRemoveInput.test");

        cliRunner()
                .withTargets("Test.java")
                .withStep(FormatAnnotations.class)
                .withOption("--add-type-annotation", "Empty")
                .withOption("--add-type-annotation", "NonEmpty")
                .withOption("--remove-type-annotation", "Localized")
                .run();

        assertFile("Test.java").notSameSasResource("java/formatannotations/FormatAnnotationsAddRemoveInput.test");
        selfie().expectResource("Test.java").toMatchDisk();
    }

    @Test
    void itWorksWithAddingAndRemovingCommentsAsSingleOption() {
        setFile("Test.java").toResource("java/formatannotations/FormatAnnotationsAddRemoveInput.test");

        cliRunner()
                .withTargets("Test.java")
                .withStep(FormatAnnotations.class)
                .withOption("--add-type-annotation", "Empty,NonEmpty")
                .withOption("--remove-type-annotation", "Localized")
                .run();

        assertFile("Test.java").notSameSasResource("java/formatannotations/FormatAnnotationsAddRemoveInput.test");
        selfie().expectResource("Test.java").toMatchDisk();
    }
}
