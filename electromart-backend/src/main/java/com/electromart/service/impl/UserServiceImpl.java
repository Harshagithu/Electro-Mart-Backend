package com.electromart.service.impl;

import com.electromart.dto.response.PagedResponse;
import com.electromart.dto.response.UserResponse;
import com.electromart.entity.User;
import com.electromart.exception.ResourceNotFoundException;
import com.electromart.repository.UserRepository;
import com.electromart.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<UserResponse> adminGetAll(Pageable pageable) {
        Page<User> page = userRepository.findAll(pageable);
        return PagedResponse.from(page.map(this::toResponse));
    }

    @Override
    @Transactional
    public UserResponse adminSetEnabled(Long id, boolean enabled) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("User", id));
        user.setEnabled(enabled);
        return toResponse(userRepository.save(user));
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .enabled(user.isEnabled())
                .roles(user.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toSet()))
                .createdAt(user.getCreatedAt())
                .build();
    }
}