package com.example.elasticsearch_service.repository;

import com.example.elasticsearch_service.model.Task;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElasticRepository extends ElasticsearchRepository<Task, String> {
    List<Task> findAll();
    void deleteById(Long id);
}