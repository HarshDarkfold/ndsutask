package com.ndsu.task.service;

import java.time.Duration;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GetJsonTreeService {
	
	private final RestTemplate restTemplate;
	
	public GetJsonTreeService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(500))
                .setReadTimeout(Duration.ofSeconds(500))
                .basicAuthentication("interview", "GrijkofDuDasutIvyett")
                .build();
    }
	
	public String getTreeJsonFeed(String url) {
        return this.restTemplate.getForObject(url, String.class);
    }
}
