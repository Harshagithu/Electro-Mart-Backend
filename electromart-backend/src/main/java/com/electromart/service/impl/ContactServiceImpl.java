package com.electromart.service.impl;

import com.electromart.dto.request.ContactMessageRequest;
import com.electromart.dto.request.ContactStatusUpdateRequest;
import com.electromart.dto.response.ContactMessageResponse;
import com.electromart.dto.response.PagedResponse;
import com.electromart.entity.ContactMessage;
import com.electromart.entity.User;
import com.electromart.enums.ContactStatus;
import com.electromart.exception.ResourceNotFoundException;
import com.electromart.mapper.ContactMessageMapper;
import com.electromart.repository.ContactMessageRepository;
import com.electromart.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {

    private final ContactMessageRepository contactMessageRepository;

    @Override
    @Transactional
    public ContactMessageResponse create(ContactMessageRequest request, Optional<User> currentUser) {
        ContactMessage message = ContactMessage.builder()
                .user(currentUser.orElse(null))
                .name(request.getName())
                .email(request.getEmail())
                .subject(request.getSubject())
                .message(request.getMessage())
                .status(ContactStatus.NEW)
                .build();

        return ContactMessageMapper.toResponse(contactMessageRepository.save(message));
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ContactMessageResponse> adminGetAll(ContactStatus status, Pageable pageable) {
        Page<ContactMessage> page = (status != null)
                ? contactMessageRepository.findByStatus(status, pageable)
                : contactMessageRepository.findAll(pageable);
        return PagedResponse.from(page.map(ContactMessageMapper::toResponse));
    }

    @Override
    @Transactional
    public ContactMessageResponse adminUpdateStatus(Long id, ContactStatusUpdateRequest request) {
        ContactMessage message = contactMessageRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Contact message", id));
        message.setStatus(request.getStatus());
        return ContactMessageMapper.toResponse(contactMessageRepository.save(message));
    }
}
