# Build the Spring Boot application with Maven, then run only the resulting JAR.
FROM maven:3.9.11-eclipse-temurin-21 AS build

WORKDIR /app
COPY pom.xml ./
RUN mvn -q -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app
COPY --from=build /app/target/campus-crate-backend-0.0.1-SNAPSHOT.jar app.jar

# Render supplies PORT automatically; 8080 is a sensible local Docker default.
ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
