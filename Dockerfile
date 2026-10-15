# syntax=docker/dockerfile:1

# Build stage: compile the Spring Boot jar with the project's Gradle wrapper
FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace

# Copy the wrapper and build files first so dependency downloads are cached
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle settings.gradle ./
RUN ./gradlew --no-daemon dependencies > /dev/null 2>&1 || true

COPY src ./src
RUN ./gradlew --no-daemon bootJar \
    && rm -f build/libs/*-plain.jar

# Runtime stage: slim JRE image, non-root user
FROM eclipse-temurin:21-jre
WORKDIR /app

RUN useradd --system --uid 1001 --shell /usr/sbin/nologin spring
COPY --from=build /workspace/build/libs/*.jar app.jar
USER spring

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
