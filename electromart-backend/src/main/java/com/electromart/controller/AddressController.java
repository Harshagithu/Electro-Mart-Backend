package com.electromart.controller;

import com.electromart.dto.request.AddressRequest;
import com.electromart.dto.response.AddressResponse;
import com.electromart.security.CurrentUserProvider;
import com.electromart.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;
    private final CurrentUserProvider currentUserProvider;

    @PostMapping
    public ResponseEntity<AddressResponse> create(@Valid @RequestBody AddressRequest request) {
        var user = currentUserProvider.getCurrentUser();
        return ResponseEntity.status(HttpStatus.CREATED).body(addressService.create(request, user));
    }

    @GetMapping
    public ResponseEntity<List<AddressResponse>> getAll() {
        return ResponseEntity.ok(addressService.getAll(currentUserProvider.getCurrentUser()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AddressResponse> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(addressService.getOne(id, currentUserProvider.getCurrentUser()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddressResponse> update(@PathVariable Long id, @Valid @RequestBody AddressRequest request) {
        return ResponseEntity.ok(addressService.update(id, request, currentUserProvider.getCurrentUser()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        addressService.delete(id, currentUserProvider.getCurrentUser());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/default")
    public ResponseEntity<AddressResponse> setDefault(@PathVariable Long id) {
        return ResponseEntity.ok(addressService.setDefault(id, currentUserProvider.getCurrentUser()));
    }
}