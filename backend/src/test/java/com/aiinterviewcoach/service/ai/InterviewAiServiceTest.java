package com.aiinterviewcoach.service.ai;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.aiinterviewcoach.enums.AnswerEvaluation;
import com.aiinterviewcoach.enums.Difficulty;
import com.aiinterviewcoach.enums.ExperienceLevel;
import com.aiinterviewcoach.enums.FieldCategory;
import com.aiinterviewcoach.enums.InterviewDomain;
import com.aiinterviewcoach.enums.InterviewMode;
import com.aiinterviewcoach.exception.AiProviderException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;

class InterviewAiServiceTest {
    private final InterviewAiProvider provider = mock(InterviewAiProvider.class);
    private final InterviewPromptFactory promptFactory = new InterviewPromptFactory();
    private final InterviewAiService service =
            new InterviewAiService(provider, promptFactory, new ObjectMapper());

    @Test
    void parsesValidStructuredResponse() {
        when(provider.generate(contains("Field category: IT"))).thenReturn(validResponse());

        InterviewAiResponse response = service.generate(context(FieldCategory.IT));

        assertThat(response.message()).contains("encapsulation");
        assertThat(response.evaluation()).isEqualTo(AnswerEvaluation.NOT_APPLICABLE);
        assertThat(response.questionCategory()).isEqualTo("Java Fundamentals");
        assertThat(response.isFollowUp()).isFalse();
        assertThat(response.shouldComplete()).isFalse();
    }

    @Test
    void removesMarkdownFencesBeforeParsing() {
        when(provider.generate(contains("Field category: IT")))
                .thenReturn("```json\n" + validResponse() + "\n```");

        assertThat(service.generate(context(FieldCategory.IT)).questionCategory())
                .isEqualTo("Java Fundamentals");
    }

    @Test
    void retriesMalformedOrIncompleteJsonOnceWithCorrectivePrompt() {
        when(provider.generate(contains("Field category: IT"))).thenReturn("not-json");
        when(provider.generate(contains("Your previous response was invalid"))).thenReturn(validResponse());

        assertThat(service.generate(context(FieldCategory.IT)).message()).contains("encapsulation");
        verify(provider).generate(contains("Invalid response:\nnot-json"));
    }

    @Test
    void returnsProfessionalErrorWhenCorrectiveRetryIsStillInvalid() {
        when(provider.generate(contains("Field category: IT"))).thenReturn("{}");
        when(provider.generate(contains("Your previous response was invalid"))).thenReturn("{\"message\":\"Missing fields\"}");

        assertThatThrownBy(() -> service.generate(context(FieldCategory.IT)))
                .isInstanceOf(AiProviderException.class)
                .hasMessage("The AI provider returned an invalid response. Please try again.");
    }

    @Test
    void enforcesNotApplicableEvaluationForFirstQuestion() {
        when(provider.generate(contains("Field category: IT")))
                .thenReturn(validResponse().replace("NOT_APPLICABLE", "STRONG"));
        when(provider.generate(contains("Your previous response was invalid")))
                .thenReturn(validResponse().replace("NOT_APPLICABLE", "PARTIAL"));

        assertThatThrownBy(() -> service.generate(context(FieldCategory.IT)))
                .isInstanceOf(AiProviderException.class)
                .hasMessageContaining("invalid response");
    }

    @Test
    void rejectsUnexpectedFieldsInStructuredResponse() {
        String responseWithExtraField = validResponse().replace(
                "\"shouldComplete\":false", "\"shouldComplete\":false,\"hint\":\"Do not expose\"");
        when(provider.generate(contains("Field category: IT"))).thenReturn(responseWithExtraField);
        when(provider.generate(contains("Your previous response was invalid"))).thenReturn(responseWithExtraField);

        assertThatThrownBy(() -> service.generate(context(FieldCategory.IT)))
                .isInstanceOf(AiProviderException.class)
                .hasMessageContaining("invalid response");
    }

    @Test
    void retriesResponsesContainingMoreThanOneQuestion() {
        String multipleQuestions = validResponse().replace(
                "How does encapsulation help a Java application?",
                "What is encapsulation? Why is it useful?");
        when(provider.generate(contains("Field category: IT"))).thenReturn(multipleQuestions);
        when(provider.generate(contains("Your previous response was invalid"))).thenReturn(validResponse());

        assertThat(service.generate(context(FieldCategory.IT)).message()).contains("encapsulation");
        verify(provider).generate(contains("Your previous response was invalid"));
    }

    @Test
    void propagatesProviderFailuresWithoutTreatingThemAsMalformedJson() {
        AiProviderException failure = new AiProviderException(
                org.springframework.http.HttpStatus.GATEWAY_TIMEOUT,
                "AI provider timed out. Please try again.",
                true);
        when(provider.generate(contains("Field category: IT"))).thenThrow(failure);

        assertThatThrownBy(() -> service.generate(context(FieldCategory.IT))).isSameAs(failure);
    }

    private static InterviewAiContext context(FieldCategory category) {
        return new InterviewAiContext(
                category,
                category == FieldCategory.IT ? InterviewDomain.JAVA : InterviewDomain.CUSTOMER_SUPPORT,
                null,
                category == FieldCategory.IT ? "Object-oriented programming" : "Complaint handling",
                category == FieldCategory.IT ? InterviewMode.TECHNICAL : InterviewMode.SITUATIONAL,
                category == FieldCategory.IT ? "Java Developer" : "Support Specialist",
                ExperienceLevel.INTERMEDIATE,
                Difficulty.MEDIUM,
                10,
                0,
                List.of(),
                List.of(),
                AnswerEvaluation.NOT_APPLICABLE,
                0);
    }

    private static String validResponse() {
        return """
                {"message":"How does encapsulation help a Java application?",
                "evaluation":"NOT_APPLICABLE","questionCategory":"Java Fundamentals",
                "isFollowUp":false,"shouldComplete":false}
                """;
    }
}
