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

import com.diffplug.spotless.ThrowingEx;

enum SpotlessMode {
    CHECK {
        @Override
        ResultType handleResult(Result result) {
            if (result.lintState().isHasLints()) {
                result.lintState().asStringOneLine(result.target().toFile(), result.formatter());
            } else {
                System.out.println(String.format("%s is violating formatting rules.", result.target()));
            }
            return ResultType.DIRTY;
        }

        @Override
        Integer translateResultTypeToExitCode(ResultType resultType) {
            if (resultType == ResultType.CLEAN) {
                return 0;
            }
            if (resultType == ResultType.DIRTY) {
                return 1;
            }
            if (resultType == ResultType.DID_NOT_CONVERGE) {
                return -1;
            }
            throw new IllegalStateException("Unexpected result type: " + resultType);
        }
    },
    APPLY {
        @Override
        ResultType handleResult(Result result) {
            if (result.lintState().isHasLints()) {
                // something went wrong, we should not apply the changes
                System.err.println("File has lints: " + result.target().toFile().getName());
                System.err.println("lint:\n"
                        + result.lintState().asStringDetailed(result.target().toFile(), result.formatter()));
                return ResultType.DIRTY;
            }
            ThrowingEx.run(() -> result.lintState()
                    .getDirtyState()
                    .writeCanonicalTo(result.target().toFile()));
            return ResultType.CLEAN;
        }

        @Override
        Integer translateResultTypeToExitCode(ResultType resultType) {
            if (resultType == ResultType.CLEAN) {
                return 0;
            }
            if (resultType == ResultType.DIRTY) {
                return 0;
            }
            if (resultType == ResultType.DID_NOT_CONVERGE) {
                return -1;
            }
            throw new IllegalStateException("Unexpected result type: " + resultType);
        }
    };

    abstract ResultType handleResult(Result result);

    abstract Integer translateResultTypeToExitCode(ResultType resultType);
}
