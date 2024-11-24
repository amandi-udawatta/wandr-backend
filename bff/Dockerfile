# Use an official Maven image with JDK 22 as a build stage
FROM eclipse-temurin:22-jdk-alpine AS build

# Set the working directory in the container
WORKDIR /app

# Copy the project files into the container
COPY . .

# Run the Maven build to create the JAR file
RUN ./mvnw clean package -DskipTests

# Use an official Eclipse Temurin JDK 22 runtime image
FROM eclipse-temurin:22-jre-alpine

# Set the working directory in the runtime container
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port the application runs on
EXPOSE 8081

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
