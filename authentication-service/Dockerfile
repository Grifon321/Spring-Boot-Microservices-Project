# Run
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/authentication-service-0.0.1-SNAPSHOT.jar /app/authentication-service.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "authentication-service.jar"]
