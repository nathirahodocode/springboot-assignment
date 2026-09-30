package com.example.assignment.controller;

import com.example.assignment.dto.response.ExternalPostResponse;
import com.example.assignment.service.ExternalPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/external-posts")
@RequiredArgsConstructor
@Tag(name = "External Posts", description = "Nested 3rd party API integration")
public class ExternalPostController {

    private final ExternalPostService externalPostService;

    @GetMapping("/{id}")
    @Operation(summary = "Fetch post from external API through our service")
    public ResponseEntity<ExternalPostResponse> getExternalPost(@PathVariable Long id) {
        return ResponseEntity.ok(externalPostService.getPostById(id));
    }
}