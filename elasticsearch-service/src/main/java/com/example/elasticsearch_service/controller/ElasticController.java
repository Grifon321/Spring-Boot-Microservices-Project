package com.example.elasticsearch_service.controller;

import com.example.elasticsearch_service.model.Task;
import com.example.elasticsearch_service.service.ElasticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/elastic")
public class ElasticController {

    @Autowired
    private ElasticService elasticService;

    @GetMapping("/showAll")
    public ResponseEntity<List<Task>> getAllTasks() {
        return ResponseEntity.ok(elasticService.findAll());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Task>> findByText(@RequestParam String query) {
        List<Task> tasks = elasticService.findByText(query);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{userId}/search")
    public ResponseEntity<List<Task>> findByTextAndUserID(@RequestParam String query, @PathVariable Long userId) {
        List<Task> tasks = elasticService.findByTextAndUserID(query, userId);
        return ResponseEntity.ok(tasks);
    }
    
}