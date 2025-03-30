# Description: A minimal Dockerfile for running/building spotless-cli and tests in a GraalVM environment.
# --- Stage 1: Build/Install GraalVM ---
FROM ghcr.io/graalvm/graalvm-community:21.0.2 AS graalvm-builder

# --- Stage 2: Minimal Runtime Image ---
FROM ubuntu:22.04

# System packages you need at runtime
RUN apt-get update && \
    apt-get install -y \
      libssl-dev \
      libc6 \
      npm \
    && rm -rf /var/lib/apt/lists/*

# Copy GraalVM from the first stage
COPY --from=graalvm-builder /opt/graalvm-community-java21 /opt/graalvm

# Set up environment
ENV GRAALVM_HOME=/opt/graalvm
ENV PATH="${GRAALVM_HOME}/bin:${PATH}"

