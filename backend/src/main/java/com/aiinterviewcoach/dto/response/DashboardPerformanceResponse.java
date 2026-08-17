package com.aiinterviewcoach.dto.response;

import java.util.List;

public record DashboardPerformanceResponse(
        List<ScoreTrendPointResponse> scoreTrend,
        List<DomainPerformanceResponse> domainPerformance,
        List<CategoryPerformanceResponse> categoryComparison) {}
