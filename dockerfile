# Stage 1: Build JAR (optional if building locally)
FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run app
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /app/target/user-service-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
