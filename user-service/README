# User Service

This service manages the creation of the users and their changes.

## Table of Contents
- [Stack](#stack)
- [Usage](#usage)
- [API Endpoints](#api-endpoints)
- [Examples](#Examples)


## Stack
- Spring Boot
- PostgreSQL

## Usage
Make sure PostgreSQL is running.
1. Build the project:
   ```bash
   mvn clean install
   ```

2. Run the project :
   ```bash
   mvn spring-boot:run
   ```
   
## API Endpoints
- POST /users/register: Register a new user.
- PUT /users/{id}: Update the profile of a user by their ID.
- GET /users: Retrieve a list of all users.
- GET /users/{id}: Retrieve details of a user by their ID.
- GET /users/username/{username}: Retrieve details of a user by their Username.

## Examples
### POST /users/register
```
POST http://localhost:8082/users/register
```
with the following JSON body :
```
{
    "username" : "FirstUser",
    "password" : "some password",
    "email" : "email"
}
```
### PUT /users/{id}
Notice : make sure user with the id 1 exists
```
PUT http://localhost:8082/users/1
```
with the following JSON body :
```
   "username": "changedUsername",
   "password" : "securePassword",
   "email" : "randomeMail@mgail.com"
```
### GET /users/showAll
```
GET http://localhost:8082/users/showAll
```

### GET /users/{id}
```
GET http://localhost:8082/users/1
```

### GET /users/username/{username}
```
GET http://localhost:8082/users/username/changedUsername
```