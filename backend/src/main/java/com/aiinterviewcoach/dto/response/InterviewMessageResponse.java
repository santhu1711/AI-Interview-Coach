package com.aiinterviewcoach.dto.response;

import com.aiinterviewcoach.enums.AnswerEvaluation;
import com.aiinterviewcoach.enums.MessageRole;
import java.time.Instant;

public record InterviewMessageResponse(
        Long id,
        MessageRole role,
        String content,
        int sequenceNumber,
        Integer questionNumber,
        String questionCategory,
        AnswerEvaluation answerEvaluation,
        Instant createdAt) {}
