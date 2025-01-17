FROM maven:3.8.7-eclipse-temurin-17 AS producer

WORKDIR /build

# Copy Maven config and source files
COPY pom.xml ./
COPY src ./src

# Cache dependencies and build the application
RUN --mount=type=cache,target=/root/.m2 \
    mvn package -DskipTests

# Runtime container
FROM eclipse-temurin:17-jre AS runtime

WORKDIR /app

# Copy dependencies
COPY --from=producer /build/target/dependency/ ./lib
# Copy application JAR
COPY --from=producer /build/target/*.jar ./app.jar

# Add all dependencies to the classpath and run the application
CMD ["java", "-cp", "app.jar:lib/*", "com.buckl3y.Main"]
