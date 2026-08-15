package com.electromart.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.electromart.dto.request.LoginRequest;
import com.electromart.dto.request.RegisterRequest;

import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Test
    void registerThenLoginReturnsAWorkingJwt() throws Exception {
        RegisterRequest register = new RegisterRequest();
        register.setFirstName("Integration");
        register.setLastName("Test");
        register.setEmail("integration@example.com");
        register.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.roles[0]").value("USER"))
                .andExpect(jsonPath("$.token").exists());

        LoginRequest login = new LoginRequest();
        login.setEmail("integration@example.com");
        login.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void adminEndpointWithNoTokenReturnsConsistentJsonShape() throws Exception {
        // This is the Phase 15 fix in action — without it, this would come back
        // as Spring Security's bare default response instead of our ApiError shape.
        mockMvc.perform(get("/api/admin/dashboard"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").exists());
    }
}