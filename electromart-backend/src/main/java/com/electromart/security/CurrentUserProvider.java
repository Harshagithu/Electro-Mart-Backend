package com.electromart.security;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.electromart.entity.User;
import com.electromart.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CurrentUserProvider {

    private final UserRepository userRepository;

    /** Resolves the full User entity for whoever's JWT is on the current request. */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        return userRepository.findByEmail(principal.getEmail())
                .orElseThrow(() -> new IllegalStateException("Authenticated user no longer exists"));
    }
    
    // Add this method alongside getCurrentUser():
    public Optional<User> tryGetCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            return java.util.Optional.empty();
        }
        return userRepository.findByEmail(principal.getEmail());
    }
    
}