package com.aiinterviewcoach.dto.response;

import com.aiinterviewcoach.enums.FieldCategory;
import com.aiinterviewcoach.enums.Recommendation;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record InterviewReportResponse(
        Long id,
        UUID sessionId,
        FieldCategory fieldCategory,
        int overallScore,
        String scoreInterpretation,
        Integer technicalAccuracyScore,
        Integer conceptualUnderstandingScore,
        Integer problemSolvingScore,
        Integer communicationScore,
        Integer confidenceScore,
        Integer situationalJudgementScore,
        Integer roleUnderstandingScore,
        List<String> strengths,
        List<String> weaknesses,
        List<String> revisionAreas,
        String verdict,
        Recommendation recommendation,
        List<QuestionFeedbackResponse> questionFeedback,
        Instant generatedAt) {}
