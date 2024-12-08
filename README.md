# Spring Boot Microservices Project
## About this project
This projects is a demonstration of a micro service architecture which provides an API Endpoints via API Gateway for services like maintaining the users in a database, authentication of the users, maintaining the data in database - in this project Tasks -, and searching through the data via Elastic Search. Web component demonstrates how the API Gateway could be used in the backend.
![Authentication](README-images/Structure.png)

## Table of Contents
- [Stack](#stack)
- [Dependencies](#dependencies)
- [Installation](#installation)
- [Usage](#usage)
- [Usage for development](#usage-for-development)
- [API Endpoints](#api-endpoints)
- [Examples](#examples)
- [Web component](#web-component)

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
* Lombok
* Docker

## Installation
- Make sure Apache Maven is installed on the machine to compile all the files and run `build.sh` or alternatively run `mvn clean install` in all of the maven project directories :
user-service/, authentication-service/, elasticsearch-service/, eureka-server/, task-service/, web-backend/
- For web component make sure SAP UI5 is installed.
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
### 2. Eureka
```
cd Eureka-Server
mvn spring-boot:run
```
### 3. user-service
```
cd user-service
mvn spring-boot:run
```
### 4. authentication-service
```
cd authentication-service
mvn spring-boot:run
```
### 5. task-service
```
cd task-service
mvn spring-boot:run
```
### 6. elasticsearch-service
Make sure elastic search is running via sending POST request to `http://localhost:9200` before launching elasticsearch-service
```
cd elasticsearch-service
mvn spring-boot:run
```
### 7. api-gateway
```
cd api-gateway
mvn spring-boot:run
```
### 8. web-backend
```
cd web-backend
mvn spring-boot:run
```
### 9. web-frontend
```
cd web-frontend/web
ui5 serve
```
The API-Gateway can be accessed on port 8081, the web can be accessed on port `localhost:8080/login.html`

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
      <td>/users/register</td>
      <td>Register for User</td>
  </tr>
  <tr>
      <td>PUT</td>
      <td>/users/{id}</td>
      <td>Update the user by {id}</td>
  </tr>
  <tr>
      <td>GET</td>
      <td>/users/</td>
      <td>Get a list of all users</td>
  </tr>
  <tr>
      <td>GET</td>
      <td>/users/{id}</td>
      <td>Get details of the user by {id}</td>
  </tr>
  <tr>
      <td>GET</td>
      <td>/users/username/{username}</td>
      <td>Get details of a user by {username}</td>
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
      <td>POST</td>
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
      <td>/tasks/register</td>
      <td>Register a new task</td>
  </tr>
  <tr>
      <td>DELETE</td>
      <td>/tasks/{id}</td>
      <td>Delete the task by {id}</td>
  </tr>
  <tr>
      <td>PUT</td>
      <td>/tasks/{id}</td>
      <td>Update the task by {id}</td>
  </tr>
  <tr>
      <td>GET</td>
      <td>/tasks/showAll</td>
      <td>Get a list of all tasks</td>
  </tr>
  <tr>
      <td>GET</td>
      <td>/tasks/{id}</td>
      <td>Get the task by {id}</td>
  </tr>
  <tr>
      <td>PUT</td>
      <td>/tasks/addUserId/{id}</td>
      <td>Add the userID to the task with {id}</td>
  </tr>
  <tr>
      <td>PUT</td>
      <td>/tasks/removeUserId/{id}</td>
      <td>Remove the userID to the task with {id}</td>
  </tr>
  <tr>
      <td>GET</td>
      <td>/tasks/uesr/{id}</td>
      <td>Get all the task assigned to the user with {id}</td>
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
      <td>Retrieve a list of all tasks found by ES using query {query}</td>
  </tr>
  <tr>
      <td>GET</td>
      <td>/elastic/{userId}/search?query={query}</td>
      <td>Retrieve a list of all tasks found by ES using query {query} of a user with {userID}</td>
  </tr>
</table>

## Examples
Let's say we want to create a new user, authenticate the user and create a task. Examples are done via Postman.
### 1. Creating the user
```
POST http://localhost:8081/users/register
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
`eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJGaXJzdCBVc2VyIiwiaWF0IjoxNzI5NzA1Mzk5LCJleHAiOjE3Mjk3MDU5OTl9.tVMOSlcxK_yJFSRQXZ_J9zgja7vFVPAl3WJ8qQPS4YQ`
![Authentication](README-images/Authentication.png)
### 3. Creating a task 
```
POST http://localhost:8081/tasks/register
```
with the following header :\
`"key":"Authorization"`\
`"value":"Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJGaXJzdCBVc2VyIiwiaWF0IjoxNzI5NzA1Mzk5LCJleHAiOjE3Mjk3MDU5OTl9.tVMOSlcxK_yJFSRQXZ_J9zgja7vFVPAl3WJ8qQPS4YQ"`
![Authentication](README-images/Task%20creation%201.png)

and with the following JSON body :
```
{
  "name": "First Task",
  "text": "Here is a small description"
}
```
As the responce we get a confirmation as a Task Object in JSON:

![Authentication](README-images/Task%20creation%202.png)

### Web component
The functionality of the website can be demonstrated with the following gif :
![Authentication](README-images/Web%20demonstration.gif)