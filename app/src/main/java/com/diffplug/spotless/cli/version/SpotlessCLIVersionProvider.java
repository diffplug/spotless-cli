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
package com.diffplug.spotless.cli.version;

import java.util.Properties;

import picocli.CommandLine;

public class SpotlessCLIVersionProvider implements CommandLine.IVersionProvider {

    @Override
    public String[] getVersion() throws Exception {
        // load application.properties
        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream("/application.properties"));
        String version = properties.getProperty("cli.version");
        String libVersion = properties.getProperty("lib.version");
        String libExtraVersion = properties.getProperty("lib.extra.version");
        String line = "-".repeat(50);
        return """
                %1$s
                🧼 Spotless CLI %2$s
                %1$s

                spotless-lib:       %3$s
                spotless-lib-extra: %4$s
                """
                .formatted(line, version, libVersion, libExtraVersion)
                .split("\n");
    }
}
