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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;

import com.diffplug.spotless.FormatterStep;
import com.diffplug.spotless.cli.core.SpotlessActionContext;
import com.diffplug.spotless.cli.help.AdditionalInfoLinks;
import com.diffplug.spotless.cli.help.OptionConstants;
import com.diffplug.spotless.cli.help.SupportedFileTypes;
import com.diffplug.spotless.java.CleanthatJavaStep;

import picocli.CommandLine;

@CommandLine.Command(name = "clean-that", description = "CleanThat enables automatic refactoring of Java code.")
@SupportedFileTypes("Java")
@AdditionalInfoLinks({
    "https://github.com/solven-eu/cleanthat",
    "https://github.com/solven-eu/cleanthat/blob/master/MUTATORS.generated.MD"
})
public class CleanThat extends SpotlessFormatterStep {

    public static final String DEFAULT_MUTATORS = String.join(", ", CleanthatJavaStep.defaultMutators());

    static {
        // workaround for dynamic property resolution in help messages
        System.setProperty("usage.cleanthat.defaultMutators", DEFAULT_MUTATORS);
    }

    @CommandLine.Option(
            names = {"--use-default-mutators", "-d"},
            defaultValue = "true",
            description =
                    "Use the default mutators provided by CleanThat. Default mutators are: <${usage.cleanthat.defaultMutators}>."
                            + OptionConstants.DEFAULT_VALUE_SUFFIX)
    boolean useDefaultMutators;

    @CommandLine.Option(
            names = {"--add-mutator", "-a"},
            arity = "0..*",
            split = OptionConstants.OPTION_LIST_SPLIT,
            paramLabel = "mutator",
            description =
                    "Add a mutator to the list of mutators to use. Mutators are the individual refactoring steps CleanThat applies. A list of available mutators can be found in the \"Additional Info\" section. ")
    List<String> addMutators;

    @CommandLine.Option(
            names = {"--exclude-mutator", "-e"},
            arity = "0..*",
            split = OptionConstants.OPTION_LIST_SPLIT,
            paramLabel = "mutator",
            description =
                    "Remove a mutator from the list of mutators to use. This might make sense for composite mutators")
    List<String> excludeMutators;

    @CommandLine.Option(
            names = {"--include-draft-mutators", "-D"},
            defaultValue = "false",
            description =
                    "Include draft mutators in the list of mutators to use. Draft mutators are experimental and may not be fully tested or stable."
                            + OptionConstants.DEFAULT_VALUE_SUFFIX)
    boolean includeDraftMutators;

    @CommandLine.Option(
            names = {"--source-compatibility", "-s"},
            defaultValue = "1.8",
            description =
                    "The source JDK version to use for the CleanThat mutators. This is used to determine the Java language features available."
                            + OptionConstants.DEFAULT_VALUE_SUFFIX)
    String sourceCompatibility;

    @Override
    public @NotNull List<FormatterStep> prepareFormatterSteps(SpotlessActionContext context) {
        return Collections.singletonList(CleanthatJavaStep.create(
                CleanthatJavaStep.defaultGroupArtifact(),
                CleanthatJavaStep.defaultVersion(),
                this.sourceCompatibility,
                includedMutators(),
                excludedMutators(),
                this.includeDraftMutators,
                context.provisioner()));
    }

    private List<String> includedMutators() {
        List<String> mutators = new ArrayList<>();
        if (useDefaultMutators) {
            mutators.addAll(CleanthatJavaStep.defaultMutators());
        }
        if (addMutators != null) {
            mutators.addAll(addMutators.stream()
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList());
        }
        return mutators;
    }

    private List<String> excludedMutators() {
        if (excludeMutators == null) {
            return Collections.emptyList();
        }
        return excludeMutators.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }
}
