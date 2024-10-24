# Elasticsearch Service

This service manages the Elastic Search component of the project. It allows searching through the tasks via queries.
It also provides a simple API.

## Table of Contents
- [Stack](#stack)
- [Dependencies](#dependencies)
- [Usage](#usage)
- [API Endpoints](#api-endpoints)
- [Examples](#examples)


## Stack
- Spring Boot
- Elastic Search
- Kafka
- Docker

## Dependencies
- Spring Boot
- Task-Service
- ElasticSearch
- Kafka

## Usage
Make sure Elasticsearch and Kafka are running.
1. Build the project:
   ```bash
   mvn clean install
   ```
2. Run the project :
   ```bash
   mvn spring-boot:run
   ```
## API Endpoints
- GET /elastic/showAll: Retrieve a list of all tasks.
- GET /elastic/search?query={query}: Retrieve a list of all tasks found by the elasticsearch query.
- GET /elastic/{userId}/search?query={query}: Retrieve a list of all tasks found by the elasticsearch query which are associated with the userId.


## Examples
### GET /elastic/showAll
```
GET http://localhost:8085/elastic/showAll
```

### GET /elastic/search?query={query}
```
GET http://localhost:8085/elastic/search?query=text
```

### GET /elastic/{userId}/search?query={query}
```
GET http://localhost:8085/elastic/3/search?query=text
```



