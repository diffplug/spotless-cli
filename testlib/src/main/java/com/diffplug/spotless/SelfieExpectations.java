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
package com.diffplug.spotless;

import java.io.File;
import java.nio.file.Files;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;

import com.diffplug.selfie.Camera;
import com.diffplug.selfie.Selfie;
import com.diffplug.selfie.Snapshot;
import com.diffplug.selfie.StringSelfie;

public class SelfieExpectations {

    @NotNull private final File rootFolder;

    private SelfieExpectations(@NotNull File rootFolder) {
        this.rootFolder = Objects.requireNonNull(rootFolder);
    }

    public static SelfieExpectations create(@NotNull File rootFolder) {
        Objects.requireNonNull(rootFolder);
        if (!rootFolder.isDirectory()) {
            throw new IllegalArgumentException("Root folder must be a directory: " + rootFolder);
        }
        return new SelfieExpectations(rootFolder);
    }

    private static final Camera<Resource> RESOURCE_CAMERA = (Resource resource) -> {
        File file = new File(resource.rootFolder(), resource.resourcePath());
        if (!file.exists()) {
            throw new IllegalArgumentException("Resource not found: " + file);
        }
        return Snapshot.of(ThrowingEx.get(() -> Files.readString(file.toPath())));
    };

    record Resource(File rootFolder, String resourcePath) {}

    public StringSelfie expectResource(String resourcePath) {
        return Selfie.expectSelfie(new Resource(rootFolder, resourcePath), RESOURCE_CAMERA);
    }
}
