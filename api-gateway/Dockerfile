# Build stage
FROM maven:3.8.3-openjdk-17 AS build
WORKDIR /app
COPY pom.xml /app/
COPY src /app/src/
RUN mvn clean install -DskipTests

# Runtime stage
FROM azul/zulu-openjdk-alpine:17-latest
WORKDIR /app
COPY --from=build /app/target/api-gateway-0.0.1-SNAPSHOT.jar /app/api-gateway.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "api-gateway.jar"]