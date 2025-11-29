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
import java.util.stream.Collectors;

import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResult;
import org.eclipse.aether.supplier.RepositorySystemSupplier;
import org.eclipse.aether.supplier.SessionBuilderSupplier;
import org.eclipse.aether.util.artifact.JavaScopes;
import org.eclipse.aether.util.filter.DependencyFilterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.diffplug.spotless.Provisioner;
import com.diffplug.spotless.ThrowingEx;

public class SpotlessCliMavenProvisioner implements Provisioner {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpotlessCliMavenProvisioner.class);

    public static final List<RemoteRepository> DEFAULT_REMOTE_REPOSITORIES =
            List.of(new RemoteRepository.Builder("central", "default", "https://repo1.maven.org/maven2/").build());

    public static final Path DEFAULT_LOCAL_MAVEN_REPO = defaultLocalMavenRepo();

    private final List<RemoteRepository> remoteRepositories;

    private final Path localMavenRepo;

    public SpotlessCliMavenProvisioner(List<RemoteRepository> remoteRepositories, Path localMavenRepo) {
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
        SessionBuilderSupplier sessionBuilderSupplier = new SessionBuilderSupplier(system);
        return sessionBuilderSupplier
                .get()
                .withLocalRepositoryBaseDirectories(localMavenRepo)
                .build();
    }

    @Override
    public Set<File> provisionWithTransitives(boolean withTransitives, Collection<String> mavenCoordinates) {

        final ArtifactResolverFunc artifactResolverFunc =
                withTransitives ? new DependencyGraphResolverFunc() : new SingleArtifactResolverFunc();

        Set<File> jarFiles = mavenCoordinates.stream()
                .map(DefaultArtifact::new)
                .flatMap(ThrowingEx.wrap(artifact -> artifactResolverFunc.apply(artifact).stream()))
                .map(ArtifactResult::getArtifact)
                .map(Artifact::getPath)
                .map(Path::toFile)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        return jarFiles;
    }

    private interface ArtifactResolverFunc {
        List<ArtifactResult> apply(Artifact artifact) throws Exception;
    }

    private abstract class BaseArtifactResolverFunc implements ArtifactResolverFunc {
        protected final RepositorySystem repositorySystem;
        protected final RepositorySystemSession repositorySystemSession;
        protected final List<RemoteRepository> resolutionRepos;

        protected BaseArtifactResolverFunc() {
            this.repositorySystem = newRepositorySystem();
            this.repositorySystemSession = newSession(repositorySystem);
            this.resolutionRepos =
                    repositorySystem.newResolutionRepositories(repositorySystemSession, remoteRepositories);
        }
    }

    private class DependencyGraphResolverFunc extends BaseArtifactResolverFunc {

        @Override
        public List<ArtifactResult> apply(Artifact artifact) throws Exception {

            CollectRequest collectRequest = new CollectRequest();
            collectRequest.setRoot(new Dependency(artifact, JavaScopes.RUNTIME));
            collectRequest.setRepositories(remoteRepositories);

            DependencyFilter filter = DependencyFilterUtils.andFilter(
                    DependencyFilterUtils.classpathFilter(JavaScopes.RUNTIME), FilterOptionalDependencies.INSTANCE);

            DependencyRequest dependencyRequest = new DependencyRequest(collectRequest, filter);

            DependencyResult result = repositorySystem.resolveDependencies(repositorySystemSession, dependencyRequest);

            return result.getArtifactResults();
        }
    }

    private class SingleArtifactResolverFunc extends BaseArtifactResolverFunc {

        @Override
        public List<ArtifactResult> apply(Artifact artifact) throws Exception {
            ArtifactRequest request = new ArtifactRequest();
            request.setArtifact(artifact);
            remoteRepositories.forEach(request::addRepository);

            ArtifactResult result = repositorySystem.resolveArtifact(repositorySystemSession, request);
            return List.of(result);
        }
    }

    private static class FilterOptionalDependencies implements DependencyFilter {

        private static final FilterOptionalDependencies INSTANCE = new FilterOptionalDependencies();

        @Override
        public boolean accept(DependencyNode node, List<DependencyNode> parents) {
            Dependency dependency = node.getDependency();
            boolean isOptional = dependency.isOptional();
            if (isOptional) {
                LOGGER.debug("{} is optional, excluding from resolution", dependency);
            }
            return !isOptional;
        }
    }
}
