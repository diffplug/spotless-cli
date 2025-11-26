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
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResult;
import org.eclipse.aether.supplier.RepositorySystemSupplier;
import org.eclipse.aether.util.artifact.JavaScopes;
import org.eclipse.aether.util.filter.DependencyFilterUtils;

import com.diffplug.spotless.Provisioner;

public class DynamicMavenResolverPublisher implements Provisioner {

    public static final List<RemoteRepository> DEFAULT_REMOTE_REPOSITORIES =
            List.of(new RemoteRepository.Builder("central", "default", "https://repo1.maven.org/maven2/").build());

    public static final Path DEFAULT_LOCAL_MAVEN_REPO = defaultLocalMavenRepo();

    private final List<RemoteRepository> remoteRepositories;

    private final Path localMavenRepo;

    public DynamicMavenResolverPublisher(List<RemoteRepository> remoteRepositories, Path localMavenRepo) {
        this.remoteRepositories = remoteRepositories;
        this.localMavenRepo = localMavenRepo;
    }

    private static Path defaultLocalMavenRepo() {
        // 1) Respect standard Maven override if present
        String explicit = System.getProperty("maven.repo.local");
        if (explicit != null && !explicit.isBlank()) {
            return Path.of(explicit);
        }

        // 2) Maven's default: ${user.home}/.m2/repository
        String userHome = System.getProperty("user.home");
        return Path.of(userHome, ".m2", "repository");
    }

    private RepositorySystem newRepositorySystem() {
        return new RepositorySystemSupplier().get();
    }

    private RepositorySystemSession newSession(RepositorySystem system) {
        DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();
        LocalRepository localRepo = new LocalRepository(localMavenRepo.toFile());
        session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo));
        return session;
    }

    @Override
    public Set<File> provisionWithTransitives(boolean withTransitives, Collection<String> mavenCoordinates) {
        RepositorySystem repositorySystem = newRepositorySystem();
        RepositorySystemSession repositorySystemSession = newSession(repositorySystem);
        Set<File> jarFiles = new LinkedHashSet<>();

        for (String coord : mavenCoordinates) {
            Artifact artifact = new DefaultArtifact(coord);

            try {
                if (withTransitives) {
                    // Resolve full dependency graph
                    CollectRequest collectRequest = new CollectRequest();
                    collectRequest.setRoot(new Dependency(artifact, JavaScopes.RUNTIME));
                    remoteRepositories.forEach(collectRequest::addRepository);

                    DependencyRequest dependencyRequest = new DependencyRequest(
                            collectRequest, DependencyFilterUtils.classpathFilter(JavaScopes.RUNTIME));

                    DependencyResult result =
                            repositorySystem.resolveDependencies(repositorySystemSession, dependencyRequest);

                    result.getArtifactResults().forEach(r -> {
                        File f = r.getArtifact().getFile();
                        if (f != null) jarFiles.add(f);
                    });

                } else {
                    // Resolve just the main artifact
                    ArtifactRequest request = new ArtifactRequest();
                    request.setArtifact(artifact);
                    remoteRepositories.forEach(request::addRepository);

                    ArtifactResult result = repositorySystem.resolveArtifact(repositorySystemSession, request);
                    jarFiles.add(result.getArtifact().getFile());
                }

            } catch (Exception e) {
                throw new RuntimeException("Failed to resolve: " + coord, e);
            }
        }

        return jarFiles;
    }
}
