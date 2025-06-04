# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

- Commit, tag and push the choco source files to the chocolatey-bucket repository during the release process

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
