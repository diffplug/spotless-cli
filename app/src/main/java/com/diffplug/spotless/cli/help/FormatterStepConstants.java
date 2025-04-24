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

public final class FormatterStepConstants {

    // multiline
    public static final String SUPPORTED_FILETYPES_HELP_SECTION_HEADING_MULTILINE_KEY =
            "formatterstep.supported.filetypes.help.section.heading.multiline";
    public static final String SUPPORTED_FILETYPES_HELP_SECTION_HEADING_MULTILINE =
            "%n✅ This step supports the following @|bold file types|@:%n";
    public static final String SUPPORTED_FILETYPES_HELP_SECTION_DETAIL_MULTILINE_KEY =
            "formatterstep.supported.filetypes.help.section.detail";

    // singleline
    public static final String SUPPORTED_FILETYPES_HELP_SECTION_HEADING_SINGLELINE_KEY =
            "formatterstep.supported.filetypes.help.section.entry";
    public static final String SUPPORTED_FILETYPES_HELP_SECTION_HEADING_SINGLELINE =
            "%n✅ This step supports the following @|bold file type|@: ";

    public static final String SUPPORTED_FILETYPES_INTRO =
            "✅ The following @|yellow file types|@ are supported by this formatter step:" + OptionConstants.NEW_LINE;

    public static final String HOMEPAGE = "🌎 Homepage: ";

    public static final String ADDITIONAL_INFO_LINKS_SECTION_HEADING_KEY =
            "formatterstep.additional.infolinks.section.heading";

    public static final String ADDITIONAL_INFO_LINKS_SECTION_HEADING = "%n🌎 Additional info:%n";

    public static final String ADDITIONAL_INFO_LINKS_SECTION_DETAIL_KEY =
            "formatterstep.additional.infolinks.section.detail";

    private FormatterStepConstants() {
        // no instance
    }
}
