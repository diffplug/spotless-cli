# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

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
