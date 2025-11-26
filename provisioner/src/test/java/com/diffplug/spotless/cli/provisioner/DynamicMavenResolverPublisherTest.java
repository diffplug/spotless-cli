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
package com.diffplug.spotless.cli.provisioner;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.assertj.core.api.SoftAssertions;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class DynamicMavenResolverPublisherTest {

    @Test
    @Disabled(
            "Disabled to avoid network dependency during tests, enable when network access is acceptable while developing")
    void itResolvesDiffplugSpotlessLib(@TempDir Path tempDir) {
        // Given
        DynamicMavenResolverPublisher provisioner =
                new DynamicMavenResolverPublisher(DynamicMavenResolverPublisher.DEFAULT_REMOTE_REPOSITORIES, tempDir);
        // When
        Set<File> files = provisioner.provisionWithTransitives(true, "com.diffplug.spotless:spotless-lib:4.1.0");
        Set<String> fileNames = files.stream().map(File::getName).collect(Collectors.toSet());
        // Then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(files).isNotEmpty();
            softly.assertThat(fileNames).contains("spotless-lib-4.1.0.jar");
        });
    }

    @Test
    void itResolvesTestLib1(@TempDir Path tempDir) {
        // Given
        DynamicMavenResolverPublisher provisioner = provisionerForLocalMavenTestRepo(tempDir);
        // When
        Set<File> files = provisioner.provisionWithTransitives(true, "com.example:lib1:1.0.0");
        Set<String> fileNames = files.stream().map(File::getName).collect(Collectors.toSet());
        // Then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(files).isNotEmpty();
            softly.assertThat(fileNames).contains("lib1-1.0.0.jar");
        });
    }

    @Test
    void itResolvesTestLib2WithTransitives(@TempDir Path tempDir) {
        // Given
        DynamicMavenResolverPublisher provisioner = provisionerForLocalMavenTestRepo(tempDir);
        // When
        Set<File> files = provisioner.provisionWithTransitives(true, "com.example:lib2:1.0.0");
        Set<String> fileNames = files.stream().map(File::getName).collect(Collectors.toSet());

        // Then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(files).isNotEmpty();
            softly.assertThat(fileNames).contains("lib2-1.0.0.jar").contains("lib1-1.0.0.jar");
        });
    }

    @Test
    void itResolvesTestLib2WithoutTransitives(@TempDir Path tempDir) {
        // Given
        DynamicMavenResolverPublisher provisioner = provisionerForLocalMavenTestRepo(tempDir);
        // When
        Set<File> files = provisioner.provisionWithTransitives(false, "com.example:lib2:1.0.0");
        Set<String> fileNames = files.stream().map(File::getName).collect(Collectors.toSet());

        // Then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(files).isNotEmpty();
            softly.assertThat(fileNames).contains("lib2-1.0.0.jar").doesNotContain("lib1-1.0.0.jar");
        });
    }

    private static @NotNull DynamicMavenResolverPublisher provisionerForLocalMavenTestRepo(Path tempDir) {
        return new DynamicMavenResolverPublisher(localMavenTestRepo(), tempDir);
    }

    private static @NotNull List<RemoteRepository> localMavenTestRepo() {
        String localRepoPath = System.getProperty("local.maven.repo.path");
        if (localRepoPath == null || localRepoPath.isBlank()) {
            throw new IllegalStateException("System property 'local.maven.repo.path' must be set for this test.");
        }
        return List.of(
                new RemoteRepository.Builder("local-maven-test-repo", "default", "file://" + localRepoPath).build());
    }
}
