package com.aiinterviewcoach.dto.response;

import com.aiinterviewcoach.enums.FieldCategory;
import java.time.Instant;
import java.util.UUID;

public record ScoreTrendPointResponse(
        UUID sessionId,
        Instant generatedAt,
        FieldCategory fieldCategory,
        String domain,
        int score) {}
