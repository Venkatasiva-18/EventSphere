# Use OpenJDK 21 as the base image
FROM openjdk:21-jdk-slim

# Set working directory inside the container
WORKDIR /app

# Copy all project files into the container
COPY . .

# Make mvnw executable
RUN chmod +x mvnw

# Build the Spring Boot project
RUN ./mvnw clean package

# Expose port 8080
EXPOSE 8080

# Run the generated jar
CMD ["java", "-jar", "target/*.jar"]
