package com.electromart.controller;

import com.electromart.dto.request.ContactMessageRequest;
import com.electromart.dto.response.ContactMessageResponse;
import com.electromart.security.CurrentUserProvider;
import com.electromart.service.ContactService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;
    private final CurrentUserProvider currentUserProvider;

    @PostMapping
    public ResponseEntity<ContactMessageResponse> submit(@Valid @RequestBody ContactMessageRequest request) {
        var response = contactService.create(request, currentUserProvider.tryGetCurrentUser());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}