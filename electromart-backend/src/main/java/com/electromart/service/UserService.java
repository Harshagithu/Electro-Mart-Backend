package com.electromart.service;

import com.electromart.dto.response.PagedResponse;
import com.electromart.dto.response.UserResponse;
import org.springframework.data.domain.Pageable;

public interface UserService {
    PagedResponse<UserResponse> adminGetAll(Pageable pageable);
    UserResponse adminSetEnabled(Long id, boolean enabled);
}