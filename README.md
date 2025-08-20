# <img align="left" src="docs/logo/spotless_banner.png" alt="Banner introducing spotless cli"> Spotless Command Line Interface CLI

_Keep your code Spotless from the command line_

<!---freshmark shields
output = [
  link(shield('SpotlessCLI Version', 'latest-version', '{{spotlessVersion}}', 'blue'), 'CHANGELOG.md'),
  '',
  link(shield('OS Win', 'OS', 'Windows', 'blueviolet'), '#installation-on-windows'),
  link(shield('OS Linux', 'OS', 'Linux', 'blueviolet'), '#installation-on-macos-and-linux'),
  link(shield('OS macOS', 'OS', 'macOS', 'blueviolet'), '#installation-on-macos-and-linux'),
  ].join('\n')
-->

[![SpotlessCLI Version](https://img.shields.io/badge/latest--version-0.2.0-blue.svg)](CHANGELOG.md)

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
                     __  __
   _________  ____  / /_/ /__  __________
  / ___/ __ \/ __ \/ __/ / _ \/ ___/ ___/
 (__  ) /_/ / /_/ / /_/ /  __(__  |__  )
/____/ .___/\____/\__/_/\___/____/____/   Spotless CLI
    /_/


Usage: spotless [-hV] [-e=<encoding>] [-l=<lineEnding>] [--log-file=<logFile>]
                [-m=<spotlessMode>] [-p=N] [-t=<targets>]... [-q | -v [-v]...]
                [FORMATTING_STEPS]

spotless is a command line interface (CLI) for the spotless code formatter.
It can either check if your files are formatted according to your configuration
or apply the formatting to the files.

  -e, --encoding=<encoding>  The encoding of the files to format.
                             (default: UTF-8)
  -h, --help                 Show this help message and exit.
  -l, --line-ending=<lineEnding>
                             The line ending of the files to format.
                             One of: GIT_ATTRIBUTES,
                               GIT_ATTRIBUTES_FAST_ALLSAME, PLATFORM_NATIVE,
                               WINDOWS, UNIX, MAC_CLASSIC, PRESERVE
                             (default: UNIX)
      --log-file=<logFile>   The log file to write the output to.
  -m, --mode=<spotlessMode>  The mode to run spotless in.
                             One of: CHECK, APPLY
                             (default: APPLY)
                             APPLY: Apply the correct formatting where needed
                               (replace file contents with formatted content).
                             CHECK: Check if the files are formatted or show
                               the diff of the formatting.
  -p, --parallelity=N        The number of parallel formatter threads to run.
                             (default: #cores * 0.5)
  -q, --quiet                Disable as much output as possible.
  -t, --target=<targets>     The target files to format. Blobs are supported.
                             Examples:
                             -t 'src/**/*.java'
                             -t 'src/**/*.kt'
                             -t 'README.md'
  -v                         Enable verbose output. Multiple -v options
                               increase the verbosity (max 5).
  -V, --version              Print version information and exit.

Available formatting steps:
  clang-format           Runs clang-format
  clean-that             CleanThat enables automatic refactoring of Java code.
  eclipse-wtp            Runs Eclipse WTP formatter (4.21.0)
  format-annotations     Corrects line break formatting of type annotations in
                           java files.
  google-java-format     Runs google java format
  license-header         Runs license header
  palantir-java-format   Runs palantir java format
  prettier               Runs prettier, the opinionated code formatter.
  remove-unused-imports  Removes unused imports from Java files.

Possible exit codes:
  0    Successful formatting.
       In APPLY mode, this means all files were formatted successfully.
       In CHECK mode, this means all files were already formatted properly.
  1    Some files need to be formatted.
       In APPLY mode, this means some files failed to be formatted (see output
         for details).
       In CHECK mode, this means some files are currently not formatted
         properly (and might be fixed in APPLY mode).
  -1   Some files did not converge. This can happen when one formatter does not
         converge on the file content.
       You can find more about this special case here:
         <https://github.com/diffplug/spotless/blob/main/PADDEDCELL.md>
  -2   An exception occurred during execution.
```

<!---freshmark /usage_main -->

## Available Formatter Steps

Spotless CLI supports the following formatter steps in alphabetical order:

- [clang-format](#clang-format)
- [clean-that](#clean-that)
- [eclipse-wtp](#eclipse-wtp)
- [format-annotations](#format-annotations)
- [google-java-format](#google-java-format)
- [license-header](#license-header)
- [palantir-java-format](#palantir-java-format)
- [prettier](#prettier)
- [remove-unused-imports](#remove-unused-imports)

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
Usage: spotless clang-format [-hV] [-c=<pathToExec>] [-s=<style>] [-v=<version>]
Runs clang-format
  -c, --clang-format-exec=<pathToExec>
                        The path to the clang-format executable.
                        (default: looks on your PATH)
  -h, --help            Show this help message and exit.
  -s, --style=<style>   The style to use for clang-format.
  -v, --clang-version=<version>
                        The version of clang-format to use.
                        (default: 10.0.1)
  -V, --version         Print version information and exit.

✅ This step supports the following file types:
   * C
   * C++
   * Java
   * JavaScript
   * JSON
   * Objective-C
   * Protobuf
   * C#

🌎 Additional info:
https://clang.llvm.org/docs/ClangFormat.html
```

<!---freshmark /usage_clang_format -->

Example usage:

```shell
spotless --target '**/src/**/*.cpp' clang-format --clang-version=20.1.2 --style=Google
```

> [!IMPORTANT]
> Running a clang-format step requires a working installation of the clang-format binary.

### clean-that

<!---freshmark ctshields
output = [
  link(shield('CleanThat version', 'clean-that', '{{libs.versions.native.include.cleanThat}}', 'blue'), 'https://github.com/solven-eu/cleanthat'),
  ].join('\n')
-->

[![CleanThat version](https://img.shields.io/badge/clean--that-2.23-blue.svg)](https://github.com/solven-eu/cleanthat)

<!---freshmark /ctshields -->

Cleanthat is a project enabling automatic code cleaning, from formatting to refactoring.

To see usage instructions for the clean-that formatter, run: `spotless clean-that --help`

<!---freshmark usage_clean_that
output =
   '```\n' +
   {{usage.clean-that.array}}.join('\n') +
    '\n```';
-->

```
Usage: spotless clean-that [-dDhV] [-s=<sourceCompatibility>] [-a[=mutator[,
                           mutator...]...]]... [-e[=mutator[,mutator...]...]]...
CleanThat enables automatic refactoring of Java code.
  -a, --add-mutator[=mutator[,mutator...]...]
                  Add a mutator to the list of mutators to use. Mutators are
                    the individual refactoring steps CleanThat applies. A list
                    of available mutators can be found in the "Additional Info"
                    section.
  -d, --use-default-mutators
                  Use the default mutators provided by CleanThat. Default
                    mutators are: <SafeAndConsensual>.
                  (default: true)
  -D, --include-draft-mutators
                  Include draft mutators in the list of mutators to use. Draft
                    mutators are experimental and may not be fully tested or
                    stable.
                  (default: false)
  -e, --exclude-mutator[=mutator[,mutator...]...]
                  Remove a mutator from the list of mutators to use. This might
                    make sense for composite mutators
  -h, --help      Show this help message and exit.
  -s, --source-compatibility=<sourceCompatibility>
                  The source JDK version to use for the CleanThat mutators.
                    This is used to determine the Java language features
                    available.
                  (default: 1.8)
  -V, --version   Print version information and exit.

✅ This step supports the following file type: Java

🌎 Additional info:
   * https://github.com/solven-eu/cleanthat
   * https://github.com/solven-eu/cleanthat/blob/master/MUTATORS.generated.MD
```

<!---freshmark /usage_clean_that -->

Example usage:

```shell
spotless --target '**/src/**/*.java' clean-that --exclude-mutator=StreamAnyMatch
```

### eclipse-wtp

<!---freshmark eclipsewtpshields
output = [
  link(shield('spotless eclipse wtp version', 'spotless-eclipse-wtp', '{{libs.versions.native.include.spotlessEclipseWtp}}', 'blue'), 'https://central.sonatype.com/artifact/com.diffplug.spotless/spotless-eclipse-wtp/{{libs.versions.native.include.spotlessEclipseWtp}}'),
  link(shield('eclipse wtp version', 'eclipse-wtp-formatter', '{{libs.versions.native.include.spotlessEclipseWtpFormatter}}', 'blue'), 'https://github.com/diffplug/spotless/blob/main/lib-extra/src/main/resources/com/diffplug/spotless/extra/eclipse_wtp_formatter/v{{libs.versions.native.include.spotlessEclipseWtpFormatter}}'),
  ].join('\n')
-->

[![spotless eclipse wtp version](https://img.shields.io/badge/spotless--eclipse--wtp-3.23.0-blue.svg)](https://central.sonatype.com/artifact/com.diffplug.spotless/spotless-eclipse-wtp/3.23.0)
[![eclipse wtp version](https://img.shields.io/badge/eclipse--wtp--formatter-4.21.0-blue.svg)](https://github.com/diffplug/spotless/blob/main/lib-extra/src/main/resources/com/diffplug/spotless/extra/eclipse_wtp_formatter/v4.21.0)

<!---freshmark /eclipsewtpshields -->

The [eclipse web tools platform (WTP)](https://projects.eclipse.org/projects/webtools) formatter is a formatter for web files such as HTML, CSS, JavaScript, JSON, XML and XHTML.

It comes with reasonable defaults but can be configured using configuration files. For details see the [spotless documentation](https://github.com/diffplug/spotless/tree/main/plugin-gradle#eclipse-web-tools-platform).

To see usage instructions for the eclipse-wtp formatter, run: `spotless eclipse-wtp --help`

<!---freshmark usage_eclipse_wtp
output =
   '```\n' +
   {{usage.eclipse-wtp.array}}.join('\n') +
    '\n```';
-->

```
Usage: spotless eclipse-wtp [-hV] [-f]... [-t=<type>]
Runs Eclipse WTP formatter (4.21.0)
  -f, --config-file   The path to the Eclipse WTP configuration file. For
                        supported config file options see spotless
                        documentation (additional info links).
  -h, --help          Show this help message and exit.
  -t, --type=<type>   The type of the Eclipse WTP formatter. If not provided,
                        the type will be guessed based on the first few files
                        we find. If that does not work, we fail the formatting
                        run.
                      One of: CSS, HTML, JS, JSON, XML, XHTML
  -V, --version       Print version information and exit.

✅ This step supports the following file types:
   * css
   * html
   * js
   * json
   * xml
   * xhtml

🌎 Additional info:
   * https://github.com/diffplug/spotless/tree/main/plugin-gradle#eclipse-web-to
    ols-platform

   * https://projects.eclipse.org/projects/webtools
```

<!---freshmark /usage_eclipse_wtp -->

Example usage:

```shell
# format all js files using (multiple) project-specific configuration files
# for details regarding the configuration files see the spotless documentation
spotless --target '**/*.js' eclipse-wtp --type js --config-file spotless.xml.prefs --config-file spotless.common.properties
# or use defaults and infer type from files
spotless --target '**/*.css' eclipse-wtp
```

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
Usage: spotless format-annotations [-hV] [-a[=annotation[,
                                   annotation...]...]]... [-r[=annotation[,
                                   annotation...]...]]...
Corrects line break formatting of type annotations in java files.
  -a, --add-type-annotation[=annotation[,annotation...]...]
                  Add annotations to the list of type annotations to keep on
                    the same line as the type.
  -h, --help      Show this help message and exit.
  -r, --remove-type-annotation[=annotation[,annotation...]...]
                  Remove annotations from the list of type annotations to keep
                    on the same line as the type.
  -V, --version   Print version information and exit.

✅ This step supports the following file type: Java

🌎 Additional info:
https://github.com/diffplug/spotless/tree/main/plugin-gradle#formatAnnotations
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

[![Google Java Format version](https://img.shields.io/badge/google--java--format-1.27.0-blue.svg)](https://github.com/google/google-java-format)

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
Usage: spotless google-java-format [-hijrV] [-s=<style>]
Runs google java format
  -h, --help              Show this help message and exit.
  -i, --reorder-imports   Reorder imports.
                          (default: false)
  -j, --format-javadoc    Format javadoc.
                          (default: true)
  -r, --reflow-long-strings
                          Reflow long strings.
                          (default: false)
  -s, --style=<style>     The style to use for the google java format.
                          One of: AOSP, GOOGLE
                          (default: GOOGLE)
  -V, --version           Print version information and exit.

✅ This step supports the following file type: Java

🌎 Additional info:
https://github.com/google/google-java-format
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
Usage: spotless license-header [-hV] [-c=<contentPattern>] [-d=<delimiter>]
                               [-m=<yearMode>] [-s=<skipLinesMatching>]
                               [-Y=<yearSeparator>] (-H=<header> |
                               -f=<headerFile>)
Runs license header
  -c, --content-pattern=<contentPattern>
                          The pattern to match the content of the file before
                            inserting the licence header. (If the file content
                            does not match the pattern, the header will not be
                            inserted/updated.)
  -d, --delimiter=<delimiter>
                          The delimiter to use for the license header. If not
                            provided, the delimiter will be guessed based on
                            the first few files we find. Otherwise, 'java' will
                            be assumed.
  -f, --header-file=<headerFile>
                          The license header content in a file to apply.
                            May contain $YEAR as placeholder.
  -h, --help              Show this help message and exit.
  -H, --header=<header>   The license header content to apply. May contain
                            $YEAR as placeholder.
  -m, --year-mode=<yearMode>
                          How and if the year in the copyright header should be
                            updated.
                          One of: PRESERVE, UPDATE_TO_TODAY, SET_FROM_GIT
                          (default: PRESERVE)
  -s, --skip-lines-matching=<skipLinesMatching>
                          Skip lines matching the given regex pattern before
                            inserting the licence header.
  -V, --version           Print version information and exit.
  -Y, --year-separator=<yearSeparator>
                          The separator to use for the year range in the
                            license header.
                          (default: -)

✅ This step supports the following file type: any

🌎 Additional info:
https://github.com/diffplug/spotless/tree/main/plugin-gradle#license-header
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

[![Palantir Java Format version](https://img.shields.io/badge/palantir--java--format-2.67.0-blue.svg)](https://github.com/palantir/palantir-java-format)

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
Usage: spotless palantir-java-format [-hjV] [-s=<style>]
Runs palantir java format
  -h, --help             Show this help message and exit.
  -j, --format-javadoc   Format javadoc.
                         (default: false)
  -s, --style=<style>    The style to use for the palantir java format.
                         One of: PALANTIR, AOSP, GOOGLE
                         (default: PALANTIR)
  -V, --version          Print version information and exit.

✅ This step supports the following file type: Java

🌎 Additional info:
https://github.com/palantir/palantir-java-format
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
Usage: spotless prettier [-hV] [-C=<npmInstallCacheDir>]
                         [-n=<explicitNpmExecutable>]
                         [-N=<explicitNodeExecutable>]
                         [-P=<prettierConfigPath>] [-R=<explicitNpmrcFile>]
                         [-A=<additionalNpmrcLocations>]...
                         [-c='OPTION=VALUE']... [-D='PACKAGE=VERSION']...
Runs prettier, the opinionated code formatter.
  -A, --additional-npmrc-location=<additionalNpmrcLocations>
                  Additional locations to search for .npmrc files.
  -c, --prettier-config-option='OPTION=VALUE'
                  A prettier configuration options.
                  The format is 'OPTION=VALUE'.
                  example: 'printWidth=80'
  -C, --npm-install-cache-dir=<npmInstallCacheDir>
                  The directory to use for caching libraries retrieved by 'npm
                    install'.
  -D, --dev-dependency='PACKAGE=VERSION'
                  An entry to add to the package.json for running prettier.
                  The format is 'PACKAGE=VERSION'.
                  example: 'prettier=2.8.7'
  -h, --help      Show this help message and exit.
  -n, --npm-exec=<explicitNpmExecutable>
                  The explicit path to the npm executable.
  -N, --node-exec=<explicitNodeExecutable>
                  The explicit path to the node executable.
  -P, --prettier-config-path=<prettierConfigPath>
                  The path to the prettier configuration file.
  -R, --npmrc-file=<explicitNpmrcFile>
                  The explicit path to the .npmrc file.
  -V, --version   Print version information and exit.

✅ This step supports the following file types:
   * JavaScript
   * JSX
   * Angular
   * Vue
   * Flow
   * TypeScript
   * CSS
   * Less
   * SCSS
   * HTML
   * Ember/Handlebars
   * JSON
   * GraphQL
   * Markdown
   * YAML
   * Java (only with plugins)
   * and more (using plugins)

🌎 Additional info:
   * https://prettier.io/
   * 🧩 Find plugins at https://prettier.io/docs/plugins.html#official-plugins
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

### remove-unused-imports

This removes unused imports from Java files.

To see usage instructions for the remove-unused-imports formatter, run: `spotless remove-unused-imports --help`

<!---freshmark usage_remove_unused_imports
output =
   '```\n' +
   {{usage.remove-unused-imports.array}}.join('\n') +
    '\n```';
-->

```
Usage: spotless remove-unused-imports [-hV] [-e=<engine>]
Removes unused imports from Java files.
  -e, --engine=<engine>   The backing engine to use for detecting and removing
                            unused imports.
                          One of: GOOGLE_JAVA_FORMAT, CLEAN_THAT
                          (default: GOOGLE_JAVA_FORMAT)
  -h, --help              Show this help message and exit.
  -V, --version           Print version information and exit.

✅ This step supports the following file type: Java

🌎 Additional info:
https://github.com/diffplug/spotless/tree/main/plugin-gradle#removeunusedimports
```

<!---freshmark /usage_remove_unused_imports -->

Example usage:

```shell
spotless --target '**/src/**/*.java' remove-unused-imports

# or use non-default engine
spotless --target '**/src/**/*.java' remove-unused-imports --engine=CLEAN_THAT
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
