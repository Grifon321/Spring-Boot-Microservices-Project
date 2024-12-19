package com.example.elasticsearch_service.model;

public class TaskMessage {
    private String operation;  // "CREATE", "UPDATE", "DELETE"
    private Task task;

    // Getters and Setters
    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
}
