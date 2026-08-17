package com.aiinterviewcoach.service.ai;

import com.aiinterviewcoach.enums.Recommendation;
import java.util.List;

public record ReportAiResponse(
        int overallScore,
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
        List<ReportQuestionFeedback> questionFeedback) {}
