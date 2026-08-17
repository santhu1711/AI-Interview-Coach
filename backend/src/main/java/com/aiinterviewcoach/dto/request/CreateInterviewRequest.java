package com.aiinterviewcoach.dto.request;

import com.aiinterviewcoach.enums.Difficulty;
import com.aiinterviewcoach.enums.ExperienceLevel;
import com.aiinterviewcoach.enums.FieldCategory;
import com.aiinterviewcoach.enums.InterviewDomain;
import com.aiinterviewcoach.enums.InterviewMode;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateInterviewRequest(
        @NotNull(message = "Field category is required.") FieldCategory fieldCategory,
        @NotNull(message = "Interview domain is required.") InterviewDomain interviewDomain,
        @Size(max = 120, message = "Custom domain must not exceed 120 characters.") String customDomain,
        @NotBlank(message = "Topic is required.")
        @Size(max = 200, message = "Topic must not exceed 200 characters.") String topic,
        @NotNull(message = "Difficulty is required.") Difficulty difficulty,
        @NotNull(message = "Interview mode is required.") InterviewMode interviewMode,
        @NotBlank(message = "Target role is required.")
        @Size(min = 2, max = 150, message = "Target role must be between 2 and 150 characters.") String targetRole,
        @NotNull(message = "Experience level is required.") ExperienceLevel experienceLevel,
        @Min(value = 5, message = "Total questions must be at least 5.")
        @Max(value = 20, message = "Total questions must not exceed 20.") int totalQuestions) {}
