package com.example.elasticsearch_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class StartupTaskIndexer {
    
    @Autowired
    private ElasticService elasticService;

    @EventListener(ApplicationReadyEvent.class)
    public void indexTasksOnStartup() {
        elasticService.fetchAndIndexTasks();
    }
}