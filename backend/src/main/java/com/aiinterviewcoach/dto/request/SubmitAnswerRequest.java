package com.aiinterviewcoach.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SubmitAnswerRequest(
        @NotBlank(message = "Answer is required.")
        @Size(max = 10_000, message = "Answer must not exceed 10000 characters.") String answer) {}
