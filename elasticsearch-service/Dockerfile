# Run
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/elasticsearch-service-0.0.1-SNAPSHOT.jar /app/elasticsearch-service.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "elasticsearch-service.jar"]
