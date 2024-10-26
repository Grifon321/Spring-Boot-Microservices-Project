#!/bin/bash

cd user-service || { echo "Failed to navigate to user-service directory"; exit 1; }
mvn clean install

cd ../authentication-service || { echo "Failed to navigate to authentication-service directory"; exit 1; }
mvn clean install

cd ../task-service || { echo "Failed to navigate to task-service directory"; exit 1; }
mvn clean install

cd ../elasticsearch-service || { echo "Failed to navigate to elasticsearch-service directory"; exit 1; }
mvn clean install

cd ../eureka-server || { echo "Failed to navigate to eureka-server directory"; exit 1; }
mvn clean install

cd ../api-gateway || { echo "Failed to navigate to api-gateway directory"; exit 1; }
mvn clean install

cd ../web || { echo "Failed to navigate to web directory"; exit 1; }
mvn clean install

echo "Build process completed!"
