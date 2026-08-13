package com.electromart.mapper;

import com.electromart.dto.response.AddressResponse;
import com.electromart.entity.Address;

public final class AddressMapper {

    private AddressMapper() {}

    public static AddressResponse toResponse(Address address) {
        if (address == null) return null;
        return AddressResponse.builder()
                .id(address.getId())
                .fullName(address.getFullName())
                .phone(address.getPhone())
                .addressLine(address.getAddressLine())
                .city(address.getCity())
                .state(address.getState())
                .postalCode(address.getPostalCode())
                .country(address.getCountry())
                .addressType(address.getAddressType())
                .isDefault(address.isDefaultAddress())
                .build();
    }
}