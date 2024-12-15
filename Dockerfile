# Use an OpenJDK base image
FROM openjdk:22-jdk-slim



# Install tools
RUN apt-get update && apt-get install -y netcat-openbsd iputils-ping && rm -rf /var/lib/apt/lists/*


# Set the working directory
WORKDIR /app

# Copy the JAR file built by Maven
COPY target/kafka-app.jar app.jar

# Expose the application's port
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
