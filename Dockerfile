# Use OpenJDK 21 as the base image
FROM openjdk:21-jdk-slim

# Set working directory
WORKDIR /app

# Copy all project files
COPY . .

# Make mvnw executable
RUN chmod +x mvnw

# Build the Spring Boot project WITHOUT running tests
RUN ./mvnw clean package -DskipTests

# Expose port 8080
EXPOSE 8080

# Run the generated jar (wildcard works in shell form)
CMD ["sh", "-c", "java -jar target/*.jar"]

