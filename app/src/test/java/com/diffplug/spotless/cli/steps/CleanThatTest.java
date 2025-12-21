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
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.diffplug.spotless.cli.CLIIntegrationHarness;
import com.diffplug.spotless.tag.CliProcessTest;

@CliProcessTest
public class CleanThatTest extends CLIIntegrationHarness {

    @Test
    void itRunsWithDefaultOptions() {
        setFile("Test.java").toResource("java/cleanthat/MultipleMutators.dirty.test");

        cliRunner().withTargets("Test.java").withStep(CleanThat.class).run();

        assertFile("Test.java").notSameSasResource("java/cleanthat/MultipleMutators.dirty.test");
        selfie().expectResource("Test.java").toMatchDisk();
    }

    @Test
    void itLetsDisableDefaultMutators() {
        setFile("Test.java").toResource("java/cleanthat/MultipleMutators.dirty.test");

        cliRunner()
                .withTargets("Test.java")
                .withStep(CleanThat.class)
                .withOption("--use-default-mutators", "false")
                .run();

        assertFile("Test.java").sameAsResource("java/cleanthat/MultipleMutators.dirty.test");
    }

    @Test
    void itLetsEnableSpecificMutators() {
        setFile("Test.java").toResource("java/cleanthat/MultipleMutators.dirty.test");

        cliRunner()
                .withTargets("Test.java")
                .withStep(CleanThat.class)
                .withOption("--use-default-mutators", "false")
                .withOption("--add-mutator", "LiteralsFirstInComparisons")
                .run();

        assertFile("Test.java").notSameSasResource("java/cleanthat/MultipleMutators.dirty.test");
        selfie().expectResource("Test.java").toMatchDisk();
    }

    @Test
    void itLetsDisableSpecificMutators() {
        setFile("Test.java").toResource("java/cleanthat/MultipleMutators.dirty.test");

        cliRunner()
                .withTargets("Test.java")
                .withStep(CleanThat.class)
                .withOption("--exclude-mutator", "StreamAnyMatch")
                .run();

        assertFile("Test.java").sameAsResource("java/cleanthat/MultipleMutators.dirty.test");
    }

    @Test
    void itLetsEnableDraftMutators() throws IOException {
        File file1 = setFile("Test.java")
                .toResource("java/cleanthat/MultipleMutators.dirty.test")
                .getFile();
        File file2 = setFile("Test2.java")
                .toResource("java/cleanthat/MultipleMutators.dirty.test")
                .getFile();

        cliRunner()
                .withTargets("Test.java")
                .withStep(CleanThat.class)
                .withOption("--add-mutator", "RemoveAllToClearCollection")
                .run();

        var result = cliRunner()
                .withTargets("Test2.java")
                .withStep(CleanThat.class)
                .withOption("--include-draft-mutators", "true")
                .withOption("--add-mutator", "RemoveAllToClearCollection")
                .run();

        assertFile("Test.java").notSameSasResource("java/cleanthat/MultipleMutators.dirty.test");
        selfie().expectResource("Test.java").toMatchDisk("excluding draft mutators");
        selfie().expectResource("Test2.java").toMatchDisk("including draft mutators");

        //        these outcomes should be different, but they are not, problably a upstream issue in CleanThat
        //        assertFile(file1).notSameAsFile(file2);
    }

    @Test
    void itCanExecuteAllMutators() {
        setFile("Test.java").toResource("java/cleanthat/MultipleMutators.dirty.test");

        cliRunner()
                .withTargets("Test.java")
                .withStep(CleanThat.class)
                .withOption("--add-mutator", "AllIncludingDraftSingleMutators")
                .withOption("--include-draft-mutators", "true")
                .run();

        selfie().expectResource("Test.java").toMatchDisk();
    }

    @Test
    @Disabled("Runs for a long time when using 2.14 version of CleanThat, not suitable for regular tests")
    void itLetsSelectCustomVersion() {
        setFile("Test.java").toResource("java/cleanthat/StringFromString.dirty.test");

        // StringFromString has been added in CleanThat 2.15, so should not work before then
        cliRunner()
                .withTargets("Test.java")
                .withStep(CleanThat.class)
                .withOption("--add-mutator", "StringFromString")
                .run();

        assertFile("Test.java").notSameSasResource("java/cleanthat/StringFromString.dirty.test");

        setFile("Test2.java").toResource("java/cleanthat/StringFromString.dirty.test");

        cliRunner()
                .withTargets("Test2.java")
                .withStep(CleanThat.class)
                .withOption("--add-mutator", "StringFromString")
                .withOption("--use-version", "2.14")
                .run();

        assertFile("Test2.java").sameAsResource("java/cleanthat/StringFromString.dirty.test");
    }

    @Test
    void itSelectsCustomVersion() {
        // bug https://github.com/solven-eu/cleanthat/issues/897 has been fixed in 2.24, so selecting 2.23 should yield
        // no fix
        setFile("Test.java").toResource("java/cleanthat/ModifierOrderBug.dirty.test");

        cliRunner()
                .withTargets("Test.java")
                .withStep(CleanThat.class)
                .withOption("--add-mutator", "ModifierOrder")
                .run();

        selfie().expectResource("Test.java").toMatchDisk();
        assertFile("Test.java").notHasContent("Deprecatedprivate", StandardCharsets.UTF_8);

        setFile("Test2.java").toResource("java/cleanthat/ModifierOrderBug.dirty.test");

        cliRunner()
                .withTargets("Test2.java")
                .withStep(CleanThat.class)
                .withOption("--add-mutator", "ModifierOrder")
                .withOption("--use-version", "2.23")
                .run();
        assertFile("Test2.java").hasContent("Deprecatedprivate", StandardCharsets.UTF_8);
    }
}
