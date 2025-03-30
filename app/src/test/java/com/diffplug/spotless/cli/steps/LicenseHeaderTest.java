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

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.diffplug.spotless.cli.CLIIntegrationHarness;
import com.diffplug.spotless.cli.SpotlessCLIRunner;
import com.diffplug.spotless.generic.LicenseHeaderStep;

import static org.assertj.core.api.Assertions.assertThat;

public class LicenseHeaderTest extends CLIIntegrationHarness {

    @Test
    void assertHeaderMustBeSpecified() {
        SpotlessCLIRunner.Result result = cliRunner()
                .withTargets("**/*.java")
                .withStep(LicenseHeader.class)
                .runAndFail();

        assertThat(result.stdErr()).containsPattern(".*Missing required.*header.*");
    }

    @Test
    void assertHeaderIsApplied() {
        setFile("TestFile.java").toContent("public class TestFile {}");

        SpotlessCLIRunner.Result result = cliRunner()
                .withTargets("TestFile.java")
                .withStep(LicenseHeader.class)
                .withOption("--header", "/* License */")
                .run();

        selfie().expectResource("TestFile.java").toBe("""
/* License */
public class TestFile {}""");
    }

    @Test
    void assertHeaderFileIsApplied() {
        setFile("TestFile.java").toContent("public class TestFile {}");
        setFile("header.txt").toContent("/* License */");

        SpotlessCLIRunner.Result result = cliRunner()
                .withTargets("TestFile.java")
                .withStep(LicenseHeader.class)
                .withOption("--header-file", "header.txt")
                .run();

        selfie().expectResource("TestFile.java").toBe("""
/* License */
public class TestFile {}""");
    }

    @Test
    void assertDelimiterIsApplied() {
        setFile("TestFile.java").toContent("/* keep me */\npublic class TestFile {}");

        SpotlessCLIRunner.Result result = cliRunner()
                .withTargets("TestFile.java")
                .withStep(LicenseHeader.class)
                .withOption("--header", "/* License */")
                .withOption("--delimiter", "\\/\\* keep me")
                .run();

        selfie().expectResource("TestFile.java").toBe("""
/* License */
/* keep me */
public class TestFile {}""");
    }

    @Test
    void assertYearModeIsApplied() {
        setFile("TestFile.java").toContent("/* License (c) 2022 */\npublic class TestFile {}");

        SpotlessCLIRunner.Result result = cliRunner()
                .withTargets("TestFile.java")
                .withStep(LicenseHeader.class)
                .withOption("--header", "/* License (c) $YEAR */")
                .withOption("--year-mode", LicenseHeaderStep.YearMode.UPDATE_TO_TODAY.toString())
                .run();

        assertFile("TestFile.java")
                .hasContent("/* License (c) 2022-" + LocalDate.now().getYear() + " */\npublic class TestFile {}");
    }

    @Test
    void assertYearSeparatorIsApplied() {
        setFile("TestFile.java").toContent("/* License (c) 2022...2023 */\npublic class TestFile {}");

        SpotlessCLIRunner.Result result = cliRunner()
                .withTargets("TestFile.java")
                .withStep(LicenseHeader.class)
                .withOption("--header", "/* License (c) $YEAR */")
                .withOption("--year-mode", LicenseHeaderStep.YearMode.UPDATE_TO_TODAY.toString())
                .withOption("--year-separator", "...")
                .run();

        assertFile("TestFile.java")
                .hasContent("/* License (c) 2022..." + LocalDate.now().getYear() + " */\npublic class TestFile {}");
    }

    @Test
    void assertSkipLinesMatchingIsApplied() {
        setFile("TestFile.java").toContent("/* skip me */\npublic class TestFile {}");

        SpotlessCLIRunner.Result result = cliRunner()
                .withTargets("TestFile.java")
                .withStep(LicenseHeader.class)
                .withOption("--header", "/* License */")
                .withOption("--skip-lines-matching", ".*skip me.*")
                .run();

        selfie().expectResource("TestFile.java").toBe("""
/* skip me */
/* License */
public class TestFile {}
""");
    }

    @Test
    void assertPreserveModeIsApplied() {
        setFile("TestFile.java").toContent("/* License (c) 2022 */\npublic class TestFile {}");

        SpotlessCLIRunner.Result result = cliRunner()
                .withTargets("TestFile.java")
                .withStep(LicenseHeader.class)
                .withOption("--header", "/* License (c) $YEAR */")
                .withOption("--year-mode", LicenseHeaderStep.YearMode.PRESERVE.toString())
                .run();

        selfie().expectResource("TestFile.java").toBe("""
/* License (c) 2022 */
public class TestFile {}""");
    }

    @Test
    void assertContentPatternIsAppliedIfMatching() {
        setFile("TestFile.java").toContent("public class TestFile {}");

        SpotlessCLIRunner.Result result = cliRunner()
                .withTargets("TestFile.java")
                .withStep(LicenseHeader.class)
                .withOption("--header", "/* License */")
                .withOption("--content-pattern", ".*TestFile.*")
                .run();

        selfie().expectResource("TestFile.java").toBe("""
/* License */
public class TestFile {}""");
    }

    @Test
    void assertContentPatternIsNotAppliedIfNotMatching() {
        setFile("TestFile.java").toContent("public class TestFile {}");

        SpotlessCLIRunner.Result result = cliRunner()
                .withTargets("TestFile.java")
                .withStep(LicenseHeader.class)
                .withOption("--header", "/* License */")
                .withOption("--content-pattern", ".*NonExistent.*")
                .run();

        selfie().expectResource("TestFile.java").toBe("public class TestFile {}");
    }
}
