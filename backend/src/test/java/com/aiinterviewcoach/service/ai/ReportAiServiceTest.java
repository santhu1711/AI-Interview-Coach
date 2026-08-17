package com.aiinterviewcoach.service.ai;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.aiinterviewcoach.entity.InterviewSession;
import com.aiinterviewcoach.enums.FieldCategory;
import com.aiinterviewcoach.enums.Recommendation;
import com.aiinterviewcoach.exception.AiProviderException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;

class ReportAiServiceTest {
    private final InterviewAiProvider provider = mock(InterviewAiProvider.class);
    private final ReportPromptFactory promptFactory = new ReportPromptFactory();
    private final ReportAiService service = new ReportAiService(provider, promptFactory, new ObjectMapper());

    @Test
    void parsesItScoresAndAppliesServerPassThreshold() {
        when(provider.generate(contains("REPORT_GENERATION_REQUEST"))).thenReturn(itResponse("FAIL"));

        ReportAiResponse result = service.generate(session(FieldCategory.IT), List.of());

        assertThat(result.overallScore()).isEqualTo(78);
        assertThat(result.technicalAccuracyScore()).isEqualTo(82);
        assertThat(result.situationalJudgementScore()).isNull();
        assertThat(result.recommendation()).isEqualTo(Recommendation.PASS);
        assertThat(result.questionFeedback()).hasSize(1);
    }

    @Test
    void acceptsNonItDimensionsAndRejectsItOnlyScores() {
        when(provider.generate(contains("REPORT_GENERATION_REQUEST"))).thenReturn(nonItResponse());
        ReportAiResponse result = service.generate(session(FieldCategory.NON_IT), List.of());

        assertThat(result.technicalAccuracyScore()).isNull();
        assertThat(result.situationalJudgementScore()).isEqualTo(70);
        assertThat(result.roleUnderstandingScore()).isEqualTo(69);
    }

    @Test
    void retriesAnInvalidCategoryShapeOnce() {
        when(provider.generate(anyString())).thenReturn(nonItResponse(), itResponse("PASS"));

        assertThat(service.generate(session(FieldCategory.IT), List.of()).overallScore()).isEqualTo(78);
        verify(provider).generate(contains("Your previous report was invalid"));
    }

    @Test
    void rejectsOutOfRangeScoresAfterCorrectiveRetry() {
        String invalid = itResponse("PASS").replace("\"overallScore\":78", "\"overallScore\":101");
        when(provider.generate(contains("REPORT_GENERATION_REQUEST"))).thenReturn(invalid);
        when(provider.generate(contains("Your previous report response was invalid"))).thenReturn(invalid);

        assertThatThrownBy(() -> service.generate(session(FieldCategory.IT), List.of()))
                .isInstanceOf(AiProviderException.class)
                .hasMessage("The AI provider returned an invalid report. Please try again.");
    }

    private static InterviewSession session(FieldCategory category) {
        InterviewSession session = new InterviewSession();
        session.setFieldCategory(category);
        return session;
    }

    private static String itResponse(String recommendation) {
        return """
                {"overallScore":78,"technicalAccuracyScore":82,"conceptualUnderstandingScore":76,
                "problemSolvingScore":80,"communicationScore":72,"confidenceScore":74,
                "situationalJudgementScore":null,"roleUnderstandingScore":null,
                "strengths":["Strong application"],"weaknesses":["Limited trade-offs"],
                "revisionAreas":["Trade-offs"],"verdict":"A good technical performance.",
                "recommendation":"%s","questionFeedback":[{"question":"Question?",
                "answerSummary":"Answer summary","evaluation":"STRONG","feedback":"Useful feedback"}]}
                """.formatted(recommendation);
    }

    private static String nonItResponse() {
        return """
                {"overallScore":68,"technicalAccuracyScore":null,"conceptualUnderstandingScore":null,
                "problemSolvingScore":66,"communicationScore":72,"confidenceScore":65,
                "situationalJudgementScore":70,"roleUnderstandingScore":69,
                "strengths":["Clear communication"],"weaknesses":["Limited evidence"],
                "revisionAreas":["STAR structure"],"verdict":"An adequate performance.",
                "recommendation":"PASS","questionFeedback":[{"question":"Question?",
                "answerSummary":"Answer summary","evaluation":"STRONG","feedback":"Useful feedback"}]}
                """;
    }
}
