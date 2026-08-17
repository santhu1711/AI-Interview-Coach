package com.aiinterviewcoach.dto.response;

import com.aiinterviewcoach.enums.Difficulty;
import com.aiinterviewcoach.enums.ExperienceLevel;
import com.aiinterviewcoach.enums.FieldCategory;
import com.aiinterviewcoach.enums.InterviewDomain;
import com.aiinterviewcoach.enums.InterviewMode;
import com.aiinterviewcoach.enums.InterviewStatus;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record InterviewResponse(
        UUID id,
        FieldCategory fieldCategory,
        InterviewDomain interviewDomain,
        String customDomain,
        String topic,
        Difficulty difficulty,
        InterviewMode interviewMode,
        String targetRole,
        ExperienceLevel experienceLevel,
        int totalQuestions,
        int currentQuestionNumber,
        int followUpCount,
        int progressPercentage,
        InterviewStatus status,
        Integer overallScore,
        Instant startedAt,
        Instant completedAt,
        Instant createdAt,
        Instant updatedAt,
        List<InterviewMessageResponse> messages) {}
