package com.electromart.controller;

import com.electromart.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AdminReviewController {

    private final ReviewService reviewService;

    @DeleteMapping("/api/admin/reviews/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reviewService.adminDelete(id);
        return ResponseEntity.noContent().build();
    }
}