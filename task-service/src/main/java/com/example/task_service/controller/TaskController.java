package com.example.task_service.controller;

import com.example.task_service.kafka.TaskProducer;
import com.example.task_service.model.BadRequestException;
import com.example.task_service.model.Task;
import com.example.task_service.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskProducer taskProducer;

    @PostMapping("/register")
    public ResponseEntity<Task> registerTask(@RequestBody Task task) {
        if (!taskService.verifyStatus(task))
            throw new BadRequestException("Status has to be \"To Do\", \"In Progress\" or \"Done\"");
        Task savedTask = taskService.registerTask(task);
        taskProducer.sendTask(savedTask);
        return ResponseEntity.ok(savedTask);
    }

    @DeleteMapping("/{id}")
    public void deleteTaskById(@PathVariable Long id) {
        Task task = taskService.getTaskById(id);
        taskProducer.deleteTask(task);
        taskService.deleteTaskById(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task task) {
        if (!taskService.verifyStatus(task))
            throw new BadRequestException("Status has to be \"To Do\", \"In Progress\" or \"Done\"");
        Task newTask = taskService.updateTask(id, task);
        taskProducer.updateTask(newTask);
        return ResponseEntity.ok(newTask);
    }

    @PutMapping("/addUserId/{id}")
    public ResponseEntity<Task> addUserID(@PathVariable Long id, @RequestParam Long userId) {
        return ResponseEntity.ok(taskService.addUserID(id, userId));
    }

    @PutMapping("/removeUserId/{id}")
    public ResponseEntity<Task> deleteUserID(@PathVariable Long id, @RequestParam Long userId) {
        return ResponseEntity.ok(taskService.deleteUserID(id, userId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Task>> getTasksByUserId(@PathVariable Long userId) {
        List<Task> tasks = taskService.getTasksByUserId(userId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/showAll")
    public ResponseEntity<List<Task>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }
}