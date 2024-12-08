package com.example.elasticsearch_service.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.example.demo.repository")
public class ElasticsearchConfig {

    @Value("${spring.elasticsearch.uris}")
    private String elasticsearchUri;
    
    @SuppressWarnings("deprecation")
    @Bean
    public RestHighLevelClient restHighLevelClient() {
        String[] uriParts = elasticsearchUri.replace("http://", "").split(":");
        String host = uriParts[0];
        int port = Integer.parseInt(uriParts[1]);

        return new RestHighLevelClient(
            RestClient.builder(
                new HttpHost(host, port, "http")
            )
        );
    }
}
