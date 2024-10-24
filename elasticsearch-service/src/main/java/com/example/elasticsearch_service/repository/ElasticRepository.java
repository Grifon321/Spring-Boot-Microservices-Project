package com.example.elasticsearch_service.repository;

import com.example.elasticsearch_service.model.Task;

import java.util.List;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElasticRepository extends ElasticsearchRepository<Task, String> {
    @Query("{\"bool\": {\"must\": [{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"text\"]}}], \"filter\": [{\"term\": {\"userIds\": \"?1\"}}]}}")
    List<Task> findByTextAndUserID(String query, Long userId);

    List<Task> findAll();
    List<Task> findByText(String query);
    void deleteById(Long id);
}