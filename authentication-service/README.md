# Authentication Service

This service authenticates the user via username and password which are stored in user-service.
It also provides a simple API.

## Table of Contents
- [Stack](#stack)
- [Dependencies](#dependencies)
- [Usage](#usage)
- [API Endpoints](#api-endpoints)
- [Examples](#Examples)


## Stack
- Spring Boot
- Spring Security
- JWT

## Dependencies
- User-Service

## Usage
Make sure user-service is running.
1. Build the project:
   ```bash
   mvn clean install
   ```
2. Run the project :
   ```bash
   mvn spring-boot:run
   ```

## API Endpoints
- POST /auth/authenticate: Authenticate the user and provides JWT bearer token.
- GET /auth/validate: Validate the user.

## Examples
### POST /auth/authenticate
```
POST http://localhost:8083/auth/authenticate
```
with the following JSON body :
```
{
    "username": "newuser",
    "password": "securePassword"
}
```
It will send back the jwt_tocken back.
### GET /auth/validate
```
GET http://localhost:8083/auth/validate
```
with the following headers :
```
   "authentication": "Bearer your_jwt_tocken_from_authentication",
```