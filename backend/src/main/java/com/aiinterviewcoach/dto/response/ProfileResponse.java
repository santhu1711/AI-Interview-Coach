package com.aiinterviewcoach.dto.response;

import java.time.Instant;

public record ProfileResponse(Long id, String fullName, String email, Instant createdAt) {}
