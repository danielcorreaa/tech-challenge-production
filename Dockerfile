FROM eclipse-temurin:17-jdk-alpine
# Setting up work directory
WORKDIR /app

# Copy the jar file into our app
COPY  target/tech-challenge-production-*.jar tech-challenge-production.jar

# Exposing port 8080
EXPOSE 8080

# Starting the application
CMD ["java", "-jar", "tech-challenge-production.jar"]