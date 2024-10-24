package com.example.elasticsearch_service.service;

import com.example.elasticsearch_service.model.Task;
import com.example.elasticsearch_service.repository.ElasticRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ElasticService {

    @Autowired
    private final ElasticRepository elasticRepository;
    
    @Autowired
    private final RestTemplate restTemplate;

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
        return elasticRepository.findByText(query);
    }

    public List<Task> findByTextAndUserID(String query, Long userId){
        return elasticRepository.findByTextAndUserID(query, userId);
    }

    public List<Task> findAll() {
        return (List<Task>) elasticRepository.findAll();
    }
}