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
        // Send 200 responce with all tasks in body
        return ResponseEntity.ok(elasticService.findAll());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Task>> findByText(@RequestParam String query) {
        // Perform a query and retrieve all tasks
        List<Task> tasks = elasticService.findByText(query);

        // Send 200 responce with all tasks in body
        return ResponseEntity.ok(tasks);
    }

    // " " can be encoded with "%20", "||" has to be encoded with "%7C%7C"
    @GetMapping("/{userId}/search")
    public ResponseEntity<List<Task>> findByTextAndUserID(@RequestParam String query, @PathVariable Long userId) {
        // Perform a query and retrieve all tasks with a filter on userId
        List<Task> tasks = elasticService.findByTextAndUserID(query, userId);
        // Send 200 responce with all tasks in body
        return ResponseEntity.ok(tasks);
    }
    
}