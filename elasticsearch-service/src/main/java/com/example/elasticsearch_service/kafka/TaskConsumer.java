package com.example.elasticsearch_service.kafka;

import com.example.elasticsearch_service.model.Task;
import com.example.elasticsearch_service.model.TaskMessage;
import com.example.elasticsearch_service.repository.ElasticRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TaskConsumer {

    @Autowired
    private ElasticRepository elasticRepository;

    @Autowired
    private ObjectMapper objectMapper;
    
    @KafkaListener(topics = "task-topic", groupId = "group_id")
    public void consume(String messageJSON) {
        try {
            // Process the tasks retrieved from kafka 
            TaskMessage taskMessage = objectMapper.readValue(messageJSON, TaskMessage.class);
            String operation = taskMessage.getOperation();
            Task task = taskMessage.getTask();
            System.out.println("Task recieved from Kafka topic: " + messageJSON);

            switch (operation) {
                case "CREATE":
                    elasticRepository.save(task); // Does not have to check for duplicates as elasticsearch already does it
                    System.out.println("Task created in Elasticsearch: " + messageJSON);
                    break;
                case "UPDATE":
                    elasticRepository.save(task);
                    System.out.println("Task updated in Elasticsearch: " + messageJSON);
                    break;
                case "DELETE":
                    elasticRepository.deleteById(task.getId());
                    System.out.println("Task deleted in Elasticsearch: " + messageJSON);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            System.out.println("Task could not be recieved from Kafka");
            e.printStackTrace();
        }
        
    }

}