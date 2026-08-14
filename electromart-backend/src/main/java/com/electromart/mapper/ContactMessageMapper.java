package com.electromart.mapper;

import com.electromart.dto.response.ContactMessageResponse;
import com.electromart.entity.ContactMessage;

public final class ContactMessageMapper {

    private ContactMessageMapper() {}

    public static ContactMessageResponse toResponse(ContactMessage message) {
        return ContactMessageResponse.builder()
                .id(message.getId())
                .name(message.getName())
                .email(message.getEmail())
                .subject(message.getSubject())
                .message(message.getMessage())
                .status(message.getStatus())
                .createdAt(message.getCreatedAt())
                .build();
    }
}