# Use OpenJDK 21 as the base image
FROM openjdk:21-jdk-slim

# Set working directory inside the container
WORKDIR /app

# Copy all project files into the container
COPY . .

# Build the Spring Boot project
RUN ./mvnw clean package

# Expose port 8080 (Spring Boot default)
EXPOSE 8080

# Run the generated jar file
CMD ["java", "-jar", "target/*.jar"]
