package com.aiinterviewcoach.service.ai;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.ai.provider", havingValue = "deterministic-test")
public class DeterministicTestInterviewAiProvider implements InterviewAiProvider {
    @Override
    public String generate(String prompt) {
        boolean nonIt = prompt.contains("Field category: NON_IT");
        boolean firstQuestion = prompt.contains("Current question number: 0");
        String message = nonIt
                ? "Tell me about a challenging workplace situation and how you handled it."
                : "Explain a core concept from the selected technical domain and where you would apply it.";
        return """
                {"message":"%s","evaluation":"%s",
                "questionCategory":"%s","isFollowUp":false,"shouldComplete":false}
                """.formatted(
                message,
                firstQuestion ? "NOT_APPLICABLE" : "STRONG",
                nonIt ? "Professional Scenario" : "Technical Fundamentals");
    }
}
