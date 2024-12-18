package com.example.task_service.controller;

import com.example.task_service.kafka.TaskProducer;
import com.example.task_service.model.Task;
import com.example.task_service.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskProducer taskProducer;

    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody Task task) {
        // Validate the status of the task and send 400 responce if status is invalid
        if (!taskService.verifyStatus(task))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid status. Status has to be \"To Do\", \"In Progress\" or \"Done\".");

        // Save the task and send it to the Kafka producer
        Task createdTask = taskService.registerTask(task);
        taskProducer.sendTask(createdTask);

        // Send 201 responce with task's location
         URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdTask.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdTask);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTaskById(@PathVariable Long id) {
        // Delete task or send 404 responce if no task with the id
        Task task = taskService.getTaskById(id);
        if (task == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found.");
        taskProducer.deleteTask(task);
        taskService.deleteTaskById(id);
        
        // Send 204 responce
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable Long id) {
        // Get task or send 404 responce
        Task task = taskService.getTaskById(id);
        if (task == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found.");
        
        // Send 200 responce with task in body
        return ResponseEntity.ok(task);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @RequestBody Task task) {
        // Validate the status of the task
        if (!taskService.verifyStatus(task))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid status. Status has to be \"To Do\", \"In Progress\" or \"Done\".");
        
        // Update the task and send it to the Kafka producer
        Task newTask = taskService.updateTask(id, task);
        taskProducer.updateTask(newTask);

        // Send 200 responce with task in body
        return ResponseEntity.ok(newTask);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateUserId(@PathVariable Long id,
                                            @RequestParam(required = false) Long userId, 
                                            @RequestParam(required = false) String action, 
                                            @RequestParam(required = false) String name, 
                                            @RequestParam(required = false) String text, 
                                            @RequestParam(required = false) String deadline, 
                                            @RequestParam(required = false) String status) {
        // Get task or send 404 responce
        Task task = taskService.getTaskById(id);
        if (task == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found.");
        
        // Patch the task's name, text, deadline and status if present
        if (name != null)
            task.setName(name);
        
        if (text != null)
            task.setText(text);
        
        if (deadline != null)
            task.setDeadline(deadline);

        if (status != null && (status.equals("To Do") || status.equals("In Progress") || status.equals("Done"))) {
            task.setStatus(status);
        } else if (status != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid status. Valid statuses are \"To Do\", \"In Progress\", or \"Done\".");
        }
        
        // Patch the task's userId list if present
        if (userId != null && action != null) {
            if (action.equals("add")) {
                task = taskService.addUserID(id, userId);
            } else if (action.equals("remove")) {
                task = taskService.deleteUserID(id, userId);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid action. Valid actions are \"add\" or \"remove\".");
            }
        }
        // Send the task to the Kafka producer
        taskProducer.updateTask(task);
        // Send 200 responce with task in body
        return ResponseEntity.ok(task);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Task>> getTasksByUserId(@PathVariable Long userId) {
        List<Task> tasks = taskService.getTasksByUserId(userId);
        // Send 200 responce with tasks list in body
        return ResponseEntity.ok(tasks);
    }

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }
}