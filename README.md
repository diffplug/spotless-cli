# <img align="left" src="docs/logo/spotless_banner.png" alt="Banner introducing spotless cli"> Spotless Command Line Interface CLI

_Keep your code Spotless from the command line_

<!---freshmark shields
output = [
  link(shield('SpotlessCLI Version', 'latest-version', '{{spotlessVersion}}', 'blue'), 'CHANGES.md'),
  '',
  link(shield('OS Win', 'OS', 'Windows', 'blueviolet'), '#installation-on-windows'),
  link(shield('OS Linux', 'OS', 'Linux', 'blueviolet'), '#installation-on-macos-and-linux'),
  link(shield('OS macOS', 'OS', 'macOS', 'blueviolet'), '#installation-on-macos-and-linux'),
  ].join('\n')
-->

[![SpotlessCLI Version](https://img.shields.io/badge/latest--version-0.1.0-blue.svg)](CHANGES.md)

[![OS Win](https://img.shields.io/badge/OS-Windows-blueviolet.svg)](#installation-on-windows)
[![OS Linux](https://img.shields.io/badge/OS-Linux-blueviolet.svg)](#installation-on-macos-and-linux)
[![OS macOS](https://img.shields.io/badge/OS-macOS-blueviolet.svg)](#installation-on-macos-and-linux)

<!---freshmark /shields -->

> [!NOTE]
> This project is a work in progress :hourglass_flowing_sand: and not yet released.
>
> Please check back later for the first release. :heart:

`spotless` is a command line interface (CLI) for the [spotless code formatter](../README.md).
It intends to be a simple alternative to its siblings: the plugins for [gradle](../plugin-gradle/README.md), [maven](../plugin-maven/README.md)
and others. It supports formatting a plethora of file types and can be easily configured.

Example usage:

```shell
spotless --target '**/src/**/*.java' \
    google-java-format \
    license-header --header='/* (c) DiffPlug $YEAR */'
```

This command formats all java files in any `src` folder with the [google-java-format](https://github.com/google/google-java-format) and adds (or updates an existing) license header.

Using the above command line you go

<!---freshmark example_usage_before_after
output = [
  '| From this | to this |',
    '| --- | --- |',
  '| ' + image('before', 'docs/examples/intro/FormattingExample.png') + ' | ' + image('after', 'docs/examples/intro/FormattingExampleFormatted.png') + ' |',
  ].join('\n')
-->

| From this                                            | to this                                                      |
| ---------------------------------------------------- | ------------------------------------------------------------ |
| ![before](docs/examples/intro/FormattingExample.png) | ![after](docs/examples/intro/FormattingExampleFormatted.png) |

<!---freshmark /example_usage_before_after -->

## Installation

### Installation on macOS and Linux

To install with [Homebrew](https://brew.sh/) on macOS or Linux:

```shell
brew install diffplug/tap/spotless-cli

# or if you prefer
brew tap diffplug/tap
brew install spotless-cli
```

### Installation on Windows

To install with [Chocolatey](https://chocolatey.org/) on Windows:

```shell
choco install spotless-cli
```

Alternatively, you can download the latest binary for your system from the [releases page](https://...) and add it to your PATH.

## General usage

The general principle is to specify the files to format, configure global options and then add one or more formatter steps - with configuration if needed.

```shell
# general structure of the invocation
spotless --target [... more options] formatter1 [config-of-formatter1] formatter2 [config-of-formatter2] ...
```

Be aware that the order of the formatter steps is important. The formatters are applied in the order they are specified.

To see all available options and formatters, run:

```shell
spotless --help
```

This will show you the available options and formatters as such:

<!---freshmark usage_main
output =
   '```\n' +
   {{usage.main.array}}.join('\n') +
    '\n```';
-->

```

```

<!---freshmark /usage_main -->

## Available Formatter Steps

Spotless CLI supports the following formatter steps in alphabetical order:

- [clang-format](#clang-format)
- [format-annotations](#format-annotations)
- [google-java-format](#google-java-format)
- [license-header](#license-header)
- [palantir-java-format](#palantir-java-format)
- [prettier](#prettier)

### clang-format

Formats C/C++/Objective-C and more files according to the [clang-format](https://clang.llvm.org/docs/ClangFormat.html) style guide.

To see usage instructions for the clang-format formatter, run: `spotless clang-format --help`

<!---freshmark usage_clang_format
output =
   '```\n' +
   {{usage.clang-format.array}}.join('\n') +
    '\n```';
-->

```

```

<!---freshmark /usage_clang_format -->

Example usage:

```shell
spotless --target '**/src/**/*.cpp' clang-format --clang-version=20.1.2 --style=Google
```

> [!IMPORTANT]
> Running a clang-format step requires a working installation of the clang-format binary.

### format-annotations

In Java, type annotations should be on the same line as the type that they qualify. This formatter fixes this for you.

To see usage instructions for the format-annotations formatter, run: `spotless format-annotations --help`

<!---freshmark usage_format_annotations
output =
   '```\n' +
   {{usage.format-annotations.array}}.join('\n') +
    '\n```';
-->

```

```

<!---freshmark /usage_format_annotations -->

Example usage:

```shell
spotless --target '**/src/**/*.java' format-annotations

# or add/remove annotations to the default set using list syntax
spotless --target '**/src/**/*.java' format-annotations \
    --add-type-annotation='MyAnnotation1,MyAnnotation2' \
    --remove-type-annotation='MyAnnotation3,MyAnnotation4'

# or add/remove annotations to the default set using repeated options
spotless --target '**/src/**/*.java' format-annotations \
    --add-type-annotation='MyAnnotation1' \
    --add-type-annotation='MyAnnotation2' \
    --remove-type-annotation='MyAnnotation3' \
    --remove-type-annotation='MyAnnotation4'
```

### google-java-format

<!---freshmark gjfshields
output = [
  link(shield('Google Java Format version', 'google-java-format', '{{libs.versions.native.include.googleJavaFormat}}', 'blue'), 'https://github.com/google/google-java-format'),
  ].join('\n')
-->

[![Google Java Format version](https://img.shields.io/badge/google--java--format-1.24.0-blue.svg)](https://github.com/google/google-java-format)

<!---freshmark /gjfshields -->

Formats Java files according to the [google-java-format](https://github.com/google/google-java-format) style guide.

To see usage instructions for the google-java-format formatter, run: `spotless google-java-format --help`

<!---freshmark usage_google_java_format
output =
   '```\n' +
   {{usage.google-java-format.array}}.join('\n') +
    '\n```';
-->

```

```

<!---freshmark /usage_google_java_format -->

Example usage:

```shell
spotless --target '**/src/**/*.java' google-java-format --reorder-imports=true
```

### license-header

Add or update a license header to the files.

To see usage instructions for the license-header formatter, run: `spotless license-header --help`

<!---freshmark usage_license_header
output =
   '```\n' +
   {{usage.license-header.array}}.join('\n') +
    '\n```';
-->

```

```

<!---freshmark /usage_license_header -->

Example usage:

```shell
spotless --target '**/src/**/*.java' license-header --header='/* (c) DiffPlug $YEAR */'
```

### palantir-java-format

<!---freshmark pjfshields
output = [
  link(shield('Palantir Java Format version', 'palantir-java-format', '{{libs.versions.native.include.palantirJavaFormat}}', 'blue'), 'https://github.com/palantir/palantir-java-format'),
  ].join('\n')
-->

[![Palantir Java Format version](https://img.shields.io/badge/palantir--java--format-2.61.0-blue.svg)](https://github.com/palantir/palantir-java-format)

<!---freshmark /pjfshields -->

Formats java files according to the [palantir-java-format](https://github.com/palantir/palantir-java-format) style guide. Palantir Java Format is a modern, lambda-friendly,
120 character Java formatter. It is based on the Google Java Format project.

To see usage instructions for the palantir-java-format formatter, run `spotless palantir-java-format --help`

<!---freshmark usage_palantir_java_format
output =
   '```\n' +
   {{usage.palantir-java-format.array}}.join('\n') +
    '\n```';
-->

```

```

<!---freshmark /usage_palantir_java_format -->

Example usage:

```shell
spotless --target '**/src/**/*.java' palantir-java-format --format-javadoc=true
```

### prettier

<!---freshmark prettiershields
output = [
  link(shield('Default prettier version', '(default)-prettier', '{{libs.versions.bundled.prettier}}', 'blue'), 'https://www.npmjs.com/package/prettier/v/{{libs.versions.bundled.prettier}}'),
  ].join('\n')
-->

[![Default prettier version](https://img.shields.io/badge/%28default%29--prettier-2.8.8-blue.svg)](https://www.npmjs.com/package/prettier/v/2.8.8)

<!---freshmark /prettiershields -->

[Prettier](https://prettier.io/) is an opinionated code formatter that supports many languages. Some are supported out of the box such as
JavaScript, JSX, Angular, Vue, Flow, TypeScript, CSS, Less, SCSS, HTML, Ember/Handlebars, JSON, GraphQL, Markdown and YAML.

Even more languages can be supported by including [prettier-plugins](https://prettier.io/docs/plugins).

> [!IMPORTANT]
> Running a prettier formatter step requires a working installation of [Node.js](https://nodejs.org/en/) and [npm](https://www.npmjs.com/).

To see usage instructions for the prettier formatter, run: `spotless prettier --help`

<!---freshmark usage_prettier
output =
   '```\n' +
   {{usage.prettier.array}}.join('\n') +
    '\n```';
-->

```

```

<!---freshmark /usage_prettier -->

Example usage:

```shell
spotless --target '**/*.json' prettier

# or using a custom version and plugin (prettier <= 2)
spotless --target='src/**/*.java' prettier \
    --prettier-config-option='printWidth=120' \
    --dev-dependency='prettier=2.8.7' \
    --dev-dependency='prettier-plugin-java=2.1.0'

# or using a custom version and plugin (prettier 3+)
# → prettier 3 needs you to enable plugins explicitly (see 'plugins' config option)
spotless --target='src/**/*.java' prettier \
    --prettier-config-option='printWidth=120' \
    --prettier-config-option='plugins=["prettier-plugin-java"]' \
    --dev-dependency='prettier=3.0.3' \
    --dev-dependency='prettier-plugin-java=2.3.0'
```

## Tipps & Tricks

### Using a configuration file

Since spotless-cli is based on `picocli`, you can use configuration files to store long or complex command lines
(called @files in picocli terminology).

:point_right: For details see [picocli documentation](https://picocli.info/#AtFiles)

Example usage:

Store a configuration file `/path/to/my/project/spotless-prettier-java.config` with the following content:

```
--target 'src/**/*.java'
prettier
--prettier-config-option 'printWidth=120'
--prettier-config-option 'plugins=["prettier-plugin-java"]'
--dev-dependency 'prettier=3.0.3'
--dev-dependency 'prettier-plugin-java=2.3.0'
license-header
--header-file=/path/to/my/project/license-header.txt
```

Then you can run spotless-cli with just the following command:

```shell
spotless @/path/to/my/project/spotless-prettier-java.config
```

which behind the scenes will be expanded into:

```shell
spotless --target='src/**/*.java' \
    prettier \
        --prettier-config-option='printWidth=120' \
        --prettier-config-option='plugins=["prettier-plugin-java"]' \
        --dev-dependency='prettier=3.0.3' \
        --dev-dependency='prettier-plugin-java=2.3.0' \
    license-header \
        --header-file='/path/to/my/project/license-header.txt'
```
