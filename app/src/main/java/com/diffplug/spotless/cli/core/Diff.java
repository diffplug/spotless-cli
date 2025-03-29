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
package com.diffplug.spotless.cli.core;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import difflib.DiffUtils;
import difflib.Patch;

public final class Diff {

    public static final int DEFAULT_MAX_PATCHES = 3;
    public static final int DEFAULT_CONTEXT = 0;

    private Diff() {
        // no instance
    }

    public static int countLineDifferences(String dirty, String clean) {
        return DiffUtils.diff(lines(dirty), lines(clean)).getDeltas().size();
    }

    public static String createDiffString(String dirty, String clean, Path file) {
        return createDiffString(dirty, clean, file, Limits.defaultLimits());
    }

    public static String createDiffString(String dirty, String clean, Path file, Limits limits) {
        List<String> dirtyLines = lines(dirty);
        List<String> cleanLines = lines(clean);

        final Patch<String> patch = DiffUtils.diff(dirtyLines, cleanLines);

        // limit to 3 diffs to avoid too much output
        final Patch<String> limitedPatch = new Patch<>();
        patch.getDeltas().stream().limit(limits.maxPatches()).forEachOrdered(limitedPatch::addDelta);

        final List<String> diff = new ArrayList<>(DiffUtils.generateUnifiedDiff(
                file.toString(), file + "(cleaned)", dirtyLines, limitedPatch, limits.diffContext()));
        if (limitedPatch.getDeltas().size() != patch.getDeltas().size()) {
            diff.add("[...]"); // indicate that we have more diffs
        }
        return String.join("\n", diff);
    }

    private static List<String> lines(String string) {
        return Arrays.asList(string.split("\r?\n"));
    }

    public record Limits(int maxPatches, int diffContext) {
        static Limits defaultLimits() {
            return new Limits(DEFAULT_MAX_PATCHES, DEFAULT_CONTEXT);
        }
    }
}
