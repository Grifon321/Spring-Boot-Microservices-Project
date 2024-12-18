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
- **POST /tasks**: Register a new task.
- **DELETE /tasks/{id}**: Delete the task with the specified `id`.
- **PUT /tasks/{id}**: Update the task by its `id`.
- **GET /tasks**: Retrieve a list of all tasks.
- **GET /tasks/{id}**: Retrieve details of a task by its `id`.
- **PATCH /tasks/{id}?**: Update a task with the given `id`. You can modify one or more of the following attributes by passing them in the query string (concatenated by `&`):
  - `userId={userId}&action={action}`: Add or remove the user ID from the task. 
    - `action` can be either `add` or `remove`.
  - `name={name}`: Change the name of the task.
  - `text={text}`: Change the text of the task.
  - `deadline={deadline}`: Change the deadline of the task.
  - `status={status}`: Change the status of the task. 
    - Valid status values: `"To Do"`, `"In Progress"`, `"Done"`.
- **GET /tasks/user/{userId}**: Retrieve all tasks associated with `userID`.


## Examples
### POST /tasks
```
POST http://localhost:8084/tasks
```
with following JSON body 
```json
{
    "name": "First task",
    "text": "Some text",
    "deadline": "today",
    "status": "To Do",
    "userIds": [
        1
    ]
}
```
### DELETE /tasks/{id}
```
http://localhost:8084/tasks/11
```
### PUT /tasks/{id}
```
http://localhost:8084/tasks/10
```
with following JSON body
```json
{
    "name": "Some new name",
    "text": "Some new text",
    "deadline": null,
    "status": "In Progress",
    "userIds": [
        2
    ]
}
```
### GET /tasks
```
GET http://localhost:8084/tasks
```
### GET /tasks/{id}
```
GET http://localhost:8084/tasks/10
```
### PATCH /tasks/{taskId}?
```
PATCH http://localhost:8084/tasks/1?userId=3&action=add
```
```
PATCH http://localhost:8084/tasks/1?userId=3&action=remove
```
```
PATCH http://localhost:8084/tasks/1?status=To Do&name=New Name
```
### GET /tasks/user/{userId}
```
GET http://localhost:8084/tasks/user/3
```