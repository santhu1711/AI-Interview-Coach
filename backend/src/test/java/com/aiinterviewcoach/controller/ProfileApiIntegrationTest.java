package com.aiinterviewcoach.controller;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.aiinterviewcoach.dto.request.RegisterRequest;
import com.aiinterviewcoach.dto.response.AuthResponse;
import com.aiinterviewcoach.repository.UserRepository;
import com.aiinterviewcoach.service.auth.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProfileApiIntegrationTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private AuthService authService;
    @Autowired private UserRepository userRepository;

    @BeforeEach
    void clearDatabase() {
        userRepository.deleteAll();
    }

    @Test
    void getsAndUpdatesOwnedProfileWithoutPasswordData() throws Exception {
        AuthResponse auth = authService.register(new RegisterRequest(
                "Original Name", "profile@example.com", "Strong1!Password", "Strong1!Password"));

        mockMvc.perform(get("/api/profile").header("Authorization", "Bearer " + auth.accessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Original Name"))
                .andExpect(jsonPath("$.email").value("profile@example.com"))
                .andExpect(jsonPath("$.createdAt", notNullValue()))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());

        mockMvc.perform(put("/api/profile")
                        .header("Authorization", "Bearer " + auth.accessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fullName\":\"  Updated Name  \"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Updated Name"))
                .andExpect(jsonPath("$.email").value("profile@example.com"));
    }

    @Test
    void validatesUpdatesAndRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/profile")).andExpect(status().isUnauthorized());
        AuthResponse auth = authService.register(new RegisterRequest(
                "Original Name", "profile@example.com", "Strong1!Password", "Strong1!Password"));
        mockMvc.perform(put("/api/profile")
                        .header("Authorization", "Bearer " + auth.accessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fullName\":\"   \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.fullName").value("Full name is required."));
    }
}
