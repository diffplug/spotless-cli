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
import com.diffplug.spotless.tag.CliProcessTest;

@CliProcessTest
class RemoveUnusedImportsTest extends CLIIntegrationHarness {

    @Test
    void itRemovesUnusedImportsWithDefaultEngine() {
        setFile("Java.java").toResource("java/removeunusedimports/JavaCodeWithLicensePackageUnformatted.test");

        cliRunner().withTargets("Java.java").withStep(RemoveUnusedImports.class).run();

        assertFile("Java.java")
                .notSameSasResource("java/removeunusedimports/JavaCodeWithLicensePackageUnformatted.test")
                .hasNotContent("Unused");

        selfie().expectResource("Java.java").toMatchDisk();
    }

    @Test
    void itRemovesWithExplicitDefaultEngine() {
        setFile("Java.java").toResource("java/removeunusedimports/JavaCodeWithLicensePackageUnformatted.test");

        cliRunner()
                .withTargets("Java.java")
                .withStep(RemoveUnusedImports.class)
                .withOption("--engine", "GOOGLE_JAVA_FORMAT")
                .run();

        assertFile("Java.java")
                .notSameSasResource("java/removeunusedimports/JavaCodeWithLicensePackageUnformatted.test")
                .hasNotContent("Unused");

        selfie().expectResource("Java.java").toMatchDisk();
    }

    @Test
    void itRemovesWithExplicitCleanThatEngine() {
        setFile("Java.java").toResource("java/removeunusedimports/JavaCodeWithLicensePackageUnformatted.test");

        cliRunner()
                .withTargets("Java.java")
                .withStep(RemoveUnusedImports.class)
                .withOption("--engine", "CLEAN_THAT")
                .run();

        assertFile("Java.java")
                .notSameSasResource("java/removeunusedimports/JavaCodeWithLicensePackageUnformatted.test")
                .hasNotContent("Unused");

        selfie().expectResource("Java.java").toMatchDisk();
    }
}
