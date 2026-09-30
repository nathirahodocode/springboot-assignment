package com.example.assignment.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExternalPostResponse {
    private Long id;
    private Long userId;
    private String title;
    private String body;
}