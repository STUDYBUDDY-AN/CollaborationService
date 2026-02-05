# ==============================
# BUILD STAGE
# ==============================
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

# Copy Gradle infra first for layer caching
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon \
  -Porg.gradle.java.installations.auto-detect=false \
  -Porg.gradle.java.installations.fromEnv=JAVA_HOME

# Copy source last
COPY src src

RUN ./gradlew bootJar --no-daemon -x test \
  -Porg.gradle.java.installations.auto-detect=false \
  -Porg.gradle.java.installations.fromEnv=JAVA_HOME


# ============================== 
# RUNTIME STAGE
# ============================== 
FROM eclipse-temurin:25-jre
WORKDIR /app

# Runtime utilities (curl for healthchecks/debug)
RUN apt-get update \
 && apt-get install -y curl \
 && rm -rf /var/lib/apt/lists/*

# JVM FLAGS (container-safe, Java 25)
ENV JAVA_TOOL_OPTIONS="\
-XX:MaxRAMPercentage=70.0 \
-XX:+ExitOnOutOfMemoryError"

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]