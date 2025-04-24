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
package com.diffplug.spotless.cli;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.diffplug.spotless.ThrowingEx;
import com.diffplug.spotless.cli.core.Diff;
import com.diffplug.spotless.cli.logging.output.Output;

enum SpotlessMode {
    CHECK {
        private static final Logger LOGGER = LoggerFactory.getLogger(SpotlessMode.class);

        @Override
        ResultType handleResult(Output output, Result result) {
            if (result.lintState().isHasLints()) {
                output.eitherDefault(() -> new Output.MessageWithArgs(
                                "File has lints: {} -- {}",
                                result.target().toFile().getPath(),
                                result.lintState()
                                        .asStringOneLine(result.target().toFile(), result.formatter())))
                        .orDetail(() -> new Output.MessageWithArgs(
                                "File has lints: {}\n\t{}",
                                result.target().toFile().getPath(),
                                result.lintState()
                                        .asStringDetailed(result.target().toFile(), result.formatter())));
                return ResultType.DIRTY;
            }

            try (ByteArrayOutputStream cleanedOutputStream = new ByteArrayOutputStream()) {
                result.lintState().getDirtyState().writeCanonicalTo(cleanedOutputStream);
                String cleaned = cleanedOutputStream.toString(StandardCharsets.UTF_8);
                String original = Files.readString(result.target(), StandardCharsets.UTF_8);

                final int diffs = Diff.countLineDifferences(original, cleaned);
                output.eitherDefault(() -> {
                            if (diffs > 0) {
                                return new Output.MessageWithArgs(
                                        "File needs reformatting: {} -- {} differences", result.target(), diffs);
                            }
                            return new Output.MessageWithArgs("File is clean: {}", result.target());
                        })
                        .orDetail(() -> {
                            String diffString = Diff.createDiffString(original, cleaned, result.target());
                            String delim = "*".repeat(80);
                            if (diffs > 0) {
                                return new Output.MessageWithArgs(
                                        "File needs reformatting: {}\n{}\n{}\n{}",
                                        result.target(),
                                        delim,
                                        diffString,
                                        delim);
                            }
                            return new Output.MessageWithArgs("File is clean: {}", result.target());
                        });
            } catch (IOException e) {
                throw ThrowingEx.asRuntime(e);
            }
            return ResultType.DIRTY;
        }

        @Override
        Integer translateResultTypeToExitCode(ResultType resultType) {
            return switch (resultType) {
                case CLEAN -> 0;
                case DIRTY -> 1;
                case DID_NOT_CONVERGE -> -1;
            };
        }
    },
    APPLY {
        private static final Logger LOGGER = LoggerFactory.getLogger(SpotlessMode.class);

        @Override
        ResultType handleResult(Output output, Result result) {
            if (result.lintState().isHasLints()) {
                // something went wrong, we should not apply the changes
                output.eitherDefault(() -> new Output.MessageWithArgs(
                                "File has lints: {} -- {}",
                                result.target().toFile().getPath(),
                                result.lintState()
                                        .asStringOneLine(result.target().toFile(), result.formatter())))
                        .orDetail(() -> new Output.MessageWithArgs(
                                "File has lints: {}\n\t{}",
                                result.target().toFile().getPath(),
                                result.lintState()
                                        .asStringDetailed(result.target().toFile(), result.formatter())));
                return ResultType.DIRTY;
            } else {
                LOGGER.debug(
                        "Apply-Result - File is clean: {}",
                        result.target().toFile().getPath());
            }
            ThrowingEx.run(() -> result.lintState()
                    .getDirtyState()
                    .writeCanonicalTo(result.target().toFile()));
            return ResultType.CLEAN;
        }

        @Override
        Integer translateResultTypeToExitCode(ResultType resultType) {
            return switch (resultType) {
                case CLEAN -> 0;
                case DIRTY -> 1;
                case DID_NOT_CONVERGE -> -1;
            };
        }
    };

    abstract ResultType handleResult(Output output, Result result);

    abstract Integer translateResultTypeToExitCode(ResultType resultType);
}
