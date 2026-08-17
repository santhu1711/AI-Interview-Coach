package com.aiinterviewcoach.service.ai;

import com.aiinterviewcoach.enums.AnswerEvaluation;

public record InterviewAiResponse(
        String message,
        AnswerEvaluation evaluation,
        String questionCategory,
        boolean isFollowUp,
        boolean shouldComplete) {}
