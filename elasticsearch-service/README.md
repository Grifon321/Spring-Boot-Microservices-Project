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

Query has to be written in the following format :
```
query=WORD1 WORD2 %7C%7C WORD3 WORD4
```
Spaces (or alternatively `%20`) are the AND delimeters, `%7C%7C` are the OR delimeters for the search.
The exmaple query = description big%7C%7Csmall, userId = 2 is translated into following logic :
```json
{
  "query": {
    "bool": {
      "should": [
        {
          "bool": {
            "must": [
              { "match": { "text": { "query": "description", "operator": "AND" } } },
              { "match": { "text": { "query": "big", "operator": "AND" } } }
            ]
          }
        },
        {
          "bool": {
            "must": [
              { "match": { "text": { "query": "small", "operator": "AND" } } }
            ]
          }
        }
      ],
      "minimum_should_match": 1,
      "filter": [
        {
          "term": { "userIds": 2 }
        }
      ]
    }
  }
}
```
## Examples
### GET /elastic/showAll
```
GET http://localhost:8085/elastic/showAll
```

### GET /elastic/search?query={query}
```
GET http://localhost:8085/elastic/search?query=description big%7C%7Csmall
```

### GET /elastic/{userId}/search?query={query}
```
GET http://localhost:8085/elastic/2/search?query=description big%7C%7Csmall
```



