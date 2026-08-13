package com.electromart.service;

import com.electromart.dto.request.AddressRequest;
import com.electromart.dto.response.AddressResponse;
import com.electromart.entity.User;

import java.util.List;

public interface AddressService {
    AddressResponse create(AddressRequest request, User user);
    List<AddressResponse> getAll(User user);
    AddressResponse getOne(Long id, User user);
    AddressResponse update(Long id, AddressRequest request, User user);
    void delete(Long id, User user);
    AddressResponse setDefault(Long id, User user);
}