# Stage 1: Build the application using JDK 17
FROM eclipse-temurin:17-jdk-focal AS build
WORKDIR /app
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

# Build the project, skipping tests
RUN ./mvnw clean package -DskipTests

# Stage 2: Create the final, smaller runtime image using JRE 17
FROM eclipse-temurin:17-jre-focal
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]