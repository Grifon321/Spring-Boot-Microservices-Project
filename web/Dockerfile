# Run
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/web-0.0.1-SNAPSHOT.jar /app/web.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "web.jar"]
