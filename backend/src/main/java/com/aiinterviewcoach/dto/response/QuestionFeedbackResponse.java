package com.aiinterviewcoach.dto.response;

import com.aiinterviewcoach.enums.AnswerEvaluation;

public record QuestionFeedbackResponse(
        String question,
        String answerSummary,
        AnswerEvaluation evaluation,
        String feedback) {}
