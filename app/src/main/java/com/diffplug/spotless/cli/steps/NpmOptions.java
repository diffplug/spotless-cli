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

import java.nio.file.Path;
import java.util.List;

import picocli.CommandLine;

public class NpmOptions {
    @CommandLine.Option(
            names = {"--npm-install-cache-dir", "-C"},
            description = "The directory to use for caching libraries retrieved by @|YELLOW 'npm install'|@.")
    Path npmInstallCacheDir;

    @CommandLine.Option(
            names = {"--npm-exec", "-n"},
            description = "The explicit path to the npm executable.")
    Path explicitNpmExecutable;

    @CommandLine.Option(
            names = {"--node-exec", "-N"},
            description = "The explicit path to the node executable.")
    Path explicitNodeExecutable;

    @CommandLine.Option(
            names = {"--npmrc-file", "-R"},
            description = "The explicit path to the .npmrc file.")
    Path explicitNpmrcFile;

    @CommandLine.Option(
            names = {"--additional-npmrc-location", "-A"},
            description = "Additional locations to search for .npmrc files.")
    List<Path> additionalNpmrcLocations;
}
