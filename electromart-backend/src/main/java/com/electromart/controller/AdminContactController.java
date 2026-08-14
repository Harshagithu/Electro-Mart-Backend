package com.electromart.controller;

import com.electromart.dto.request.ContactStatusUpdateRequest;
import com.electromart.dto.response.ContactMessageResponse;
import com.electromart.dto.response.PagedResponse;
import com.electromart.enums.ContactStatus;
import com.electromart.service.ContactService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/contact-messages")
@RequiredArgsConstructor
public class AdminContactController {

    private final ContactService contactService;

    @GetMapping
    public ResponseEntity<PagedResponse<ContactMessageResponse>> getAll(
            @RequestParam(required = false) ContactStatus status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(contactService.adminGetAll(status, pageable));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ContactMessageResponse> updateStatus(@PathVariable Long id, @Valid @RequestBody ContactStatusUpdateRequest request) {
        return ResponseEntity.ok(contactService.adminUpdateStatus(id, request));
    }
}