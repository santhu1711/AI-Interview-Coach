package com.aiinterviewcoach.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.aiinterviewcoach.entity.User;
import com.aiinterviewcoach.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthApiIntegrationTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Test
    void registersWithBcryptAndReturnsBearerTokenWithoutPasswordData() throws Exception {
        MvcResult result = register("New User", "New.User@Example.com", "Secure@123")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(3600))
                .andExpect(jsonPath("$.user.email").value("new.user@example.com"))
                .andExpect(jsonPath("$.user.passwordHash").doesNotExist())
                .andReturn();

        User persisted = userRepository.findByEmailIgnoreCase("new.user@example.com").orElseThrow();
        assertThat(persisted.getPasswordHash()).isNotEqualTo("Secure@123");
        assertThat(passwordEncoder.matches("Secure@123", persisted.getPasswordHash())).isTrue();
        assertThat(result.getResponse().getContentAsString()).doesNotContain("passwordHash");
    }

    @Test
    void rejectsInvalidRegistrationAndDuplicateEmail() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"fullName":"","email":"bad","password":"weak","confirmPassword":"different"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.fieldErrors.fullName").exists())
                .andExpect(jsonPath("$.fieldErrors.email").exists())
                .andExpect(jsonPath("$.fieldErrors.password").exists());

        register("First", "duplicate@example.com", "Secure@123").andExpect(status().isCreated());
        register("Second", "DUPLICATE@example.com", "Secure@123")
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("An account with this email already exists."));
    }

    @Test
    void rejectsMismatchedConfirmation() throws Exception {
        String body = objectMapper.writeValueAsString(new RegistrationBody(
                "User", "mismatch@example.com", "Secure@123", "Secure@124"));
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.confirmPassword").exists());
    }

    @Test
    void logsInAndLoadsCurrentUserWithBearerToken() throws Exception {
        register("Login User", "login@example.com", "Secure@123").andExpect(status().isCreated());

        MvcResult login = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"LOGIN@example.com","password":"Secure@123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.fullName").value("Login User"))
                .andReturn();
        JsonNode response = objectMapper.readTree(login.getResponse().getContentAsString());
        String token = response.get("accessToken").asText();

        mockMvc.perform(get("/api/auth/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("login@example.com"))
                .andExpect(jsonPath("$.fullName").value("Login User"));
    }

    @Test
    void rejectsInvalidLoginAndUnauthorizedCurrentUserRequests() throws Exception {
        register("Login User", "invalid-login@example.com", "Secure@123")
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"invalid-login@example.com","password":"Wrong@123"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("The email or password is incorrect."));

        mockMvc.perform(get("/api/auth/me")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/auth/me").header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    private org.springframework.test.web.servlet.ResultActions register(
            String fullName, String email, String password) throws Exception {
        String body = objectMapper.writeValueAsString(new RegistrationBody(
                fullName, email, password, password));
        return mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }

    private record RegistrationBody(
            String fullName, String email, String password, String confirmPassword) {}
}
