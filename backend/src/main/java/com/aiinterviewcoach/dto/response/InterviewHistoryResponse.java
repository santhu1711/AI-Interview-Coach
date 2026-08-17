package com.aiinterviewcoach.dto.response;

import java.util.List;

public record InterviewHistoryResponse(
        List<InterviewSummaryResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last) {}
