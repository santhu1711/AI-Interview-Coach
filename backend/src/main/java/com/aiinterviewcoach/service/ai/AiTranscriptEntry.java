package com.aiinterviewcoach.service.ai;

import com.aiinterviewcoach.enums.MessageRole;

public record AiTranscriptEntry(MessageRole role, String content) {}
