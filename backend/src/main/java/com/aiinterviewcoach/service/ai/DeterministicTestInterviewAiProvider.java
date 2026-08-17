package com.aiinterviewcoach.service.ai;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.ai.provider", havingValue = "deterministic-test")
public class DeterministicTestInterviewAiProvider implements InterviewAiProvider {
    @Override
    public String generate(String prompt) {
        if (prompt.contains("REPORT_GENERATION_REQUEST")) {
            return report(prompt.contains("fieldCategory=NON_IT"));
        }
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

    private static String report(boolean nonIt) {
        if (nonIt) {
            return """
                    {"overallScore":68,"technicalAccuracyScore":null,
                    "conceptualUnderstandingScore":null,"problemSolvingScore":66,
                    "communicationScore":72,"confidenceScore":65,
                    "situationalJudgementScore":70,"roleUnderstandingScore":69,
                    "strengths":["Explained the customer situation clearly."],
                    "weaknesses":["The response needed a more measurable outcome."],
                    "revisionAreas":["STAR response structure","Customer de-escalation"],
                    "verdict":"An adequate response with clear communication and room for stronger evidence.",
                    "recommendation":"PASS","questionFeedback":[{"question":"What challenging workplace situation have you handled, and what did you do?",
                    "answerSummary":"The candidate described handling a workplace situation.",
                    "evaluation":"STRONG","feedback":"Add a measurable result to strengthen the answer."}]}
                    """;
        }
        return """
                {"overallScore":78,"technicalAccuracyScore":82,
                "conceptualUnderstandingScore":76,"problemSolvingScore":80,
                "communicationScore":72,"confidenceScore":74,
                "situationalJudgementScore":null,"roleUnderstandingScore":null,
                "strengths":["Applied the technical concept to a practical situation."],
                "weaknesses":["The trade-off discussion could be more precise."],
                "revisionAreas":["Technical trade-offs","Edge cases"],
                "verdict":"A good technical performance with solid practical understanding.",
                "recommendation":"PASS","questionFeedback":[{"question":"What core concept from the selected technical domain would you apply here, and why?",
                "answerSummary":"The candidate gave a practical technical answer.",
                "evaluation":"STRONG","feedback":"Explain the main trade-off in more depth."}]}
                """;
    }
}
