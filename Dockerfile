# Stage 1: Build the application using Maven
FROM maven:3.8.5-openjdk-17 AS builder

# Set the working directory
WORKDIR /app

# Force UTF-8 encoding for the JVM
ENV MAVEN_OPTS="-Dfile.encoding=UTF-8"

# Copy the pom.xml and the source code
COPY pom.xml .
COPY src ./src

# Build the project, skipping tests to speed up the process
RUN mvn clean install -DskipTests

# Stage 2: Create the final, lightweight image
# Use a current and available Java 17 image
FROM eclipse-temurin:17-jre-jammy

# Set the working directory
WORKDIR /app

# Copy the JAR file from the builder stage
# The JAR file name is determined by the <artifactId> and <version> in pom.xml
COPY --from=builder /app/target/PE-0.0.1-SNAPSHOT.jar app.jar

# Expose the port the application runs on
EXPOSE 8080

# Set the command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
