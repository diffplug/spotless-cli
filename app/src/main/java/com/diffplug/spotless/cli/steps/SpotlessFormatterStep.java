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

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.diffplug.spotless.FormatterStep;
import com.diffplug.spotless.cli.core.SpotlessActionContext;
import com.diffplug.spotless.cli.help.SpotlessFormatterStepHelpRenderer;

import picocli.CommandLine;

@CommandLine.Command(mixinStandardHelpOptions = true, usageHelpAutoWidth = true)
public abstract class SpotlessFormatterStep implements SpotlessCLIFormatterStep {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpotlessFormatterStep.class);

    @CommandLine.Spec
    private CommandLine.Model.CommandSpec spec;

    @CommandLine.Option(
            names = "--xxxabbbaaaddd", // random value, just to make it unique
            hidden = true,
            defaultValue = "true",
            description = "just a hook to be invoked so we can modify the help section in the spec.commandLine()")
    private void setSupportedFileTypes(boolean ignore) {
        SpotlessFormatterStepHelpRenderer helpRenderer = new SpotlessFormatterStepHelpRenderer(this);
        if (!helpRenderer.addSupportedFileTypesSection(spec)) {
            LOGGER.debug(
                    "Adding supported file types for step {} failed",
                    this.getClass().getSimpleName());
        }
        if (!helpRenderer.addAdditionalInfoLinksSection(spec)) {
            LOGGER.debug(
                    "Adding additional info links for step {} failed",
                    this.getClass().getSimpleName());
        }
    }

    @NotNull @Override
    public List<FormatterStep> prepareFormatterSteps(SpotlessActionContext context) {
        return prepareFormatterSteps();
    }

    protected List<FormatterStep> prepareFormatterSteps() {
        throw new IllegalStateException("This method must be overridden or not be called");
    }
}
