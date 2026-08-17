package com.aiinterviewcoach.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.aiinterviewcoach.entity.InterviewMessage;
import com.aiinterviewcoach.entity.InterviewSession;
import com.aiinterviewcoach.enums.AnswerEvaluation;
import com.aiinterviewcoach.enums.MessageRole;
import com.aiinterviewcoach.repository.InterviewMessageRepository;
import com.aiinterviewcoach.repository.InterviewSessionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
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
class InterviewApiIntegrationTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private InterviewSessionRepository sessionRepository;
    @Autowired private InterviewMessageRepository messageRepository;

    @Test
    void createsItSessionAndPersistsFirstAssistantQuestion() throws Exception {
        String token = register("create-it@example.com");

        MvcResult result = createInterview(token, itRequest(5))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.currentQuestionNumber").value(1))
                .andExpect(jsonPath("$.followUpCount").value(0))
                .andExpect(jsonPath("$.messages.length()").value(1))
                .andExpect(jsonPath("$.messages[0].role").value("ASSISTANT"))
                .andExpect(jsonPath("$.messages[0].questionNumber").value(1))
                .andExpect(jsonPath("$.messages[0].answerEvaluation").value("NOT_APPLICABLE"))
                .andExpect(jsonPath("$.messages[0].content").value(
                        "What core concept from the selected technical domain would you apply here, and why?"))
                .andReturn();

        UUID sessionId = sessionId(result);
        InterviewSession session = sessionRepository.findById(sessionId).orElseThrow();
        List<InterviewMessage> messages = messageRepository
                .findAllByInterviewSessionIdOrderBySequenceNumberAsc(sessionId);
        assertThat(session.getStatus().name()).isEqualTo("IN_PROGRESS");
        assertThat(messages).singleElement().satisfies(message -> {
            assertThat(message.getRole()).isEqualTo(MessageRole.ASSISTANT);
            assertThat(message.getSequenceNumber()).isEqualTo(1);
        });
    }

    @Test
    void createsNonItSessionWithRelevantFirstQuestion() throws Exception {
        String token = register("create-non-it@example.com");

        createInterview(token, nonItRequest())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fieldCategory").value("NON_IT"))
                .andExpect(jsonPath("$.interviewDomain").value("CUSTOMER_SUPPORT"))
                .andExpect(jsonPath("$.messages[0].questionCategory").value("Professional Scenario"))
                .andExpect(jsonPath("$.messages[0].content").value(
                        "What challenging workplace situation have you handled, and what did you do?"));
    }

    @Test
    void submitsAnswerPersistsBothRolesAndAdvancesQuestion() throws Exception {
        String token = register("answer@example.com");
        UUID sessionId = sessionId(createInterview(token, itRequest(5)).andReturn());

        answer(token, sessionId, "A strong answer")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentQuestionNumber").value(2))
                .andExpect(jsonPath("$.messages.length()").value(3))
                .andExpect(jsonPath("$.messages[1].role").value("USER"))
                .andExpect(jsonPath("$.messages[1].sequenceNumber").value(2))
                .andExpect(jsonPath("$.messages[1].questionNumber").value(1))
                .andExpect(jsonPath("$.messages[1].answerEvaluation").value("STRONG"))
                .andExpect(jsonPath("$.messages[2].role").value("ASSISTANT"))
                .andExpect(jsonPath("$.messages[2].sequenceNumber").value(3))
                .andExpect(jsonPath("$.messages[2].questionNumber").value(2));

        assertThat(messageRepository.findAllByInterviewSessionIdOrderBySequenceNumberAsc(sessionId))
                .extracting(InterviewMessage::getRole)
                .containsExactly(MessageRole.ASSISTANT, MessageRole.USER, MessageRole.ASSISTANT);
    }

    @Test
    void generatesOnlyOneFollowUpPerPrimaryQuestion() throws Exception {
        String token = register("follow-up@example.com");
        UUID sessionId = sessionId(createInterview(token, itRequest(5)).andReturn());

        answer(token, sessionId, "This is a partial answer")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentQuestionNumber").value(1))
                .andExpect(jsonPath("$.followUpCount").value(1))
                .andExpect(jsonPath("$.messages[1].answerEvaluation").value("PARTIAL"))
                .andExpect(jsonPath("$.messages[2].questionNumber").value(1))
                .andExpect(jsonPath("$.messages[2].content").value(
                        "Could you expand on the most important missing part?"));

        answer(token, sessionId, "Another partial answer")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentQuestionNumber").value(2))
                .andExpect(jsonPath("$.followUpCount").value(1))
                .andExpect(jsonPath("$.messages[4].questionNumber").value(2));
    }

    @Test
    void completesAutomaticallyAtQuestionLimitAndRejectsFurtherAnswers() throws Exception {
        String token = register("limit@example.com");
        UUID sessionId = sessionId(createInterview(token, itRequest(5)).andReturn());

        for (int answerNumber = 1; answerNumber < 5; answerNumber++) {
            answer(token, sessionId, "Strong answer " + answerNumber)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                    .andExpect(jsonPath("$.currentQuestionNumber").value(answerNumber + 1));
        }
        answer(token, sessionId, "Strong final answer")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.currentQuestionNumber").value(5))
                .andExpect(jsonPath("$.progressPercentage").value(100))
                .andExpect(jsonPath("$.messages.length()").value(10));

        answer(token, sessionId, "Too late")
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(
                        "Answers can only be submitted to an interview in progress."));
    }

    @Test
    void enforcesManualCompletionAndAbandonmentTransitions() throws Exception {
        String token = register("states@example.com");
        UUID completedId = sessionId(createInterview(token, itRequest(5)).andReturn());
        UUID abandonedId = sessionId(createInterview(token, itRequest(5)).andReturn());

        mockMvc.perform(post("/api/interviews/{id}/complete", completedId)
                        .header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
        mockMvc.perform(post("/api/interviews/{id}/abandon", completedId)
                        .header("Authorization", bearer(token)))
                .andExpect(status().isConflict());

        mockMvc.perform(post("/api/interviews/{id}/abandon", abandonedId)
                        .header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ABANDONED"));
        answer(token, abandonedId, "Too late").andExpect(status().isConflict());
        mockMvc.perform(post("/api/interviews/{id}/complete", abandonedId)
                        .header("Authorization", bearer(token)))
                .andExpect(status().isConflict());
    }

    @Test
    void hidesSessionsAndMessagesFromOtherUsers() throws Exception {
        String ownerToken = register("owner@example.com");
        String otherToken = register("other@example.com");
        UUID sessionId = sessionId(createInterview(ownerToken, itRequest(5)).andReturn());

        mockMvc.perform(get("/api/interviews/{id}", sessionId)
                        .header("Authorization", bearer(otherToken)))
                .andExpect(status().isNotFound());
        answer(otherToken, sessionId, "Attempted answer").andExpect(status().isNotFound());
        mockMvc.perform(delete("/api/interviews/{id}", sessionId)
                        .header("Authorization", bearer(otherToken)))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/interviews/{id}", sessionId)
                        .header("Authorization", bearer(ownerToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messages.length()").value(1));
    }

    @Test
    void listsOnlyOwnedHistoryAndDeletesOwnedSessionWithTranscript() throws Exception {
        String ownerToken = register("history-owner@example.com");
        String otherToken = register("history-other@example.com");
        UUID firstId = sessionId(createInterview(ownerToken, itRequest(5)).andReturn());
        createInterview(ownerToken, nonItRequest()).andExpect(status().isCreated());
        createInterview(otherToken, itRequest(5)).andExpect(status().isCreated());

        mockMvc.perform(get("/api/interviews").header("Authorization", bearer(ownerToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(2));

        mockMvc.perform(delete("/api/interviews/{id}", firstId)
                        .header("Authorization", bearer(ownerToken)))
                .andExpect(status().isNoContent());
        assertThat(sessionRepository.findById(firstId)).isEmpty();
        assertThat(messageRepository.findAllByInterviewSessionIdOrderBySequenceNumberAsc(firstId)).isEmpty();
    }

    @Test
    void rejectsInvalidConfigurationMalformedIdsAndAnswerPayloads() throws Exception {
        String token = register("validation@example.com");
        String crossCategory = itRequest(5).replace("\"JAVA\"", "\"CUSTOMER_SUPPORT\"");
        createInterview(token, crossCategory)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.interviewDomain").exists());

        String crossMode = itRequest(5).replace("\"TECHNICAL\"", "\"SITUATIONAL\"");
        createInterview(token, crossMode)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.interviewMode").exists());

        createInterview(token, itRequest(4))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.totalQuestions").exists());

        String missingCustom = itRequest(5).replace("\"JAVA\"", "\"CUSTOM\"");
        createInterview(token, missingCustom)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.customDomain").exists());

        UUID sessionId = sessionId(createInterview(token, itRequest(5)).andReturn());
        answer(token, sessionId, " ")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.answer").exists());
        String oversized = "x".repeat(10_001);
        mockMvc.perform(post("/api/interviews/{id}/answers", sessionId)
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("answer", oversized))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.answer").exists());
        mockMvc.perform(get("/api/interviews/not-a-uuid")
                        .header("Authorization", bearer(token)))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/api/interviews/{id}", UUID.randomUUID())
                        .header("Authorization", bearer(token)))
                .andExpect(status().isNotFound());
    }

    @Test
    void detectsDuplicateAnswerWhenCurrentQuestionHasNoPendingAssistantMessage() throws Exception {
        String token = register("duplicate-answer@example.com");
        UUID sessionId = sessionId(createInterview(token, itRequest(5)).andReturn());
        InterviewSession session = sessionRepository.findById(sessionId).orElseThrow();
        InterviewMessage existingAnswer = new InterviewMessage();
        existingAnswer.setInterviewSession(session);
        existingAnswer.setRole(MessageRole.USER);
        existingAnswer.setContent("Existing answer");
        existingAnswer.setSequenceNumber(2);
        existingAnswer.setQuestionNumber(1);
        existingAnswer.setAnswerEvaluation(AnswerEvaluation.NOT_APPLICABLE);
        messageRepository.saveAndFlush(existingAnswer);

        answer(token, sessionId, "Duplicate answer")
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("The current question has already been answered."));
    }

    @Test
    void requiresAuthenticationForInterviewApis() throws Exception {
        mockMvc.perform(get("/api/interviews")).andExpect(status().isUnauthorized());
        mockMvc.perform(post("/api/interviews").contentType(MediaType.APPLICATION_JSON).content(itRequest(5)))
                .andExpect(status().isUnauthorized());
    }

    private org.springframework.test.web.servlet.ResultActions createInterview(String token, String request)
            throws Exception {
        return mockMvc.perform(post("/api/interviews")
                .header("Authorization", bearer(token))
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));
    }

    private org.springframework.test.web.servlet.ResultActions answer(String token, UUID sessionId, String answer)
            throws Exception {
        return mockMvc.perform(post("/api/interviews/{id}/answers", sessionId)
                .header("Authorization", bearer(token))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("answer", answer))));
    }

    private String register(String email) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "fullName", "Interview User",
                                "email", email,
                                "password", "Secure@123",
                                "confirmPassword", "Secure@123"))))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("accessToken").asText();
    }

    private static UUID sessionId(MvcResult result) throws Exception {
        return UUID.fromString(new ObjectMapper().readTree(result.getResponse().getContentAsString()).get("id").asText());
    }

    private static String bearer(String token) {
        return "Bearer " + token;
    }

    private static String itRequest(int totalQuestions) {
        return """
                {"fieldCategory":"IT","interviewDomain":"JAVA","customDomain":null,
                "topic":"Core Java","difficulty":"MEDIUM","interviewMode":"TECHNICAL",
                "targetRole":"Java Backend Developer","experienceLevel":"BEGINNER",
                "totalQuestions":%d}
                """.formatted(totalQuestions);
    }

    private static String nonItRequest() {
        return """
                {"fieldCategory":"NON_IT","interviewDomain":"CUSTOMER_SUPPORT","customDomain":null,
                "topic":"Customer complaint handling","difficulty":"MEDIUM","interviewMode":"SITUATIONAL",
                "targetRole":"Customer Support Specialist","experienceLevel":"INTERMEDIATE",
                "totalQuestions":5}
                """;
    }
}
