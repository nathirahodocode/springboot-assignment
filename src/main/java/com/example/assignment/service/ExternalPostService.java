package com.example.assignment.service;

import com.example.assignment.dto.response.ExternalPostResponse;

public interface ExternalPostService {
    ExternalPostResponse getPostById(Long postId);
}