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

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;

import com.diffplug.spotless.cli.CLIIntegrationHarness;
import com.diffplug.spotless.cli.SpotlessCLIRunner;
import com.diffplug.spotless.tag.CliProcessNpmTest;
import com.diffplug.spotless.tag.NpmTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.condition.OS.WINDOWS;

@NpmTest
@CliProcessNpmTest
public class PrettierTest extends CLIIntegrationHarness {

    @Test
    void itRunsPrettierForTsFilesWithOptions() throws IOException {
        setFile("test.ts").toResource("npm/prettier/config/typescript.dirty");

        SpotlessCLIRunner.Result result = cliRunner()
                .withTargets("test.ts")
                .withStep(Prettier.class)
                .withOption("--prettier-config-option", "printWidth=20")
                .withOption("--prettier-config-option", "parser=typescript")
                .run();

        selfie().expectResource("test.ts").toMatchDisk();
    }

    @Test
    void itRunsPrettierForTsFilesWithOptionFile() throws Exception {
        setFile(".prettierrc.yml").toResource("npm/prettier/config/.prettierrc.yml");
        setFile("test.ts").toResource("npm/prettier/config/typescript.dirty");

        SpotlessCLIRunner.Result result = cliRunner()
                .withTargets("test.ts")
                .withStep(Prettier.class)
                .withOption("--prettier-config-path", ".prettierrc.yml")
                .run();

        selfie().expectResource("test.ts").toMatchDisk();
    }

    @Test
    void itRunsPrettierWithoutAnyOptions() throws IOException {
        setFile("test.ts").toResource("npm/prettier/config/typescript.dirty");

        SpotlessCLIRunner.Result result =
                cliRunner().withTargets("test.ts").withStep(Prettier.class).run();

        selfie().expectResource("test.ts").toMatchDisk();
    }

    @Test
    void itRunsSpecificPrettierVersion2x() throws IOException {
        setFile("test.ts").toResource("npm/prettier/config/typescript.dirty");

        SpotlessCLIRunner.Result result = cliRunner()
                .withTargets("test.ts")
                .withStep(Prettier.class)
                .withOption("--dev-dependency", "prettier=2.8.7")
                .run();

        selfie().expectResource("test.ts").toMatchDisk();
    }

    @Test
    void itUsesACacheDir() throws IOException {
        setFile("test.ts").toResource("npm/prettier/config/typescript.dirty");

        File cacheDir = newFolder("cachedir");
        SpotlessCLIRunner.Result result = cliRunner()
                .withTargets("test.ts")
                .withStep(Prettier.class)
                .withOption("--npm-install-cache-dir", cacheDir.getPath())
                .run();

        selfie().expectResource("test.ts").toMatchDisk();

        assertThat(cacheDir).isNotEmptyDirectory();
    }

    @Test
    @DisabledOnOs(WINDOWS)
    void itUsesNpmExec() {
        setFile("test.ts").toResource("npm/prettier/config/typescript.dirty");
        File npmOut = newFile("npmoutput.txt");
        File npm = setFile("npm")
                .toContent(
                        """
            #!/bin/sh
            echo "npm exec" > %s
            exit -99
            """
                                .formatted(npmOut.getAbsolutePath()))
                .makeExecutable()
                .getFile();

        File nodeOut = newFile("nodeoutput.txt");
        setFile("node")
                .toContent(
                        """
            #!/bin/sh
            echo "node exec" > %s
            exit -100
            """
                                .formatted(nodeOut.getAbsolutePath()))
                .makeExecutable();

        SpotlessCLIRunner.Result result = cliRunner()
                .withTargets("test.ts")
                .withStep(Prettier.class)
                .withOption("--npm-exec", npm.getPath())
                .runAndFail();

        selfie().expectFile(npmOut).toBe("""
npm exec
""");
        selfie().expectFile(nodeOut).toBe("");
    }

    @Test
    @DisabledOnOs(WINDOWS)
    void itUsesNodeExec() {
        setFile("test.ts").toResource("npm/prettier/config/typescript.dirty");
        File npmOut = newFile("npmoutput.txt");
        setFile("npm")
                .toContent(
                        """
            #!/bin/sh
            echo "npm exec" > %s
            exit -99
            """
                                .formatted(npmOut.getAbsolutePath()))
                .makeExecutable();

        File nodeOut = newFile("nodeoutput.txt");
        File node = setFile("node")
                .toContent(
                        """
            #!/bin/sh
            echo "node exec" > %s
            exit -100
            """
                                .formatted(nodeOut.getAbsolutePath()))
                .makeExecutable()
                .getFile();

        SpotlessCLIRunner.Result result = cliRunner()
                .withTargets("test.ts")
                .withStep(Prettier.class)
                .withOption("--node-exec", node.getPath())
                .runAndFail();

        selfie().expectFile(npmOut).toBe("""
npm exec
""");
        selfie().expectFile(nodeOut).toBe("");
    }

    @Test
    void itUsesNpmrcFile() {
        setFile("test.ts").toResource("npm/prettier/config/typescript.dirty");

        File npmrc = setFile(".custom_npmrc")
                .toLines(
                        "registry=https://i.do.not.exist.com",
                        "fetch-timeout=250",
                        "fetch-retry-mintimeout=250",
                        "fetch-retry-maxtimeout=250")
                .getFile();

        SpotlessCLIRunner.Result result = cliRunner()
                .withTargets("test.ts")
                .withStep(Prettier.class)
                .withOption("--npmrc-file", npmrc.getPath())
                .runAndFail();

        assertThat(result.exitCode()).isNotZero();
        assertThat(result.stdErr()).containsPattern(".*Running npm command.*NpmInstall.* failed with exit code: 1");
    }

    @Test
    void itUsesNpmrcFileInAdditionalLocation() throws IOException {
        setFile("test.ts").toResource("npm/prettier/config/typescript.dirty");

        File additionalLocation = newFolder("additionalLocation");

        File npmrc = setFile(additionalLocation.getName() + "/.npmrc")
                .toLines(
                        "registry=https://i.do.not.exist.com",
                        "fetch-timeout=250",
                        "fetch-retry-mintimeout=250",
                        "fetch-retry-maxtimeout=250")
                .getFile();

        SpotlessCLIRunner.Result result = cliRunner()
                .withTargets("test.ts")
                .withStep(Prettier.class)
                .withOption("--additional-npmrc-location", additionalLocation.getPath())
                .runAndFail();

        assertThat(result.exitCode()).isNotZero();
        assertThat(result.stdErr()).containsPattern(".*Running npm command.*NpmInstall.* failed with exit code: 1");
    }
}
