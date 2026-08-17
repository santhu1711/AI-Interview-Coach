package com.aiinterviewcoach.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.aiinterviewcoach.config.SecurityConfig;
import com.aiinterviewcoach.security.DatabaseUserDetailsService;
import com.aiinterviewcoach.security.JwtService;

@WebMvcTest(HealthController.class)
@Import(SecurityConfig.class)
class HealthControllerTest {
    @Autowired private MockMvc mockMvc;
    @MockitoBean private JwtService jwtService;
    @MockitoBean private DatabaseUserDetailsService userDetailsService;

    @Test
    void reportsHealthy() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("UP")));
    }

    @Test
    void keepsUnlistedEndpointsProtected() throws Exception {
        mockMvc.perform(get("/api/private"))
                .andExpect(status().isUnauthorized());
    }
}
