package com.diffplug.spotless.cli.steps;

import com.diffplug.spotless.cli.CLIIntegrationHarness;
import com.diffplug.spotless.tag.CliNativeTest;
import com.diffplug.spotless.tag.CliProcessTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@CliProcessTest
@CliNativeTest
class RemoveUnusedImportsTest extends CLIIntegrationHarness {

    @Test
    void itRemovesUnusedImportsWithDefaultEngine() {
        setFile("Java.java").toResource("java/removeunusedimports/JavaCodeWithLicensePackageUnformatted.test");

        cliRunner()
                .withTargets("Java.java")
                .withStep(RemoveUnusedImports.class)
                .run();

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
