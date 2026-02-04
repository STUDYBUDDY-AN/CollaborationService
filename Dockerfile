FROM eclipse-temurin:25-jdk AS build
WORKDIR /app
COPY . .
RUN chmod +x gradlew
# Disable toolchain detection to force using the JDK from the image
RUN ./gradlew build --no-daemon -x test -Porg.gradle.java.installations.auto-detect=false -Porg.gradle.java.installations.fromEnv=JAVA_HOME

FROM eclipse-temurin:25-jdk
WORKDIR /app
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
