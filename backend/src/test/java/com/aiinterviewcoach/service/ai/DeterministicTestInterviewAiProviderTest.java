package com.aiinterviewcoach.service.ai;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class DeterministicTestInterviewAiProviderTest {
    private final DeterministicTestInterviewAiProvider provider = new DeterministicTestInterviewAiProvider();

    @Test
    void returnsRelevantDeterministicItAndNonItQuestions() {
        String it = provider.generate("Field category: IT\nCurrent question number: 0");
        String nonIt = provider.generate("Field category: NON_IT\nCurrent question number: 0");

        assertThat(it).contains("technical domain", "Technical Fundamentals", "NOT_APPLICABLE");
        assertThat(nonIt).contains("workplace situation", "Professional Scenario", "NOT_APPLICABLE");
    }
}
