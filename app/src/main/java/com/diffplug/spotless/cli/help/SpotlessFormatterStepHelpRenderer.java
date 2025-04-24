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
package com.diffplug.spotless.cli.help;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;

import com.diffplug.spotless.cli.steps.SpotlessFormatterStep;

import picocli.CommandLine;

import static com.diffplug.spotless.cli.help.FormatterStepConstants.SUPPORTED_FILETYPES_HELP_SECTION_DETAIL_MULTILINE_KEY;
import static com.diffplug.spotless.cli.help.FormatterStepConstants.SUPPORTED_FILETYPES_HELP_SECTION_HEADING_MULTILINE_KEY;
import static com.diffplug.spotless.cli.help.FormatterStepConstants.SUPPORTED_FILETYPES_HELP_SECTION_HEADING_SINGLELINE;
import static com.diffplug.spotless.cli.help.FormatterStepConstants.SUPPORTED_FILETYPES_HELP_SECTION_HEADING_SINGLELINE_KEY;
import static java.util.Objects.requireNonNull;
import static picocli.CommandLine.Model.UsageMessageSpec.SECTION_KEY_FOOTER_HEADING;

public class SpotlessFormatterStepHelpRenderer {

    @NotNull private final SpotlessFormatterStep spotlessFormatterStep;

    public SpotlessFormatterStepHelpRenderer(@NotNull SpotlessFormatterStep spotlessFormatterStep) {
        this.spotlessFormatterStep = requireNonNull(spotlessFormatterStep);
    }

    // ---- supported file types

    public boolean addSupportedFileTypesSection(@NotNull CommandLine.Model.CommandSpec commandSpec) {
        requireNonNull(commandSpec);
        if (!hasSupportedFileTypes()) {
            return false;
        }

        if (supportedFileTypes().size() == 1) {
            addOneLinedSupportedFileTypes(commandSpec);
            return true;
        }

        addMultiLinedSupportedFileTypesSection(commandSpec);
        return true;
    }

    public boolean hasSupportedFileTypes() {
        return !supportedFileTypes().isEmpty();
    }

    private List<String> supportedFileTypes() {
        if (spotlessFormatterStep.getClass().isAnnotationPresent(SupportedFileTypes.class)) {
            SupportedFileTypes supportedFileTypes =
                    spotlessFormatterStep.getClass().getAnnotation(SupportedFileTypes.class);
            assert supportedFileTypes != null;
            return List.of(supportedFileTypes.value());
        }
        return Collections.emptyList();
    }

    private void addOneLinedSupportedFileTypes(CommandLine.Model.CommandSpec spec) {
        requireNonNull(spec);
        spec.commandLine()
                .getHelpSectionMap()
                .put(
                        SUPPORTED_FILETYPES_HELP_SECTION_HEADING_SINGLELINE_KEY,
                        help -> help.createHeading(SUPPORTED_FILETYPES_HELP_SECTION_HEADING_SINGLELINE
                                + this.renderSupportedFileTypes(help)));
        addBefore(
                spec.commandLine(),
                SECTION_KEY_FOOTER_HEADING,
                SUPPORTED_FILETYPES_HELP_SECTION_HEADING_SINGLELINE_KEY);
    }

    private void addMultiLinedSupportedFileTypesSection(@NotNull CommandLine.Model.CommandSpec spec) {
        requireNonNull(spec);
        Map<String, CommandLine.IHelpSectionRenderer> helpSectionMap =
                spec.commandLine().getHelpSectionMap();
        helpSectionMap.put(
                SUPPORTED_FILETYPES_HELP_SECTION_HEADING_MULTILINE_KEY,
                help -> help.createHeading(FormatterStepConstants.SUPPORTED_FILETYPES_HELP_SECTION_HEADING_MULTILINE));
        helpSectionMap.put(SUPPORTED_FILETYPES_HELP_SECTION_DETAIL_MULTILINE_KEY, this::renderSupportedFileTypes);

        addBefore(
                spec.commandLine(),
                SECTION_KEY_FOOTER_HEADING,
                SUPPORTED_FILETYPES_HELP_SECTION_HEADING_MULTILINE_KEY,
                SUPPORTED_FILETYPES_HELP_SECTION_DETAIL_MULTILINE_KEY);
    }

    private String renderSupportedFileTypes(CommandLine.Help help) {
        List<String> supportedFileTypes = supportedFileTypes();
        return renderSectionDetail(help, supportedFileTypes);
    }

    // --- additionalInfoLinks

    public boolean addAdditionalInfoLinksSection(@NotNull CommandLine.Model.CommandSpec commandSpec) {
        requireNonNull(commandSpec);
        if (!hasAdditionalInfoLinks()) {
            return false;
        }
        addMultiLinedAdditionalInfoLinksSection(commandSpec);
        return true;
    }

    public boolean hasAdditionalInfoLinks() {
        return !additionalInfoLinks().isEmpty();
    }

    private List<String> additionalInfoLinks() {
        if (spotlessFormatterStep.getClass().isAnnotationPresent(AdditionalInfoLinks.class)) {
            AdditionalInfoLinks additionalInfoLinks =
                    spotlessFormatterStep.getClass().getAnnotation(AdditionalInfoLinks.class);
            assert additionalInfoLinks != null;
            return List.of(additionalInfoLinks.value());
        }
        return Collections.emptyList();
    }

    private void addMultiLinedAdditionalInfoLinksSection(@NotNull CommandLine.Model.CommandSpec spec) {
        requireNonNull(spec);
        Map<String, CommandLine.IHelpSectionRenderer> helpSectionMap =
                spec.commandLine().getHelpSectionMap();
        helpSectionMap.put(
                FormatterStepConstants.ADDITIONAL_INFO_LINKS_SECTION_HEADING_KEY,
                help -> help.createHeading(FormatterStepConstants.ADDITIONAL_INFO_LINKS_SECTION_HEADING));
        helpSectionMap.put(
                FormatterStepConstants.ADDITIONAL_INFO_LINKS_SECTION_DETAIL_KEY, this::renderAdditionalInfoLinks);

        addBefore(
                spec.commandLine(),
                SECTION_KEY_FOOTER_HEADING,
                FormatterStepConstants.ADDITIONAL_INFO_LINKS_SECTION_HEADING_KEY,
                FormatterStepConstants.ADDITIONAL_INFO_LINKS_SECTION_DETAIL_KEY);
    }

    private String renderAdditionalInfoLinks(CommandLine.Help help) {
        List<String> additionalInfoLinks = additionalInfoLinks();
        return renderSectionDetail(help, additionalInfoLinks);
    }

    private String renderSectionDetail(CommandLine.Help help, List<String> detailElements) {
        if (detailElements.isEmpty()) {
            return "";
        }
        if (detailElements.size() == 1) {
            return help.createHeading("%s%n", detailElements.getFirst());
        }

        Map<String, String> sectionDetailElementsMap = new LinkedHashMap<>();
        for (String element : detailElements) {
            sectionDetailElementsMap.put(" * " + element, "");
        }
        return help.createTextTable(sectionDetailElementsMap).toString();
    }

    // --- utils

    private void addBefore(
            @NotNull CommandLine commandLine,
            @NotNull String targetSectionKey,
            @NotNull String sectionKeyToAdd,
            @NotNull String... moreSectionKeysToAdd) {
        List<String> keys = new ArrayList<>(requireNonNull(commandLine).getHelpSectionKeys());
        int index = keys.indexOf(requireNonNull(targetSectionKey));
        if (index < 0) {
            throw new NoSuchElementException("Cannot resolve location of " + targetSectionKey);
        }
        keys.addAll(
                index,
                Stream.concat(
                                Stream.of(requireNonNull(sectionKeyToAdd)),
                                Stream.of(requireNonNull(moreSectionKeysToAdd)))
                        .toList());
        commandLine.setHelpSectionKeys(keys);
    }
}
