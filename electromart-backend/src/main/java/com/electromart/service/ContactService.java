package com.electromart.service;

import com.electromart.dto.request.ContactMessageRequest;
import com.electromart.dto.request.ContactStatusUpdateRequest;
import com.electromart.dto.response.ContactMessageResponse;
import com.electromart.dto.response.PagedResponse;
import com.electromart.entity.User;
import com.electromart.enums.ContactStatus;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ContactService {
    ContactMessageResponse create(ContactMessageRequest request, Optional<User> currentUser);
    PagedResponse<ContactMessageResponse> adminGetAll(ContactStatus status, Pageable pageable);
    ContactMessageResponse adminUpdateStatus(Long id, ContactStatusUpdateRequest request);
}