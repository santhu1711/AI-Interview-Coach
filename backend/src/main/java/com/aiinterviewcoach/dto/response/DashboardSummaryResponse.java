package com.aiinterviewcoach.dto.response;

import java.util.List;

public record DashboardSummaryResponse(
        long totalInterviews,
        long completedInterviews,
        long activeInterviews,
        long itInterviewCount,
        long nonItInterviewCount,
        Double averageScore,
        Double averageItScore,
        Double averageNonItScore,
        Integer highestScore,
        double passPercentage,
        String strongestDomain,
        String weakestDomain,
        List<InterviewSummaryResponse> recentInterviews) {}
