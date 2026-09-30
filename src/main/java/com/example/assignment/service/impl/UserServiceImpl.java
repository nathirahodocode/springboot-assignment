package com.example.assignment.service.impl;

import com.example.assignment.dto.request.UserCreateRequest;
import com.example.assignment.dto.request.UserUpdateRequest;
import com.example.assignment.dto.response.PageResponse;
import com.example.assignment.dto.response.UserResponse;
import com.example.assignment.entity.User;
import com.example.assignment.exception.DuplicateResourceException;
import com.example.assignment.exception.ResourceNotFoundException;
import com.example.assignment.mapper.UserMapper;
import com.example.assignment.repository.UserRepository;
import com.example.assignment.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final int MAX_PAGE_SIZE = 10;

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        if (userRepository.existsByEmail(request.getEmail().toLowerCase())) {
            throw new DuplicateResourceException("User with email already exists: " + request.getEmail());
        }
        User entity = userMapper.toEntity(request);
        User saved = userRepository.save(entity);
        log.debug("Created user id={}", saved.getId());
        return userMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        String newEmail = request.getEmail().toLowerCase();
        if (!existing.getEmail().equalsIgnoreCase(newEmail)
                && userRepository.existsByEmail(newEmail)) {
            throw new DuplicateResourceException("Email already in use: " + newEmail);
        }

        userMapper.updateEntity(existing, request);
        User updated = userRepository.save(existing);
        log.debug("Updated user id={}", updated.getId());
        return userMapper.toResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserResponse> getUsers(int page, int size) {
        int effectiveSize = Math.min(size <= 0 ? MAX_PAGE_SIZE : size, MAX_PAGE_SIZE);

        PageRequest pageRequest = PageRequest.of(
                Math.max(page, 0),
                effectiveSize,
                Sort.by(Sort.Direction.DESC, "id")
        );

        Page<User> result = userRepository.findAll(pageRequest);

        List<UserResponse> content = result.getContent()
                .stream()
                .map(userMapper::toResponse)
                .toList();

        return PageResponse.<UserResponse>builder()
                .content(content)
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .last(result.isLast())
                .build();
    }
}