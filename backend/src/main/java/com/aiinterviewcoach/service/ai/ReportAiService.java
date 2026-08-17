package com.aiinterviewcoach.service.ai;

import com.aiinterviewcoach.entity.InterviewMessage;
import com.aiinterviewcoach.entity.InterviewSession;
import com.aiinterviewcoach.enums.AnswerEvaluation;
import com.aiinterviewcoach.enums.FieldCategory;
import com.aiinterviewcoach.enums.Recommendation;
import com.aiinterviewcoach.exception.AiProviderException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class ReportAiService {
    private static final Set<String> REPORT_FIELDS = Set.of(
            "overallScore", "technicalAccuracyScore", "conceptualUnderstandingScore",
            "problemSolvingScore", "communicationScore", "confidenceScore",
            "situationalJudgementScore", "roleUnderstandingScore", "strengths", "weaknesses",
            "revisionAreas", "verdict", "recommendation", "questionFeedback");
    private static final Set<String> FEEDBACK_FIELDS = Set.of(
            "question", "answerSummary", "evaluation", "feedback");

    private final InterviewAiProvider provider;
    private final ReportPromptFactory promptFactory;
    private final ObjectMapper objectMapper;

    public ReportAiService(
            InterviewAiProvider provider, ReportPromptFactory promptFactory, ObjectMapper objectMapper) {
        this.provider = provider;
        this.promptFactory = promptFactory;
        this.objectMapper = objectMapper;
    }

    public ReportAiResponse generate(InterviewSession session, List<InterviewMessage> messages) {
        String prompt = promptFactory.create(session, messages);
        String first = provider.generate(prompt);
        try {
            return parse(first, session.getFieldCategory());
        } catch (InvalidReportResponseException exception) {
            String retry = provider.generate(promptFactory.corrective(prompt, first));
            try {
                return parse(retry, session.getFieldCategory());
            } catch (InvalidReportResponseException retryException) {
                throw new AiProviderException(
                        HttpStatus.BAD_GATEWAY,
                        "The AI provider returned an invalid report. Please try again.",
                        false,
                        retryException);
            }
        }
    }

    private ReportAiResponse parse(String raw, FieldCategory category) {
        if (raw == null || raw.isBlank()) {
            throw new InvalidReportResponseException("Report response was empty.");
        }
        try {
            JsonNode root = objectMapper.readTree(extractJson(raw));
            require(root.isObject() && fields(root).equals(REPORT_FIELDS), "Report fields are invalid.");
            int overall = score(root, "overallScore");
            Integer technical = nullableScore(root, "technicalAccuracyScore");
            Integer conceptual = nullableScore(root, "conceptualUnderstandingScore");
            Integer problem = nullableScore(root, "problemSolvingScore");
            Integer communication = nullableScore(root, "communicationScore");
            Integer confidence = nullableScore(root, "confidenceScore");
            Integer situational = nullableScore(root, "situationalJudgementScore");
            Integer role = nullableScore(root, "roleUnderstandingScore");
            validateDimensions(category, technical, conceptual, problem, communication, confidence, situational, role);

            List<String> strengths = textArray(root, "strengths");
            List<String> weaknesses = textArray(root, "weaknesses");
            List<String> revisionAreas = textArray(root, "revisionAreas");
            String verdict = text(root, "verdict", 5_000);
            try {
                Recommendation.valueOf(text(root, "recommendation", 20));
            } catch (IllegalArgumentException exception) {
                throw new InvalidReportResponseException("Report recommendation is invalid.", exception);
            }
            List<ReportQuestionFeedback> feedback = feedback(root.get("questionFeedback"));
            Recommendation recommendation = overall >= 60 ? Recommendation.PASS : Recommendation.FAIL;
            return new ReportAiResponse(
                    overall, technical, conceptual, problem, communication, confidence, situational, role,
                    strengths, weaknesses, revisionAreas, verdict, recommendation, feedback);
        } catch (JsonProcessingException exception) {
            throw new InvalidReportResponseException("Report response was not valid JSON.", exception);
        }
    }

    private static void validateDimensions(
            FieldCategory category,
            Integer technical,
            Integer conceptual,
            Integer problem,
            Integer communication,
            Integer confidence,
            Integer situational,
            Integer role) {
        require(problem != null && communication != null && confidence != null,
                "Shared category scores are required.");
        if (category == FieldCategory.IT) {
            require(technical != null && conceptual != null, "IT scores are required.");
            require(situational == null && role == null, "Non-IT-only scores must be null for IT reports.");
        } else {
            require(situational != null && role != null, "Non-IT scores are required.");
            require(technical == null && conceptual == null, "IT-only scores must be null for Non-IT reports.");
        }
    }

    private static List<ReportQuestionFeedback> feedback(JsonNode node) {
        require(node != null && node.isArray(), "questionFeedback must be an array.");
        List<ReportQuestionFeedback> result = new ArrayList<>();
        for (JsonNode item : node) {
            require(item.isObject() && fields(item).equals(FEEDBACK_FIELDS), "Question feedback fields are invalid.");
            AnswerEvaluation evaluation;
            try {
                evaluation = AnswerEvaluation.valueOf(text(item, "evaluation", 30));
            } catch (IllegalArgumentException exception) {
                throw new InvalidReportResponseException("Question feedback evaluation is invalid.", exception);
            }
            result.add(new ReportQuestionFeedback(
                    text(item, "question", 2_000),
                    text(item, "answerSummary", 2_000),
                    evaluation,
                    text(item, "feedback", 5_000)));
        }
        return List.copyOf(result);
    }

    private static List<String> textArray(JsonNode root, String field) {
        JsonNode node = root.get(field);
        require(node != null && node.isArray() && !node.isEmpty(), field + " must be a non-empty array.");
        List<String> values = new ArrayList<>();
        for (JsonNode value : node) {
            require(value.isTextual() && !value.textValue().isBlank(), field + " contains an invalid value.");
            values.add(value.textValue().trim());
        }
        return List.copyOf(values);
    }

    private static int score(JsonNode root, String field) {
        JsonNode node = root.get(field);
        require(node != null && node.isIntegralNumber(), field + " must be an integer.");
        int value = node.intValue();
        require(value >= 0 && value <= 100, field + " must be between 0 and 100.");
        return value;
    }

    private static Integer nullableScore(JsonNode root, String field) {
        JsonNode node = root.get(field);
        if (node == null || node.isNull()) {
            return null;
        }
        return score(root, field);
    }

    private static String text(JsonNode root, String field, int maximumLength) {
        JsonNode node = root.get(field);
        require(node != null && node.isTextual() && !node.textValue().isBlank(), field + " is required.");
        String value = node.textValue().trim();
        require(value.length() <= maximumLength, field + " is too long.");
        return value;
    }

    private static Set<String> fields(JsonNode node) {
        Set<String> fields = new HashSet<>();
        node.fieldNames().forEachRemaining(fields::add);
        return fields;
    }

    private static String extractJson(String raw) {
        String candidate = raw.trim();
        if (candidate.startsWith("```")) {
            int firstLine = candidate.indexOf('\n');
            int closing = candidate.lastIndexOf("```");
            if (firstLine >= 0 && closing > firstLine) {
                candidate = candidate.substring(firstLine + 1, closing).trim();
            }
        }
        int start = candidate.indexOf('{');
        int end = candidate.lastIndexOf('}');
        require(start >= 0 && end >= start, "Report response did not contain JSON.");
        return candidate.substring(start, end + 1);
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new InvalidReportResponseException(message);
        }
    }

    private static final class InvalidReportResponseException extends RuntimeException {
        private InvalidReportResponseException(String message) { super(message); }
        private InvalidReportResponseException(String message, Throwable cause) { super(message, cause); }
    }
}
