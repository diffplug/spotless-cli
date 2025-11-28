# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Changed

- **BREAKING** Bumping the bundled spotless-lib (3.3.1 -> 4.1.0)
- **BREAKING** spotless cli is moving away from distributing as a graalvm native image.
  From now on, we will distribute spotless cli as a regular java application (set of jars).
  :question: Why? Even though the native image gives us great performance, it comes with the cost of major limitations:
  - :boom: no support for dynamic class loading (which is needed to load formatters/plugins in specific versions at runtime)
  - :boom: classpath collisions are difficult to handle (for example it is not possible to have eclipse-wtp and eclipse-java-formatter at the same time)
  - :boom: increased code complexity when reflection is added mix by any formatter used
    The distribution as regular java application removes these limitations and gives us more flexibility to add new formatters and features in the future.
    The downside is that the startup time will be a bit slower and it requires a jre to be installed on the system.

### Fixed

- Fix automated chocolatey source package publishing

## [0.3.0] - 2025-09-03

### Added

- Added formatter [`eclipse-wtp`](https://github.com/diffplug/spotless/tree/main/plugin-gradle#eclipse-web-tools-platform)

## [0.2.0] - 2025-07-31

### Fixed

- Adapted the build so that README.md does correctly include usage helps when updated during the release process

### Added

- Commit, tag and push the choco source files to the chocolatey-bucket repository during the release process
- Added formatter [`clean-that`](https://github.com/diffplug/spotless/tree/main/plugin-gradle#cleanthat)
- Added formatter [`remove-unused-imports`](https://github.com/diffplug/spotless/tree/main/plugin-gradle#removeunusedimports)

### Changed

- Bumping the bundled spotless-lib (3.1.1 -> 3.3.1)

## [0.1.1] - 2025-06-02

### Changed

- Improvements to the chocolatey package distribution
- Bumping the bundled google-java-format (1.21.0 -> 1.27.0)
- Bumping the bundled palantir-java-format (2.61.0 -> 2.67.0)

## [0.1.0] - 2025-05-13

### Fixed

- Support for transporting changelog to github releases
- Make sure release binaries are executable on unix systems
- Make sure to use same zips for chocolatey distribution that are used for the release

### Added

- Initial version of the CLI
- Supported formatters (in alphabetical order):
  - clang-format
  - format-annotations
  - google-java-format
  - license-header
  - palantir-java-format
  - prettier
