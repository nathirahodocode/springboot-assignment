package com.example.assignment.mapper;

import com.example.assignment.dto.request.UserCreateRequest;
import com.example.assignment.dto.request.UserUpdateRequest;
import com.example.assignment.dto.response.UserResponse;
import com.example.assignment.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserCreateRequest request) {
        return User.builder()
                .name(request.getName().trim())
                .email(request.getEmail().trim().toLowerCase())
                .build();
    }

    public void updateEntity(User entity, UserUpdateRequest request) {
        entity.setName(request.getName().trim());
        entity.setEmail(request.getEmail().trim().toLowerCase());
    }

    public UserResponse toResponse(User entity) {
        return UserResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}