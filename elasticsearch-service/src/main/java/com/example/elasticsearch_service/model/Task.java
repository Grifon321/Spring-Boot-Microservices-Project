package com.example.elasticsearch_service.model;

import lombok.Data;
import java.util.List;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "tasks")
@Data
public class Task {
    private Long id;
    private String name;
    private String text;
    private String deadline;
    private String status; 
    private List<Long> userIds;
}