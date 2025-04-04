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

import java.lang.ref.Cleaner;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jetbrains.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum SpotlessRunCleanup {
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(SpotlessRunCleanup.class);

    private static final Cleaner CLEANER = Cleaner.create();

    private static final ConcurrentLinkedQueue<Cleaner.Cleanable> CLEANABLES = new ConcurrentLinkedQueue<>();

    public void deleteDirOnCleanup(Object reference, Path path) {
        LOGGER.debug("Registering cleanup for directory: {} -- reference: {}", path, reference);
        CLEANABLES.add(CLEANER.register(reference, new PathCleanup(path)));
    }

    @VisibleForTesting
    public void clearCleanables() {
        while (!CLEANABLES.isEmpty()) {
            Cleaner.Cleanable cleanable = CLEANABLES.poll();
            if (cleanable != null) {
                cleanable.clean();
            }
        }
    }

    private record PathCleanup(Path path) implements Runnable {

        @Override
        public void run() {
            // delete the directory
            deleteDir();
        }

        private void deleteDir() {
            if (!Files.exists(path)) {
                LOGGER.debug("Directory does not exist, nothing to delete: {}", path);
                return; // Directory does not exist, nothing to delete
            }
            LOGGER.info("Deleting directory: {}", path);
            try {
                Files.walk(path).sorted(Comparator.reverseOrder()).forEach(file -> {
                    try {
                        Files.delete(file);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to delete file: " + file, e);
                    }
                });
            } catch (Exception e) {
                LOGGER.warn("Failed to delete directory: {}", path, e);
            }
        }
    }
}
