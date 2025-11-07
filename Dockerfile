# ====== Stage 1: Build the project ======
FROM maven:3.8.7-eclipse-temurin-17 AS build

# Copy project source into image
WORKDIR /app
COPY . .

# Build the Spring Boot app (skip tests for faster build)
RUN mvn clean package -DskipTests

# ====== Stage 2: Run the project ======
FROM eclipse-temurin:17-jdk-jammy

# Set working directory
WORKDIR /app

# Copy built jar from previous stage
COPY --from=build /app/target/*.jar app.jar

# Expose the same port as your Spring Boot app
EXPOSE 9090

# Start the Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]
