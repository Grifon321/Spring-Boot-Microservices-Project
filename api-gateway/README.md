# API Gateway 

This service manages all the internal APIs and does the API Gateway.

## Table of Contents
- [Stack](#stack)
- [Dependencies](#dependencies)
- [Usage](#usage)
- [API Endpoints](#api-endpoints)

## Stack
- Spring Boot
- Spring Cloud
- Netflix Eureka

## Dependencies
- user-service
- authentication-service
- eureka-server
- taks-service
- elasticsearch-service

## Usage
1. Build the project:
   ```bash
   mvn clean install
   ```
2. Run the project :
   ```bash
   mvn spring-boot:run
   ```
## API Endpoints
This API-Gateway has all the endpoints from the API-Endpoints of the microservices
- POST /users/register: Register a new user.
- POST /auth/authenticate: Authenticate the user. Post request gives JWT-Tocken as response.
- POST /auth/validate: Validate the user.

All other API Endpoints require an authorization header sent to even be forwarded further :
```
"Key" : Authorization
"Value" : "Bearer JWT_TOCKEN_GENERATED_VIA_AUTHENTICATE"