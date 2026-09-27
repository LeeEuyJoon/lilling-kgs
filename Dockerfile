# Multi-stage build for lilling-kgs

# Stage 1: Build
FROM gradle:8-jdk17 AS build
WORKDIR /app

# Copy Gradle wrapper and build files first for caching
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Download dependencies (cached layer)
RUN gradle dependencies --no-daemon || true

# Copy source code
COPY src src

# Build the application (skip tests for faster build)
RUN gradle clean build -x test --no-daemon

# Stage 2: Runtime
FROM amazoncorretto:17-alpine
WORKDIR /app

# Download OpenTelemetry Java agent
RUN wget -O otel-agent.jar https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar

# Copy the built JAR from build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose port
EXPOSE 8081

# Run the application
ENTRYPOINT ["java", "-javaagent:/app/otel-agent.jar", "-jar", "app.jar"]
