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

enum ResultType {
    CLEAN,
    DIRTY,
    DID_NOT_CONVERGE;

    ResultType combineWith(ResultType other) {
        if (this == other) {
            return this;
        }
        if (this == DID_NOT_CONVERGE || other == DID_NOT_CONVERGE) {
            return DID_NOT_CONVERGE;
        }
        if (this == DIRTY || other == DIRTY) {
            return DIRTY;
        }
        throw new IllegalStateException("Unexpected combination of result types: " + this + " and " + other);
    }
}
