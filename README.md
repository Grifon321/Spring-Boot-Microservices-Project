# Spring Boot Microservices Project
## About this project
This projects is a demonstration of a micro service architecture which provides an API Endpoints via API Gateway for services like maintaining the users in a database, authentication of the users, maintaining the data in database - in this project Tasks -, and searching through the data via Elastic Search. Web component demonstrates how the API Gateway could be used in the backend.
![Authentication](README-images/Structure.png)

## Table of Contents
- [Stack](#stack)
- [Dependencies](#dependencies)
- [Web component](#web-component)
- [API Endpoints](#api-endpoints)
- [Examples](#examples)
- [Installation](#installation)
- [Usage](#usage-via-docker)
- [Usage for development](#usage-via-maven)

## Stack
- Maven
- Spring (Boot, Cloud, Security)
- JWT
- PostgreSQL
- Kafka
- Elasticsearch
- Eureka
- Docker
- SAP UI5

## Dependencies
* Core
    * Spring
        * Spring Boot
        * Spring Security
            * Spring Security JWT
            * Authentication
            * Validation
        * Spring Web
        * Spring Data
            * Spring Data JPA
            * PostgreSQL
        * Spring Cloud
    * Netflix
        * Eureka Server
        * Eureka Client
* Database
    * PostgreSQL
* Kafka
* Elasticsearch
* Docker

### Web component
The functionality of the website can be demonstrated with the following gif :
![Authentication](README-images/Web%20demonstration.gif)

## API Endpoints
All the respective examples of the usage can be found in the README files of the respective projects.
### User Service :
<table style="width:100%">
  <tr>
      <th>Method</th>
      <th>Url</th>
      <th>Description</th>
  </tr>
  <tr>
      <td>POST</td>
      <td>/users</td>
      <td>Register a new user</td>
  </tr>
  <tr>
      <td>GET</td>
      <td>/users</td>
      <td>Retrieve a list of all users</td>
  </tr>
  <tr>
      <td>DELETE</td>
      <td>/users/{id}</td>
      <td>Delete the user by their `id`</td>
  </tr>
  <tr>
      <td>GET</td>
      <td>/users/{id}</td>
      <td>Retrieve details of a user by their `id`</td>
  </tr>
  <tr>
      <td>PUT</td>
      <td>/users/{id}</td>
      <td>Update the user by their `id`</td>
  </tr>
  <tr>
      <td>GET</td>
      <td>/users/password/{username}</td>
      <td>Retrieve the password of a user by their `username`</td>
  </tr>
  <tr>
      <td>GET</td>
      <td>/users/id-by-username/{username}</td>
      <td>Retrieve the id of a user by their `username`</td>
  </tr>
</table>

### Authentication Service :
<table style="width:100%">
  <tr>
      <th>Method</th>
      <th>Url</th>
      <th>Description</th>
  </tr>
  <tr>
      <td>POST</td>
      <td>/auth/authenticate</td>
      <td>Authenticate the user and get JWT token</td>
  </tr>
  <tr>
      <td>GET</td>
      <td>/auth/validate</td>
      <td>Validate the user by JWT token</td>
  </tr>
</table>

### Task Service :
<table style="width:100%">
  <tr>
      <th>Method</th>
      <th>Url</th>
      <th>Description</th>
  </tr>
  <tr>
      <td>POST</td>
      <td>/tasks</td>
      <td>Register a new task</td>
  </tr>
  <tr>
      <td>DELETE</td>
      <td>/tasks/{id}</td>
      <td>Delete the task with the specified `id`</td>
  </tr>
  <tr>
      <td>PUT</td>
      <td>/tasks/{id}</td>
      <td>Update the task by its `id`</td>
  </tr>
  <tr>
      <td>GET</td>
      <td>/tasks</td>
      <td>Retrieve a list of all tasks</td>
  </tr>
  <tr>
      <td>GET</td>
      <td>/tasks/{id}</td>
      <td>Retrieve details of a task by its `id`</td>
  </tr>
  <tr>
      <td>PATCH</td>
      <td>/tasks/{id}?</td>
      <td>Update a task with the given `id`. You can modify one or more of the following attributes by passing them in the query string (concatenated by `&`):
        <ul>
          <li><strong>userId={userId}&action={action}</strong>: Add or remove the user ID from the task. `action` can be either `add` or `remove`.</li>
          <li><strong>name={name}</strong>: Change the name of the task.</li>
          <li><strong>text={text}</strong>: Change the text of the task.</li>
          <li><strong>deadline={deadline}</strong>: Change the deadline of the task.</li>
          <li><strong>status={status}</strong>: Change the status of the task. Valid status values: "To Do", "In Progress", "Done".</li>
        </ul>
      </td>
  </tr>
  <tr>
      <td>GET</td>
      <td>/tasks/user/{id}</td>
      <td>Retrieve all tasks associated with `userID`</td>
  </tr>
</table>

### Elastic Search Service :
<table style="width:100%">
  <tr>
      <th>Method</th>
      <th>Url</th>
      <th>Description</th>
  </tr>
  <tr>
      <td>GET</td>
      <td>/elastic/showAll</td>
      <td>Retrieve a list of all tasks indexed by ES</td>
  </tr>
  <tr>
      <td>GET</td>
      <td>/elastic/search?query={query}</td>
      <td>Retrieve a list of all tasks found by ES using query `query`</td>
  </tr>
  <tr>
      <td>GET</td>
      <td>/elastic/{userId}/search?query={query}</td>
      <td>Retrieve a list of all tasks found by ES using query `query` of a user with `userID`</td>
  </tr>
</table>

## Examples
Let's say we want to create a new user, authenticate the user and create a task. Examples are done via Postman.
### 1. Creating the user
```
POST http://localhost:8081/users
```
with the following JSON body :
```
{
  "username": "First User",
  "password": "securePassword"
}
```
![Registration](README-images/Registration.png)
### 2. Authenticating the user
```
POST http://localhost:8081/auth/authenticate
```
with the following JSON body :
```
{
  "username": "Very First User",
  "password": "securePassword",
  "email": "someEmail@mail.me
}
```
The post request grants us a JWT Token, e.g. 
`eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJGaXJzdCBVc2VyIiwiaWF0IjoxNzM0NjI5Mjk2LCJleHAiOjE3MzQ2Mjk4OTZ9.xRVeiwHDFNNhE116dVjxB4FKXqcbrO_80In4cOz3MnM`
![Authentication](README-images/Authentication.png)
### 3. Creating a task 
```
POST http://localhost:8081/tasks/register
```
with the following header :\
`"key":"Authorization"`\
`"value":"Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJGaXJzdCBVc2VyIiwiaWF0IjoxNzM0NjI5Mjk2LCJleHAiOjE3MzQ2Mjk4OTZ9.xRVeiwHDFNNhE116dVjxB4FKXqcbrO_80In4cOz3MnM"`

and with the following JSON body :
```
{
  "name": "Some name",
  "text": "Some text"
}
```
As the responce we get a confirmation as a Task Object in JSON.
![Authentication](README-images/Task%20creation.png)

## Installation
- Make sure Apache Maven is installed on the machine to compile all the files and via `mvn clean install` in all of the maven project directories :
user-service/, authentication-service/, elasticsearch-service/, eureka-server/, task-service/, web-backend/, api-gateway/
- For web component make sure SAP UI5 with minimal version of 1.60 is installed.
## Usage (via docker)
Make sure MAVEN is installed as well as docker client and then run docker :
```
docker-compose up --build
```
Docker does not include web. Web component has to be launched separately via :
```
cd web-backend
mvn spring-boot:run
```
for backend and 
```
cd web-frontend/web
ui5 serve
```
for frontend.
The API-Gateway can be accessed on port 8081, the web can be accessed on port `localhost:8080/login.html`
## Usage (via maven)
Make sure MAVEN is installed as well as docker client. Alternatively, PostgreSQL, Kafka and Elasticsearch can be launched manually instead of creating docker image.
### 1. PostgreSQL, Kafka, Elasticsearch
Make sure PostgreSQL is launched with following parameters on port 5432 with following parameters :
```
      POSTGRES_DB: postgres
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: admin
```
Kafka as well as zookeeper have to be running on ports 9092 and 2181 accordingly.
Elasticsearch has to be running on port 9200.
### 2. Services
- Make sure `mvn clean install` has been executed in all of the maven project directories and start each of the services via `mvn spring-boot:run`:
user-service/, authentication-service/, elasticsearch-service/, eureka-server/, task-service/, web-backend/, api-gateway/
- For elasticsearch-service make sure elastic search is running via sending POST request to `http://localhost:9200` before launching elasticsearch-service.
- For web component launch it with `ui5 serve` from web-frontend/web/
The API-Gateway can be accessed on port 8081, the web can be accessed on port `localhost:8080/login.html`