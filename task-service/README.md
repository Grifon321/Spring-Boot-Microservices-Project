# Task Service

This service manages the creation and maintaining the tasks.

## Table of Contents
- [Stack](#stack)
- [Usage](#usage)
- [API Endpoints](#api-endpoints)
- [Examples](#examples)

## Technologies
- Spring Boot
- PostgreSQL
- Kafka

## Usage
Make sure Kafka and PostgreSQL are running.
1. Build the project:
   ```bash
   mvn clean install
   ```
2. Run the project :
   ```bash
   mvn spring-boot:run
   ```

## API Endpoints
- POST /tasks/register: Register a new task.
- DELETE /tasks/{id} : Delete the task with the ID.
- PUT /tasks/{id}: Update the task by their ID.
- GET /tasks/showAll: Retrieve a list of all users.
- GET /tasks/{id}: Retrieve details of a task by their ID.
- PUT /tasks/addUserId/{id}: Add the userID of a task with the ID.
- PUT /tasks/removeUserId/{id}: Remove the userID of a task with the ID.
- GET /tasks/user/{userId}: Retrieve all tasks associated with userID.

## Examples
### POST /tasks/register
```
POST http://localhost:8084/tasks/register
```
with the following JSON body :
```
{
    "name" : "testname",
    "text" : "Some weird very very very very very very long text",
    "deadline" : "tomorrow"
}
```
### DELETE /tasks/{id}
```
POST http://localhost:8084/tasks/5
```
### PUT /tasks/{id}
Notice : make sure user with the id 1 exists
```
PUT http://localhost:8084/tasks/1
```
with the following JSON body :
```
{
    "name" : "testname",
    "text" : "Some smaller text",
    "deadline" : "today"
}
```
### GET /tasks/showAll
```
GET http://localhost:8084/tasks/showAll
```
### GET /tasks/{id}
```
GET http://localhost:8084/tasks/1
```
### PUT /tasks/addUserId/{id}
```
PUT http://localhost:8084/tasks/addUserId/1
```
with the following JSON body :
```
2
```
### PUT /tasks/removeUserId/{id}
```
PUT http://localhost:8084/tasks/removeUserId/1
```
with the following JSON body :
```
2
```
### GET /tasks/user/{userId}
```
GET http://localhost:8084/tasks/user/1
```