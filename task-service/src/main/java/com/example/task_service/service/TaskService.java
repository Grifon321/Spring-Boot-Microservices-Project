package com.example.task_service.service;

import com.example.task_service.model.Task;
import com.example.task_service.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    public Task registerTask(Task task) {
        return taskRepository.save(task);
    }

    public void deleteTaskById(Long id) {
        Task task = getTaskById(id);
        task.setUserIds(null);
        taskRepository.delete(task);
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
    }

    public Task updateTask(Long id, Task updatedTask) {
        Task task = getTaskById(id);
        task.setName(updatedTask.getName());
        task.setText(updatedTask.getText());
        task.setDeadline(updatedTask.getDeadline());
        return taskRepository.save(task);
    }

    public Task addUserID(Long id, Long userId) {
        Task task = getTaskById(id);
        List<Long> userIDsList = task.getUserIds();
        userIDsList.add(userId);
        task.setUserIds(userIDsList);
        return taskRepository.save(task);
    }

    public Task deleteUserID(Long id, Long userId) {
        Task task = getTaskById(id);
        List<Long> userIDsList = task.getUserIds();
        userIDsList.remove(userId);
        task.setUserIds(userIDsList);
        return taskRepository.save(task);
    }

    public Task getTaskByName(String taskName) {
        return taskRepository.getTaskByName(taskName);
    }

    public List<Task> getTasksByUserId(Long userId) {
        return taskRepository.findByUserId(userId);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

}