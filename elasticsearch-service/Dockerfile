# Build stage
FROM maven:3.8.3-openjdk-17 AS build
WORKDIR /app
COPY pom.xml /app/
COPY src /app/src/
RUN mvn clean install -DskipTests

# Runtime stage
FROM azul/zulu-openjdk-alpine:17-latest
WORKDIR /app
COPY --from=build /app/target/elasticsearch-service-0.0.1-SNAPSHOT.jar /app/elasticsearch-service.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "elasticsearch-service.jar"]