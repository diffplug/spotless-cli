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

import java.nio.file.Path;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.diffplug.spotless.FormatterStep;
import com.diffplug.spotless.cli.core.SpotlessActionContext;
import com.diffplug.spotless.cli.help.OptionConstants;
import com.diffplug.spotless.extra.EclipseBasedStepBuilder;
import com.diffplug.spotless.extra.wtp.EclipseWtpFormatterStep;

import picocli.CommandLine;

@CommandLine.Command(
        name = "eclipse-wtp",
        description = "Runs Eclipse WTP formatter (" + EclipseWtp.ECLIPSE_WTP_VERSION + ")")
public class EclipseWtp extends SpotlessFormatterStep {

    public static final String ECLIPSE_WTP_VERSION = "4.21.0"; // TODO we need to slurp in the lock file also

    @CommandLine.Option(
            names = {"-f", "--config-file"},
            arity = "0",
            description =
                    "The path to the Eclipse WTP configuration file.\n"
                            + "For supported config file options see <https://github.com/diffplug/spotless/tree/main/plugin-gradle#eclipse-web-tools-platform>")
    List<Path> configFiles;

    @CommandLine.Option(
            names = {"-t", "--type"},
            description = "The type of the Eclipse WTP formatter." + OptionConstants.VALID_VALUES_SUFFIX,
            required = true)
    Type type; // TODO: might trying inferring type

    public enum Type {
        CSS(EclipseWtpFormatterStep.CSS),
        HTML(EclipseWtpFormatterStep.HTML),
        JS(EclipseWtpFormatterStep.JS),
        JSON(EclipseWtpFormatterStep.JSON),
        XML(EclipseWtpFormatterStep.XML),
        XHTML(EclipseWtpFormatterStep.HTML); // XHTML is treated as HTML in Eclipse WTP

        private final EclipseWtpFormatterStep backendEclipseWtpType;

        Type(EclipseWtpFormatterStep backendEclipseWtpType) {
            this.backendEclipseWtpType = backendEclipseWtpType;
        }

        public @NotNull EclipseWtpFormatterStep toEclipseWtpType() {
            return this.backendEclipseWtpType;
        }
    }

    @Override
    public @NotNull List<FormatterStep> prepareFormatterSteps(SpotlessActionContext context) {
        EclipseWtpFormatterStep wtpType = type.toEclipseWtpType();
        EclipseBasedStepBuilder builder = wtpType.createBuilder(context.provisioner());
        builder.setVersion(ECLIPSE_WTP_VERSION);
        if (configFiles != null && !configFiles.isEmpty()) {
            builder.setPreferences(configFiles.stream()
                    .map(context::resolvePath)
                    .map(Path::toFile)
                    .toList());
        }
        return List.of(builder.build());
    }
}
