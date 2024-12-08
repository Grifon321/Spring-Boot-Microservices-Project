package com.example.elasticsearch_service.service;

import com.example.elasticsearch_service.model.Task;
import com.example.elasticsearch_service.repository.ElasticRepository;

import lombok.RequiredArgsConstructor;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ElasticService {

    @Autowired
    private final ElasticRepository elasticRepository;
    
    @Autowired
    private final RestTemplate restTemplate;

    @SuppressWarnings("deprecation")
    @Autowired
    private RestHighLevelClient client;

    @Value("${task-service-adress}")
    private String tasksServiceAdress;

    @Value("${spring.elasticsearch.uris}")
    private String elasticsearchAdress;

    

    public void fetchAndIndexTasks() {
        String showAllUrl = tasksServiceAdress + "/showAll";
        // Fetch tasks from the external API
        Task[] tasks = restTemplate.getForObject(showAllUrl, Task[].class);
        if (tasks != null) {
            // Index tasks into Elasticsearch
            List<Task> taskList = Arrays.asList(tasks);
            elasticRepository.saveAll(taskList);
            System.out.println("Tasks have been indexed in Elasticsearch");
        } else {
            System.out.println("No tasks found");
        }
    }


    public List<Task> findByText(String query){
        return customSearch(query, null);
    }

    public List<Task> findByTextAndUserID(String query, Long userId){
        return customSearch(query, userId);
    }

    /* Produces following JSON by parameters query = description%20big%7C%7Csmall , userId = 2
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
     */
    @SuppressWarnings("deprecation")
    public List<Task> customSearch(String query, Long userId){
        try {
            // Parse every OR statement separated by "||" or "%7C%7C"
            String[] queryTerms = query.split("\\|\\|");
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

            for (String termSet : queryTerms) {
                // Parse every OR statement separated by " " or "%20"
                String[] mustTerms = termSet.trim().split(" ");
                BoolQueryBuilder mustQuery = QueryBuilders.boolQuery();

                for (String term : mustTerms)
                    mustQuery.must(QueryBuilders.matchQuery("text", term).operator(org.elasticsearch.index.query.Operator.AND));
                boolQuery.should(mustQuery);
            }

            // If no userId has been specified - ignore filter
            if (userId != null)
                boolQuery.filter(QueryBuilders.termQuery("userIds", userId));
            boolQuery.minimumShouldMatch(1);

            // Build the search request
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(boolQuery);

            SearchRequest searchRequest = new SearchRequest("tasks");
            searchRequest.source(sourceBuilder);

            // Execute the search query
            SearchResponse searchResponse;
                searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

            // Parse the response to get the tasks
            List<Task> tasks = new ArrayList<>();
            for (SearchHit hit : searchResponse.getHits()) {
                Task task = new Task();
                // Even though it is indexed as Long, still needs casting from Integer to Long
                task.setId(
                    Long.valueOf( hit.getSourceAsMap().get("id").toString())
                ); 
                
                task.setName((String) hit.getSourceAsMap().get("name"));
                task.setText((String) hit.getSourceAsMap().get("text"));
                task.setDeadline((String) hit.getSourceAsMap().get("deadline"));
                
                // Even though it is indexed as List<Long>, still needs casting from List<Integer> to List<Long>
                Object userIdsObject = hit.getSourceAsMap().get("userIds");
                if (userIdsObject instanceof List) {
                    List<?> userIdsList = (List<?>) userIdsObject; // Use raw List, then check element types
                    List<Long> userIds = new ArrayList<>();
                    for (Object userIdObject : userIdsList)
                        userIds.add(
                                Long.valueOf(userIdObject.toString())
                            );

                    task.setUserIds(userIds);
                }

                tasks.add(task);
            }

            return tasks;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Task> findAll() {
        return (List<Task>) elasticRepository.findAll();
    }
}