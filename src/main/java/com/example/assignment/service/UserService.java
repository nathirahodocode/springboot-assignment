package com.example.assignment.service;

import com.example.assignment.dto.request.UserCreateRequest;
import com.example.assignment.dto.request.UserUpdateRequest;
import com.example.assignment.dto.response.PageResponse;
import com.example.assignment.dto.response.UserResponse;

public interface UserService {

    UserResponse createUser(UserCreateRequest request);

    UserResponse updateUser(Long id, UserUpdateRequest request);

    UserResponse getUserById(Long id);

    PageResponse<UserResponse> getUsers(int page, int size);
}