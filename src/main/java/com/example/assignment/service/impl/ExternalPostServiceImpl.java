package com.example.assignment.service.impl;

import com.example.assignment.dto.response.ExternalPostResponse;
import com.example.assignment.exception.ExternalServiceException;
import com.example.assignment.service.ExternalPostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalPostServiceImpl implements ExternalPostService {

    private final RestTemplate restTemplate;

    @Value("${external.api.base-url}")
    private String baseUrl;

    @Override
    public ExternalPostResponse getPostById(Long postId) {
        String url = baseUrl + "/posts/" + postId;
        try {
            log.debug("Calling external API: {}", url);
            ExternalPostResponse response = restTemplate.getForObject(url, ExternalPostResponse.class);
            if (response == null) {
                throw new ExternalServiceException("Empty response from external API for postId=" + postId);
            }
            return response;
        } catch (RestClientException ex) {
            log.error("External API call failed: {}", url, ex);
            throw new ExternalServiceException("Failed to fetch post from external API", ex);
        }
    }
}