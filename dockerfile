# =========================
# Stage 1: Build the JAR
# =========================
FROM maven:3.9.4-eclipse-temurin-21 AS build

WORKDIR /app

# Copy pom.xml and pre-download dependencies to leverage Docker cache
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application (skip tests to speed up container builds)
RUN mvn clean package -DskipTests -DfinalName=app

# ============================================
# Stage 2: Run with Distroless
# ============================================
FROM gcr.io/distroless/java21-debian12
WORKDIR /app

# Copy the fat JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]