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

import com.diffplug.spotless.cli.CLIIntegrationHarness;
import com.diffplug.spotless.cli.SpotlessCLIRunner;

public abstract class EclipseWtpTestBase extends CLIIntegrationHarness {

    protected String runEclipseWtpWithType(EclipseWtp.Type type, String unformatted) {
        String fileName = "test." + type.name().toLowerCase();
        setFile(fileName).toContent(unformatted);

        SpotlessCLIRunner.Result result = cliRunner()
                .withTargets(fileName)
                .withStep(EclipseWtp.class)
                .withOption("--type", type.name())
                .run();

        return fileName;
    }

    protected String runEclipseWtpWithTypeInferred(String fileExtension, String unformatted) {
        String fileName = "test." + fileExtension;
        setFile(fileName).toContent(unformatted);

        SpotlessCLIRunner.Result result =
                cliRunner().withTargets(fileName).withStep(EclipseWtp.class).run();

        return fileName;
    }

    protected String runEclipseWtpWithTypeAndConfigFile(EclipseWtp.Type type, String unformatted, String configFile) {
        String fileName = "test." + type.name().toLowerCase();
        setFile(fileName).toContent(unformatted);
        setFile(configFile).toResource("eclipse-wtp/" + configFile);

        SpotlessCLIRunner.Result result = cliRunner()
                .withTargets(fileName)
                .withStep(EclipseWtp.class)
                .withOption("--type", type.name())
                .withOption("--config-file", configFile)
                .run();

        return fileName;
    }
}
