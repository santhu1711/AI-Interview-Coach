package com.aiinterviewcoach.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class InterviewOptionsApiIntegrationTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Test
    void rejectsUnauthenticatedOptionsRequest() throws Exception {
        mockMvc.perform(get("/api/interview-options"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Authentication is required."));
    }

    @Test
    void returnsCategoryScopedOptionsForAuthenticatedUser() throws Exception {
        String token = registerAndGetToken();

        mockMvc.perform(get("/api/interview-options")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fieldCategories[0].value").value("IT"))
                .andExpect(jsonPath("$.fieldCategories[1].label").value("Non-IT Field"))
                .andExpect(jsonPath("$.domainLabels.IT").value("Technical Domain"))
                .andExpect(jsonPath("$.domainLabels.NON_IT").value("Professional Domain"))
                .andExpect(jsonPath("$.domains.IT.length()").value(24))
                .andExpect(jsonPath("$.domains.NON_IT.length()").value(20))
                .andExpect(jsonPath("$.modes.IT.length()").value(7))
                .andExpect(jsonPath("$.modes.NON_IT.length()").value(8))
                .andExpect(jsonPath("$.minimumQuestions").value(5))
                .andExpect(jsonPath("$.maximumQuestions").value(20))
                .andExpect(jsonPath("$.defaultQuestions").value(10))
                .andExpect(jsonPath("$.customDomain.minimumLength").value(2))
                .andExpect(jsonPath("$.customDomain.maximumLength").value(120))
                .andExpect(jsonPath("$.targetRole.maximumLength").value(150));
    }

    private String registerAndGetToken() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fullName": "Options User",
                                  "email": "options@example.com",
                                  "password": "Secure@123",
                                  "confirmPassword": "Secure@123"
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn();
        JsonNode response = objectMapper.readTree(result.getResponse().getContentAsString());
        return response.get("accessToken").asText();
    }
}
