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
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;

import com.diffplug.spotless.FormatterStep;
import com.diffplug.spotless.cli.core.ExecutionLayout;
import com.diffplug.spotless.cli.core.SpotlessActionContext;
import com.diffplug.spotless.cli.help.FormatterStepConstants;
import com.diffplug.spotless.npm.NpmPathResolver;
import com.diffplug.spotless.npm.PrettierConfig;
import com.diffplug.spotless.npm.PrettierFormatterStep;

import picocli.CommandLine;

import static com.diffplug.spotless.cli.core.FilePathUtil.asFile;
import static com.diffplug.spotless.cli.core.FilePathUtil.asFiles;
import static com.diffplug.spotless.cli.core.FilePathUtil.assertDirectoryExists;
import static com.diffplug.spotless.cli.help.OptionConstants.NEW_LINE;
import static com.diffplug.spotless.cli.steps.OptionDefaultUse.use;

@CommandLine.Command(
        name = "prettier",
        description = "Runs prettier, the opinionated code formatter.",
        footer = {
            "",
            FormatterStepConstants.SUPPORTED_FILETYPES_INTRO + Prettier.SUPPORTED_FILETYPES,
            "",
            FormatterStepConstants.HOMEPAGE + Prettier.HOMEPAGE,
            "",
            "🧩 Find plugins at https://prettier.io/docs/plugins.html#official-plugins"
        })
public class Prettier extends SpotlessFormatterStep {

    public static final String SUPPORTED_FILETYPES =
            "JavaScript, JSX, Angular, Vue, Flow, TypeScript, CSS, Less, SCSS, HTML, Ember/Handlebars, JSON, GraphQL, Markdown, YAML, (and more using plugins)";

    public static final String HOMEPAGE = "https://prettier.io/";

    @CommandLine.Option(
            names = {"--dev-dependency", "-D"},
            description = "An entry to add to the package.json for running prettier." + NEW_LINE
                    + "The format is @|YELLOW 'PACKAGE=VERSION'|@." + NEW_LINE + "example: 'prettier=2.8.7'",
            paramLabel = "'PACKAGE=VERSION'")
    Map<String, String> devDependencies;

    @CommandLine.Mixin
    NpmOptions npmOptions;

    @CommandLine.Option(
            names = {"--prettier-config-path", "-P"},
            description = "The path to the prettier configuration file.")
    Path prettierConfigPath;

    @CommandLine.Option(
            names = {"--prettier-config-option", "-c"},
            description = "A prettier configuration options." + NEW_LINE + "The format is @|YELLOW 'OPTION=VALUE'|@."
                    + NEW_LINE + "example: 'printWidth=80'",
            paramLabel = "'OPTION=VALUE'")
    Map<String, String> prettierConfigOptions;

    @NotNull @Override
    public List<FormatterStep> prepareFormatterSteps(SpotlessActionContext context) {
        FormatterStep prettierFormatterStep = builder(context)
                .withDevDependencies(devDependencies())
                .withCacheDir(npmOptions.npmInstallCacheDir)
                .withExplicitNpmExecutable(npmOptions.explicitNpmExecutable)
                .withExplicitNodeExecutable(npmOptions.explicitNodeExecutable)
                .withExplicitNpmrcFile(npmOptions.explicitNpmrcFile)
                .withAdditionalNpmrcLocations(additionalNpmrcLocations())
                .withPrettierConfigOptions(prettierConfigOptions())
                .withPrettierConfigPath(prettierConfigPath)
                .build();

        return List.of(prettierFormatterStep);
    }

    private Map<String, Object> prettierConfigOptions() {
        if (prettierConfigOptions == null) {
            return Collections.emptyMap();
        }
        Map<String, Object> normalized = new LinkedHashMap<>();
        prettierConfigOptions.forEach((key, value) -> {
            if (value == null) {
                normalized.put(key, null);
            } else {
                normalized.put(key, normalizePrettierOption(value));
            }
        });
        return normalized;
    }

    private Object normalizePrettierOption(String value) {
        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
            return Boolean.parseBoolean(value);
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return value;
        }
    }

    private Map<String, String> devDependencies() {
        return use(devDependencies).orIfNullGet(PrettierFormatterStep::defaultDevDependencies);
    }

    private List<Path> additionalNpmrcLocations() {
        return use(npmOptions.additionalNpmrcLocations).orIfNullGet(Collections::emptyList);
    }

    private PrettierFormatterStepBuilder builder(@NotNull SpotlessActionContext context) {
        return new PrettierFormatterStepBuilder(context);
    }

    private class PrettierFormatterStepBuilder {

        @NotNull private final SpotlessActionContext context;

        private Map<String, String> devDependencies;

        private Path cacheDir = null;

        // npmPathResolver
        private Path explicitNpmExecutable;

        private Path explicitNodeExecutable;

        private Path explicitNpmrcFile;

        private List<Path> additionalNpmrcLocations;

        // prettierConfig

        private Map<String, Object> prettierConfigOptions;

        private Path prettierConfigPath;

        private PrettierFormatterStepBuilder(@NotNull SpotlessActionContext context) {
            this.context = Objects.requireNonNull(context);
        }

        public PrettierFormatterStepBuilder withDevDependencies(Map<String, String> devDependencies) {
            this.devDependencies = devDependencies;
            return this;
        }

        public PrettierFormatterStepBuilder withCacheDir(Path cacheDir) {
            this.cacheDir = cacheDir;
            return this;
        }

        public PrettierFormatterStepBuilder withExplicitNpmExecutable(Path explicitNpmExecutable) {
            this.explicitNpmExecutable = explicitNpmExecutable;
            return this;
        }

        public PrettierFormatterStepBuilder withExplicitNodeExecutable(Path explicitNodeExecutable) {
            this.explicitNodeExecutable = explicitNodeExecutable;
            return this;
        }

        public PrettierFormatterStepBuilder withExplicitNpmrcFile(Path explicitNpmrcFile) {
            this.explicitNpmrcFile = explicitNpmrcFile;
            return this;
        }

        public PrettierFormatterStepBuilder withAdditionalNpmrcLocations(List<Path> additionalNpmrcLocations) {
            this.additionalNpmrcLocations = additionalNpmrcLocations;
            return this;
        }

        public PrettierFormatterStepBuilder withPrettierConfigOptions(Map<String, Object> prettierConfigOptions) {
            this.prettierConfigOptions = prettierConfigOptions;
            return this;
        }

        public PrettierFormatterStepBuilder withPrettierConfigPath(Path prettierConfigPath) {
            this.prettierConfigPath = prettierConfigPath;
            return this;
        }

        public FormatterStep build() {
            ExecutionLayout layout = context.executionLayout();
            File projectDirFile = asFile(layout.find(Path.of("package.json")) // project dir
                    .map(Path::getParent)
                    .orElseGet(layout::baseDir));
            File buildDirFile = asFile(layout.buildDirFor(Prettier.this));
            File cacheDirFile = asFile(cacheDir);
            assertDirectoryExists(projectDirFile, buildDirFile, cacheDirFile);
            FormatterStep step = PrettierFormatterStep.create(
                    use(devDependencies).orIfNullGet(PrettierFormatterStep::defaultDevDependencies),
                    context.provisioner(),
                    projectDirFile,
                    buildDirFile,
                    cacheDirFile,
                    new NpmPathResolver(
                            asFile(explicitNpmExecutable),
                            asFile(explicitNodeExecutable),
                            asFile(explicitNpmrcFile),
                            asFiles(additionalNpmrcLocations)),
                    new PrettierConfig(
                            asFile(
                                    prettierConfigPath != null
                                            ? layout.find(prettierConfigPath).orElseThrow()
                                            : null),
                            prettierConfigOptions));
            return step;
        }
    }
}
