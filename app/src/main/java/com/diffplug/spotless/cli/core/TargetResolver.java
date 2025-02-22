/*
 * Copyright 2024 DiffPlug
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

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;

import com.diffplug.spotless.ThrowingEx;

public class TargetResolver {

    private final List<String> targets;
    private final FileResolver fileResolver;

    public TargetResolver(@NotNull Path baseDir, @NotNull List<String> targets) {
        this.fileResolver = new FileResolver(baseDir);
        this.targets = Objects.requireNonNull(targets);
    }

    public Stream<Path> resolveTargets() {
        // Start with a parallel stream on the targets.
        // flatMap is used here, but because each inner stream (from resolveTarget)
        // is forced to be parallel (when needed), overall processing remains parallel.
        return targets.parallelStream().flatMap(this::resolveTarget);
    }

    private Stream<Path> resolveTarget(String target) {
        boolean isGlob = target.contains("*") || target.contains("?");
        System.out.println("isGlob: " + isGlob + " target: " + target);
        if (isGlob) {
            return resolveGlob(target);
        }
        Path targetPath = fileResolver.resolvePath(Path.of(target));
        if (Files.isRegularFile(targetPath) && Files.isReadable(targetPath)) {
            // A single file stream doesn’t benefit much from parallelism.
            return Stream.of(targetPath);
        }
        if (Files.isDirectory(targetPath)) {
            return resolveDir(targetPath);
        }
        // Optionally log a warning if the target was not found.
        return Stream.empty();
    }

    private Stream<Path> resolveDir(Path startDir) {
        return ThrowingEx.get(() -> Files.walk(startDir)).parallel().filter(Files::isRegularFile);
    }

    private Stream<Path> resolveGlob(String glob) {
        // Split the glob into directory parts and the glob pattern.
        String[] parts = glob.split(Pattern.quote(File.separator));
        List<String> startDirParts =
                Stream.of(parts).takeWhile(part -> !isGlobPathPart(part)).collect(Collectors.toList());

        Path startDir = Path.of(
                glob.startsWith(File.separator)
                        ? File.separator
                        : fileResolver.baseDir().toString(),
                startDirParts.toArray(String[]::new));
        String globPart = Stream.of(parts).skip(startDirParts.size()).collect(Collectors.joining(File.separator));

        final PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + globPart);
        return ThrowingEx.get(() -> Files.walk(startDir))
                .parallel()
                .filter(Files::isRegularFile)
                .filter(path -> matcher.matches(startDir.relativize(path)))
                .map(Path::normalize);
    }

    private static boolean isGlobPathPart(String part) {
        return part.contains("*") || part.contains("?") || part.matches(".*\\[.*].*") || part.matches(".*\\{.*}.*");
    }
}
