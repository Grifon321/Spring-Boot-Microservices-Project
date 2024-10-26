package com.example.task_service.kafka;

import com.example.task_service.model.Task;
import com.example.task_service.model.TaskMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TaskProducer {

    private static final String TOPIC = "task-topic";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void sendTask(Task task) {
        sendMessage(task, "CREATE");
    }

    public void updateTask(Task task) {
        sendMessage(task, "UPDATE");
    }

    public void deleteTask(Task task) {
        sendMessage(task, "DELETE");
    }

    public void sendMessage(Task task, String operation) {
        try {
            TaskMessage taskMessage = new TaskMessage();
            taskMessage.setTask(task);
            taskMessage.setOperation(operation);
            
            String messageJSON = objectMapper.writeValueAsString(taskMessage);
            kafkaTemplate.send(TOPIC, messageJSON);
            System.out.println("Task message sent to Kafka topic: " + messageJSON);
        } catch (JsonProcessingException e) {
            System.out.println("Task message could not be sent to Kafka");
        }
        
        
    }
}