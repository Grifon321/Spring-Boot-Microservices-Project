package com.example.web.model;

import java.util.List;

public class Task {
    public Long id;
    public String name;
    public String text;
    public String deadline;
    private List<Long> userIds;
}