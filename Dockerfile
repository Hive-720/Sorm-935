# Multi-stage Dockerfile for Spring Boot (Gradle) - Java 21
# Build stage
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /workspace

# Leverage layer caching: copy only files that affect dependency resolution first
COPY gradle gradle
COPY gradlew settings.gradle build.gradle ./

# Ensure gradlew is executable
RUN sed -i 's/\r$//' gradlew && \
    chmod +x ./gradlew

# Prepare dependencies (this will cache Gradle dependencies)
RUN ./gradlew --no-daemon dependencies > /dev/null 2>&1 || true

# Now copy the rest of the source
COPY src src

# Build the Spring Boot fat jar
RUN ./gradlew --no-daemon clean bootJar

# Runtime stage
FROM eclipse-temurin:21-jre-noble
WORKDIR /app

# Environment for JVM tuning can be passed via JAVA_OPTS
ENV JAVA_OPTS=""

# Install curl for Docker HEALTHCHECK
USER root
RUN apt-get update && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

# Copy the built jar from the builder stage
# The artifact name is derived from settings.gradle (rootProject.name) and version in build.gradle
# Result: build/libs/sorm-server-935-1.0.0-SNAPSHOT.jar
COPY --from=builder /workspace/build/libs/*.jar /app/app.jar

# Non-root userDb for better security (UID chosen arbitrarily)
RUN useradd -u 10001 -r -s /usr/sbin/nologin spring && mkdir ./logs && chown -R 10001:0 /app
USER 10001

# Application listens on 9931 (per src/main/resources/application.yaml)
# Management (actuator, including Prometheus/health) listens on 9990 (HTTP, no mTLS)
EXPOSE 9931 9990

# Docker health check: rely on actuator health endpoint (HTTP 200 when UP)
HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD curl --fail http://localhost:9990/actuator/health || exit 1

# If you want to override SSL or profiles at runtime, pass ENV/args like:
#   docker run -e JAVA_OPTS="-Dspring.profiles.active=prod" ...
# Note: SSL in this app requires clientDb certificate (clientDb-auth=need). Health checks without certs will fail.

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
