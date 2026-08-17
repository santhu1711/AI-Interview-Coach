package com.aiinterviewcoach.dto.response;

import com.aiinterviewcoach.enums.FieldCategory;
import java.util.List;
import java.util.Map;

public record InterviewOptionsResponse(
        List<InterviewOptionResponse> fieldCategories,
        Map<FieldCategory, String> domainLabels,
        Map<FieldCategory, List<InterviewOptionResponse>> domains,
        Map<FieldCategory, List<InterviewOptionResponse>> modes,
        List<InterviewOptionResponse> difficulties,
        List<InterviewOptionResponse> experienceLevels,
        int minimumQuestions,
        int maximumQuestions,
        int defaultQuestions,
        TextInputConstraintResponse customDomain,
        TextInputConstraintResponse targetRole) {}
