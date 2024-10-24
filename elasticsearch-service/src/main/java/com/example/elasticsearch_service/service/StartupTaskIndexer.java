package com.example.elasticsearch_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StartupTaskIndexer {

    private final ElasticService elasticService;

    @EventListener(ApplicationReadyEvent.class)
    public void indexTasksOnStartup() {
        elasticService.fetchAndIndexTasks();
    }
}