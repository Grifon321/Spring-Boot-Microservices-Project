# Build stage
FROM maven:3.8.3-openjdk-17 AS build
WORKDIR /app
COPY pom.xml /app/
COPY src /app/src/
RUN mvn clean install -DskipTests

# Runtime stage
FROM azul/zulu-openjdk-alpine:17-latest
WORKDIR /app
COPY --from=build /app/target/user-service-0.0.1-SNAPSHOT.jar /app/user-service.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "user-service.jar"]