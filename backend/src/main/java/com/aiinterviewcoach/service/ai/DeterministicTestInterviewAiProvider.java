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
        String latestAnswer = latestAnswer(prompt).toLowerCase();
        boolean followUp = !firstQuestion && latestAnswer.contains("partial");
        String evaluation = firstQuestion
                ? "NOT_APPLICABLE"
                : followUp ? "PARTIAL" : latestAnswer.contains("incorrect") ? "INCORRECT" : "STRONG";
        String message = followUp
                ? "Could you expand on the most important missing part?"
                : nonIt
                        ? "What challenging workplace situation have you handled, and what did you do?"
                        : "What core concept from the selected technical domain would you apply here, and why?";
        return """
                {"message":"%s","evaluation":"%s",
                "questionCategory":"%s","isFollowUp":%s,"shouldComplete":false}
                """.formatted(
                message,
                evaluation,
                nonIt ? "Professional Scenario" : "Technical Fundamentals",
                followUp);
    }

    private static String latestAnswer(String prompt) {
        int start = prompt.lastIndexOf("USER: ");
        if (start < 0) {
            return "";
        }
        int contentStart = start + "USER: ".length();
        int end = prompt.indexOf('\n', contentStart);
        return end < 0 ? prompt.substring(contentStart) : prompt.substring(contentStart, end);
    }
}
