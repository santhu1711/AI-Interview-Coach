package com.aiinterviewcoach.service.ai;

import com.aiinterviewcoach.enums.AnswerEvaluation;

public record ReportQuestionFeedback(
        String question,
        String answerSummary,
        AnswerEvaluation evaluation,
        String feedback) {}
