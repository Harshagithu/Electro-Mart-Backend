package com.electromart.config;

import com.electromart.entity.Role;
import com.electromart.entity.User;
import com.electromart.enums.RoleName;
import com.electromart.repository.RoleRepository;
import com.electromart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * Ensures the roles table has USER/ADMIN before anything tries to assign
 * them, and seeds one admin login so admin-only endpoints (Phase 7 onward)
 * are testable without a manual SQL insert. Public /api/auth/register can
 * only ever create USER accounts — this is the only place an ADMIN account
 * gets created, which is exactly the point: self-registration should never
 * be able to grant elevated access.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        Role userRole = roleRepository.findByName(RoleName.USER)
                .orElseGet(() -> roleRepository.save(Role.builder().name(RoleName.USER).build()));
        Role adminRole = roleRepository.findByName(RoleName.ADMIN)
                .orElseGet(() -> roleRepository.save(Role.builder().name(RoleName.ADMIN).build()));

        if (!userRepository.existsByEmail("admin@electromart.com")) {
            User admin = User.builder()
                    .firstName("Store")
                    .lastName("Admin")
                    .email("admin@electromart.com")
                    .password(passwordEncoder.encode("Admin@123"))
                    .enabled(true)
                    .roles(Set.of(adminRole))
                    .build();
            userRepository.save(admin);
            log.info("Seeded admin account -> admin@electromart.com / Admin@123");
        }

        log.info("Roles ready: {}", userRole.getName() + ", " + adminRole.getName());
    }
}