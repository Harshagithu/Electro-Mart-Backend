package com.electromart.service.impl;

import com.electromart.dto.request.AddressRequest;
import com.electromart.dto.response.AddressResponse;
import com.electromart.entity.Address;
import com.electromart.entity.User;
import com.electromart.enums.AddressType;
import com.electromart.exception.ResourceNotFoundException;
import com.electromart.mapper.AddressMapper;
import com.electromart.repository.AddressRepository;
import com.electromart.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;

    @Override
    @Transactional
    public AddressResponse create(AddressRequest request, User user) {
        boolean isFirstAddress = addressRepository.findByUserId(user.getId()).isEmpty();

        Address address = Address.builder()
                .user(user)
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .addressLine(request.getAddressLine())
                .city(request.getCity())
                .state(request.getState())
                .postalCode(request.getPostalCode())
                .country(request.getCountry())
                .addressType(request.getAddressType() != null ? request.getAddressType() : AddressType.HOME)
                // A user's very first address is always their default, regardless
                // of what makeDefault says — there's never a valid state where a
                // user has addresses but none of them is the default.
                .defaultAddress(isFirstAddress || request.isMakeDefault())
                .build();

        if (address.isDefaultAddress() && !isFirstAddress) {
            clearExistingDefault(user.getId());
        }

        return AddressMapper.toResponse(addressRepository.save(address));
    }

    @Override
    public List<AddressResponse> getAll(User user) {
        return addressRepository.findByUserId(user.getId()).stream()
                .map(AddressMapper::toResponse)
                .toList();
    }

    @Override
    public AddressResponse getOne(Long id, User user) {
        return AddressMapper.toResponse(findOwned(id, user));
    }

    @Override
    @Transactional
    public AddressResponse update(Long id, AddressRequest request, User user) {
        Address address = findOwned(id, user);

        address.setFullName(request.getFullName());
        address.setPhone(request.getPhone());
        address.setAddressLine(request.getAddressLine());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPostalCode(request.getPostalCode());
        address.setCountry(request.getCountry());
        if (request.getAddressType() != null) {
            address.setAddressType(request.getAddressType());
        }

        if (request.isMakeDefault() && !address.isDefaultAddress()) {
            clearExistingDefault(user.getId());
            address.setDefaultAddress(true);
        }

        return AddressMapper.toResponse(addressRepository.save(address));
    }

    @Override
    @Transactional
    public void delete(Long id, User user) {
        Address address = findOwned(id, user);
        boolean wasDefault = address.isDefaultAddress();
        addressRepository.delete(address);

        // Don't leave the user with addresses but no default — promote
        // whichever one remains (if any) to default automatically.
        if (wasDefault) {
            addressRepository.findByUserId(user.getId()).stream().findFirst()
                    .ifPresent(remaining -> {
                        remaining.setDefaultAddress(true);
                        addressRepository.save(remaining);
                    });
        }
    }

    @Override
    @Transactional
    public AddressResponse setDefault(Long id, User user) {
        Address address = findOwned(id, user);
        if (!address.isDefaultAddress()) {
            clearExistingDefault(user.getId());
            address.setDefaultAddress(true);
            addressRepository.save(address);
        }
        return AddressMapper.toResponse(address);
    }

    private void clearExistingDefault(Long userId) {
        addressRepository.findByUserIdAndDefaultAddressTrue(userId)
                .ifPresent(existing -> {
                    existing.setDefaultAddress(false);
                    addressRepository.save(existing);
                });
    }

    private Address findOwned(Long id, User user) {
        return addressRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> ResourceNotFoundException.of("Address", id));
    }
}