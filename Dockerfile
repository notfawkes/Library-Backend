# Build Stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests

# Run Stage
FROM eclipse-temurin:17-jre-focal
WORKDIR /app
COPY --from=build /app/target/library-management-0.0.1-SNAPSHOT.jar app.jar

# Ensure Spring Boot uses Render's PORT env
ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=$PORT"]
