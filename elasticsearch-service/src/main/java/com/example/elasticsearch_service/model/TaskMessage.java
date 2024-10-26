package com.example.elasticsearch_service.model;

import lombok.Data;

@Data
public class TaskMessage {
    private String operation;  // "CREATE", "UPDATE", "DELETE"
    private Task task;
}
