package com.example.task_service.model;

import java.util.List;

import jakarta.persistence.*;

import lombok.Data;

@Entity
@Table(name="tasks")
@Data
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String text;

    private String deadline;

    @ElementCollection
    @Column(name = "user-ids")
    private List<Long> userIds;
}
