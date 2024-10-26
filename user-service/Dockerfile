# Run
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/user-service-0.0.1-SNAPSHOT.jar /app/user-service.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "user-service.jar"]
# Alternative 
FROM eclipse-temurin:17-jdk-alpine
