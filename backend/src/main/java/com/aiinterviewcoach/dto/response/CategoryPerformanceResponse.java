package com.aiinterviewcoach.dto.response;

import com.aiinterviewcoach.enums.FieldCategory;

public record CategoryPerformanceResponse(
        FieldCategory fieldCategory,
        long interviewCount,
        Double averageScore) {}
