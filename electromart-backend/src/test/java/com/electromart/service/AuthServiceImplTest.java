package com.electromart.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.electromart.dto.request.RegisterRequest;
import com.electromart.entity.Cart;
import com.electromart.entity.Role;
import com.electromart.entity.User;
import com.electromart.entity.Wishlist;
import com.electromart.enums.RoleName;
import com.electromart.exception.DuplicateResourceException;
import com.electromart.repository.CartRepository;
import com.electromart.repository.RoleRepository;
import com.electromart.repository.UserRepository;
import com.electromart.repository.WishlistRepository;
import com.electromart.security.JwtTokenProvider;
import com.electromart.service.impl.AuthServiceImpl;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private CartRepository cartRepository;
    @Mock private WishlistRepository wishlistRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtTokenProvider jwtTokenProvider;
    @Mock private Authentication authentication;

    private AuthServiceImpl authService() {
        return new AuthServiceImpl(userRepository, roleRepository, cartRepository, wishlistRepository,
                passwordEncoder, authenticationManager, jwtTokenProvider);
    }

    @Test
    void registeringWithAnExistingEmailIsRejected() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("jane@example.com");
        request.setPassword("password123");
        request.setFirstName("Jane");
        request.setLastName("Doe");

        when(userRepository.existsByEmail("jane@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService().register(request))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void newRegistrationsAlwaysGetUserRoleAndAnEmptyCartAndWishlist() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("jane@example.com");
        request.setPassword("password123");
        request.setFirstName("Jane");
        request.setLastName("Doe");

        Role userRole = Role.builder().name(RoleName.USER).build();
        when(userRepository.existsByEmail("jane@example.com")).thenReturn(false);
        when(roleRepository.findByName(RoleName.USER)).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode("password123")).thenReturn("hashed");

        User savedUser = User.builder()
                .id(1L).firstName("Jane").lastName("Doe").email("jane@example.com")
                .password("hashed").enabled(true).roles(Set.of(userRole)).build();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(cartRepository.save(any(Cart.class))).thenReturn(Cart.builder().build());
        when(wishlistRepository.save(any(Wishlist.class))).thenReturn(Wishlist.builder().build());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("fake-jwt");

        var response = authService().register(request);

        assertThat(response.getRoles()).containsExactly("USER");
        assertThat(response.getToken()).isEqualTo("fake-jwt");
    }
}