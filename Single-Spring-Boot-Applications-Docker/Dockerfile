# Use an OpenJDK image for Java
FROM openjdk:17-jdk-slim

# Install Supervisor to manage multiple processes
RUN apt-get update && apt-get install -y supervisor

# Create a directory for the applications
WORKDIR /app

# Copy all Spring Boot JARs from the respective application directories into the container
COPY ../api-gateway/target/api-gateway-0.0.1-SNAPSHOT.jar /app/api-gateway.jar
COPY ../authentication-service/target/authentication-service-0.0.1-SNAPSHOT.jar /app/authentication-service.jar
COPY ../elasticsearch-service/target/elasticsearch-service-0.0.1-SNAPSHOT.jar /app/elasticsearch-service.jar
COPY ../eureka-server/target/eureka-server-0.0.1-SNAPSHOT.jar /app/eureka-server.jar
COPY ../task-service/target/task-service-0.0.1-SNAPSHOT.jar /app/task-service.jar
COPY ../user-service/target/user-service-0.0.1-SNAPSHOT.jar /app/user-service.jar
#COPY ../web/target/web-0.0.1-SNAPSHOT.jar /app/web.jar


# Create a directory for logs
RUN mkdir /var/log/springboot

# Copy the supervisor configuration to the container
COPY Single-Spring-Boot-Applications-Docker/supervisord.conf /etc/supervisord.conf


# Expose the ports that will be used by the Spring Boot apps
EXPOSE 8082 8083

# Run supervisor to manage the processes
CMD ["/usr/bin/supervisord", "-c", "/etc/supervisord.conf"]
