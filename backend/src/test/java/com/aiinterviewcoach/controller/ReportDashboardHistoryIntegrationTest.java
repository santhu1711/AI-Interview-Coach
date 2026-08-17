package com.aiinterviewcoach.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.aiinterviewcoach.entity.InterviewReport;
import com.aiinterviewcoach.entity.InterviewSession;
import com.aiinterviewcoach.enums.InterviewStatus;
import com.aiinterviewcoach.repository.InterviewReportRepository;
import com.aiinterviewcoach.repository.InterviewSessionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.UUID;
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
class ReportDashboardHistoryIntegrationTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private InterviewReportRepository reportRepository;
    @Autowired private InterviewSessionRepository sessionRepository;

    @Test
    void generatesAndPersistsItReportWithRelevantDimensions() throws Exception {
        String token = register("phase7-it@example.com");
        UUID sessionId = completedInterview(token, itRequest("Core Java", "Java Backend Developer"));

        mockMvc.perform(post("/api/interviews/{id}/report", sessionId)
                        .header("Authorization", bearer(token)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.overallScore").value(78))
                .andExpect(jsonPath("$.scoreInterpretation").value("Good"))
                .andExpect(jsonPath("$.technicalAccuracyScore").value(82))
                .andExpect(jsonPath("$.conceptualUnderstandingScore").value(76))
                .andExpect(jsonPath("$.problemSolvingScore").value(80))
                .andExpect(jsonPath("$.communicationScore").value(72))
                .andExpect(jsonPath("$.confidenceScore").value(74))
                .andExpect(jsonPath("$.situationalJudgementScore").doesNotExist())
                .andExpect(jsonPath("$.roleUnderstandingScore").doesNotExist())
                .andExpect(jsonPath("$.recommendation").value("PASS"))
                .andExpect(jsonPath("$.strengths.length()").value(1))
                .andExpect(jsonPath("$.weaknesses.length()").value(1))
                .andExpect(jsonPath("$.revisionAreas.length()").value(2))
                .andExpect(jsonPath("$.questionFeedback[0].evaluation").value("STRONG"));

        InterviewReport persisted = reportRepository.findByInterviewSessionId(sessionId).orElseThrow();
        InterviewSession session = sessionRepository.findById(sessionId).orElseThrow();
        assertThat(persisted.getOverallScore()).isEqualTo(78);
        assertThat(persisted.getQuestionFeedbackJson()).contains("technical answer");
        assertThat(session.getOverallScore()).isEqualTo(78);
        assertThat(session.getStatus()).isEqualTo(InterviewStatus.REPORT_GENERATED);

        mockMvc.perform(get("/api/interviews/{id}/report", sessionId)
                        .header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(persisted.getId()))
                .andExpect(jsonPath("$.questionFeedback[0].feedback").isNotEmpty());
    }

    @Test
    void generatesNonItReportWithOnlyNonItDimensions() throws Exception {
        String token = register("phase7-non-it@example.com");
        UUID sessionId = completedInterview(token, nonItRequest("Customer complaint handling"));

        mockMvc.perform(post("/api/interviews/{id}/report", sessionId)
                        .header("Authorization", bearer(token)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.overallScore").value(68))
                .andExpect(jsonPath("$.scoreInterpretation").value("Adequate"))
                .andExpect(jsonPath("$.technicalAccuracyScore").doesNotExist())
                .andExpect(jsonPath("$.conceptualUnderstandingScore").doesNotExist())
                .andExpect(jsonPath("$.situationalJudgementScore").value(70))
                .andExpect(jsonPath("$.roleUnderstandingScore").value(69))
                .andExpect(jsonPath("$.problemSolvingScore").value(66))
                .andExpect(jsonPath("$.communicationScore").value(72))
                .andExpect(jsonPath("$.confidenceScore").value(65))
                .andExpect(jsonPath("$.recommendation").value("PASS"));
    }

    @Test
    void rejectsEarlyAndDuplicateReportGeneration() throws Exception {
        String token = register("phase7-duplicate@example.com");
        UUID activeId = sessionId(createInterview(token, itRequest("Active topic", "Developer")));
        mockMvc.perform(post("/api/interviews/{id}/report", activeId)
                        .header("Authorization", bearer(token)))
                .andExpect(status().isConflict());

        UUID completedId = completedInterview(token, itRequest("Completed topic", "Developer"));
        generateReport(token, completedId).andExpect(status().isCreated());
        generateReport(token, completedId)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("A report has already been generated for this interview."));
        assertThat(reportRepository.findAll()).hasSize(1);
    }

    @Test
    void enforcesReportOwnershipAndAuthentication() throws Exception {
        String owner = register("phase7-report-owner@example.com");
        String other = register("phase7-report-other@example.com");
        UUID sessionId = completedInterview(owner, itRequest("Owned topic", "Developer"));
        generateReport(owner, sessionId).andExpect(status().isCreated());

        generateReport(other, sessionId).andExpect(status().isNotFound());
        mockMvc.perform(get("/api/interviews/{id}/report", sessionId)
                        .header("Authorization", bearer(other)))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/interviews/{id}/report", sessionId))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/dashboard/summary")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/dashboard/performance")).andExpect(status().isUnauthorized());
    }

    @Test
    void calculatesOwnedDashboardSummaryAndPerformance() throws Exception {
        String owner = register("phase7-dashboard-owner@example.com");
        String other = register("phase7-dashboard-other@example.com");
        UUID itId = completedInterview(owner, itRequest("Core Java", "Backend Developer"));
        UUID nonItId = completedInterview(owner, nonItRequest("Complaint handling"));
        generateReport(owner, itId).andExpect(status().isCreated());
        generateReport(owner, nonItId).andExpect(status().isCreated());
        createInterview(owner, itRequest("Active Java", "Junior Developer"));
        UUID foreignId = completedInterview(other, itRequest("Foreign", "Developer"));
        generateReport(other, foreignId).andExpect(status().isCreated());

        mockMvc.perform(get("/api/dashboard/summary").header("Authorization", bearer(owner)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalInterviews").value(3))
                .andExpect(jsonPath("$.completedInterviews").value(2))
                .andExpect(jsonPath("$.activeInterviews").value(1))
                .andExpect(jsonPath("$.itInterviewCount").value(2))
                .andExpect(jsonPath("$.nonItInterviewCount").value(1))
                .andExpect(jsonPath("$.averageScore").value(73.0))
                .andExpect(jsonPath("$.averageItScore").value(78.0))
                .andExpect(jsonPath("$.averageNonItScore").value(68.0))
                .andExpect(jsonPath("$.highestScore").value(78))
                .andExpect(jsonPath("$.passPercentage").value(100.0))
                .andExpect(jsonPath("$.strongestDomain").value("JAVA"))
                .andExpect(jsonPath("$.weakestDomain").value("CUSTOMER_SUPPORT"))
                .andExpect(jsonPath("$.recentInterviews.length()").value(3));

        mockMvc.perform(get("/api/dashboard/performance").header("Authorization", bearer(owner)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scoreTrend.length()").value(2))
                .andExpect(jsonPath("$.domainPerformance.length()").value(2))
                .andExpect(jsonPath("$.domainPerformance[0].domain").value("CUSTOMER_SUPPORT"))
                .andExpect(jsonPath("$.domainPerformance[1].domain").value("JAVA"))
                .andExpect(jsonPath("$.categoryComparison.length()").value(2));
    }

    @Test
    void filtersSortsAndPaginatesOwnedHistory() throws Exception {
        String owner = register("phase7-history-owner@example.com");
        String other = register("phase7-history-other@example.com");
        UUID itId = completedInterview(owner, itRequest("Core Java", "Backend Developer"));
        UUID nonItId = completedInterview(owner, nonItRequest("Complaint handling"));
        generateReport(owner, itId).andExpect(status().isCreated());
        generateReport(owner, nonItId).andExpect(status().isCreated());
        createInterview(owner, itRequest("Spring APIs", "Platform Engineer"));
        createInterview(other, itRequest("Core Java", "Backend Developer"));

        mockMvc.perform(get("/api/interviews")
                        .param("fieldCategory", "IT")
                        .param("status", "REPORT_GENERATED")
                        .param("sort", "HIGHEST_SCORE")
                        .param("page", "0").param("size", "1")
                        .header("Authorization", bearer(owner)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(itId.toString()))
                .andExpect(jsonPath("$.content[0].overallScore").value(78))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.first").value(true));

        mockMvc.perform(get("/api/interviews")
                        .param("search", "platform")
                        .param("domain", "JAVA")
                        .param("mode", "TECHNICAL")
                        .param("difficulty", "MEDIUM")
                        .header("Authorization", bearer(owner)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].targetRole").value("Platform Engineer"));

        mockMvc.perform(get("/api/interviews").param("size", "0")
                        .header("Authorization", bearer(owner)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void returnsNeutralDashboardForUserWithoutInterviews() throws Exception {
        String token = register("phase7-empty-dashboard@example.com");
        mockMvc.perform(get("/api/dashboard/summary").header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalInterviews").value(0))
                .andExpect(jsonPath("$.averageScore").doesNotExist())
                .andExpect(jsonPath("$.highestScore").doesNotExist())
                .andExpect(jsonPath("$.passPercentage").value(0.0))
                .andExpect(jsonPath("$.recentInterviews").isEmpty());
    }

    private UUID completedInterview(String token, String request) throws Exception {
        UUID id = sessionId(createInterview(token, request));
        mockMvc.perform(post("/api/interviews/{id}/answers", id)
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("answer", "A strong answer"))))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/interviews/{id}/complete", id)
                        .header("Authorization", bearer(token)))
                .andExpect(status().isOk());
        return id;
    }

    private MvcResult createInterview(String token, String request) throws Exception {
        return mockMvc.perform(post("/api/interviews")
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(status().isCreated()).andReturn();
    }

    private org.springframework.test.web.servlet.ResultActions generateReport(String token, UUID id)
            throws Exception {
        return mockMvc.perform(post("/api/interviews/{id}/report", id)
                .header("Authorization", bearer(token)));
    }

    private String register(String email) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "fullName", "Phase Seven User", "email", email,
                                "password", "Secure@123", "confirmPassword", "Secure@123"))))
                .andExpect(status().isCreated()).andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("accessToken").asText();
    }

    private UUID sessionId(MvcResult result) throws Exception {
        return UUID.fromString(objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText());
    }

    private static String bearer(String token) { return "Bearer " + token; }

    private static String itRequest(String topic, String role) {
        return """
                {"fieldCategory":"IT","interviewDomain":"JAVA","customDomain":null,
                "topic":"%s","difficulty":"MEDIUM","interviewMode":"TECHNICAL",
                "targetRole":"%s","experienceLevel":"BEGINNER","totalQuestions":5}
                """.formatted(topic, role);
    }

    private static String nonItRequest(String topic) {
        return """
                {"fieldCategory":"NON_IT","interviewDomain":"CUSTOMER_SUPPORT","customDomain":null,
                "topic":"%s","difficulty":"MEDIUM","interviewMode":"SITUATIONAL",
                "targetRole":"Customer Support Specialist","experienceLevel":"INTERMEDIATE","totalQuestions":5}
                """.formatted(topic);
    }
}
