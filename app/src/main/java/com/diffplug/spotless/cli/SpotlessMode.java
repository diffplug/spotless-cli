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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.diffplug.spotless.ThrowingEx;
import com.diffplug.spotless.cli.logging.output.Output;

enum SpotlessMode {
    CHECK {
        private static final Logger LOGGER = LoggerFactory.getLogger(SpotlessMode.class);

        @Override
        ResultType handleResult(Result result) {
            if (result.lintState().isHasLints()) {

                Output.eitherDefault(() -> new Output.MessageWithArgs(
                                "File has lints: {} -- {}",
                                result.target().toFile().getPath(),
                                result.lintState()
                                        .asStringOneLine(result.target().toFile(), result.formatter())))
                        .orDetail(() -> new Output.MessageWithArgs(
                                "File has lints: {}\n\t{}",
                                result.target().toFile().getPath(),
                                result.lintState()
                                        .asStringDetailed(result.target().toFile(), result.formatter())))
                        .write();
            } else {
                LOGGER.debug(
                        "Check-Result - File is clean: {}",
                        result.target().toFile().getPath());
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
        ResultType handleResult(Result result) {
            if (result.lintState().isHasLints()) {
                // something went wrong, we should not apply the changes
                Output.eitherDefault(() -> new Output.MessageWithArgs(
                                "File has lints: {} -- {}",
                                result.target().toFile().getPath(),
                                result.lintState()
                                        .asStringOneLine(result.target().toFile(), result.formatter())))
                        .orDetail(() -> new Output.MessageWithArgs(
                                "File has lints: {}\n\t{}",
                                result.target().toFile().getPath(),
                                result.lintState()
                                        .asStringDetailed(result.target().toFile(), result.formatter())))
                        .write();
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

    abstract ResultType handleResult(Result result);

    abstract Integer translateResultTypeToExitCode(ResultType resultType);
}
