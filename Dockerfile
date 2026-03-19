# Multi-stage build
FROM maven:3.9.14-eclipse-temurin-25 AS build

WORKDIR /app
COPY pom.xml .
COPY src src

# Build with maven
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

# Copy the built jar
COPY --from=build /app/target/trie-autocomplete-1.0.0.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]