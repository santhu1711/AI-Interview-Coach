package com.aiinterviewcoach.service.ai;

import com.aiinterviewcoach.enums.AnswerEvaluation;
import com.aiinterviewcoach.exception.AiProviderException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashSet;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class InterviewAiService {
    private static final int MAX_MESSAGE_LENGTH = 2_000;
    private static final int MAX_CATEGORY_LENGTH = 120;
    private static final Set<String> REQUIRED_FIELDS = Set.of(
            "message", "evaluation", "questionCategory", "isFollowUp", "shouldComplete");

    private final InterviewAiProvider provider;
    private final InterviewPromptFactory promptFactory;
    private final ObjectMapper objectMapper;

    public InterviewAiService(
            InterviewAiProvider provider,
            InterviewPromptFactory promptFactory,
            ObjectMapper objectMapper) {
        this.provider = provider;
        this.promptFactory = promptFactory;
        this.objectMapper = objectMapper;
    }

    public InterviewAiResponse generate(InterviewAiContext context) {
        validateContext(context);
        String prompt = promptFactory.create(context);
        String firstResponse = provider.generate(prompt);
        try {
            return parseAndValidate(firstResponse, context.firstQuestion());
        } catch (InvalidAiResponseException firstFailure) {
            String retryResponse = provider.generate(promptFactory.corrective(prompt, firstResponse));
            try {
                return parseAndValidate(retryResponse, context.firstQuestion());
            } catch (InvalidAiResponseException retryFailure) {
                throw new AiProviderException(
                        HttpStatus.BAD_GATEWAY,
                        "The AI provider returned an invalid response. Please try again.",
                        false,
                        retryFailure);
            }
        }
    }

    private InterviewAiResponse parseAndValidate(String rawResponse, boolean firstQuestion) {
        if (rawResponse == null || rawResponse.isBlank()) {
            throw new InvalidAiResponseException("AI response was empty.");
        }
        try {
            JsonNode root = objectMapper.readTree(extractJson(rawResponse));
            require(root.isObject(), "AI response must be a JSON object.");
            Set<String> fields = new HashSet<>();
            root.fieldNames().forEachRemaining(fields::add);
            require(fields.equals(REQUIRED_FIELDS), "AI response fields do not match the required contract.");
            String message = requiredText(root, "message", MAX_MESSAGE_LENGTH);
            require(countQuestions(message) == 1, "AI response must contain exactly one question.");
            String category = requiredText(root, "questionCategory", MAX_CATEGORY_LENGTH);
            AnswerEvaluation evaluation;
            try {
                evaluation = AnswerEvaluation.valueOf(requiredText(root, "evaluation", 30));
            } catch (IllegalArgumentException exception) {
                throw new InvalidAiResponseException("AI response contains an invalid evaluation.", exception);
            }
            require(root.has("isFollowUp") && root.get("isFollowUp").isBoolean(), "isFollowUp must be boolean.");
            require(root.has("shouldComplete") && root.get("shouldComplete").isBoolean(), "shouldComplete must be boolean.");
            require(!firstQuestion || evaluation == AnswerEvaluation.NOT_APPLICABLE,
                    "The first question must use NOT_APPLICABLE evaluation.");
            return new InterviewAiResponse(
                    message,
                    evaluation,
                    category,
                    root.get("isFollowUp").booleanValue(),
                    root.get("shouldComplete").booleanValue());
        } catch (JsonProcessingException exception) {
            throw new InvalidAiResponseException("AI response was not valid JSON.", exception);
        }
    }

    private static String extractJson(String rawResponse) {
        String candidate = rawResponse.trim();
        if (candidate.startsWith("```")) {
            int firstLineEnd = candidate.indexOf('\n');
            int closingFence = candidate.lastIndexOf("```");
            if (firstLineEnd >= 0 && closingFence > firstLineEnd) {
                candidate = candidate.substring(firstLineEnd + 1, closingFence).trim();
            }
        }
        int start = candidate.indexOf('{');
        int end = candidate.lastIndexOf('}');
        if (start < 0 || end < start) {
            throw new InvalidAiResponseException("AI response did not contain a JSON object.");
        }
        return candidate.substring(start, end + 1);
    }

    private static String requiredText(JsonNode root, String field, int maximumLength) {
        JsonNode value = root.get(field);
        require(value != null && value.isTextual() && !value.textValue().isBlank(), field + " is required.");
        String text = value.textValue().trim();
        require(text.length() <= maximumLength, field + " is too long.");
        return text;
    }

    private static long countQuestions(String message) {
        long count = 0;
        for (int index = 0; index < message.length(); index++) {
            if (message.charAt(index) == '?'
                    && (index == message.length() - 1
                    || Character.isWhitespace(message.charAt(index + 1))
                    || message.charAt(index + 1) == '\''
                    || message.charAt(index + 1) == '"')) {
                count++;
            }
        }
        return count;
    }

    private static void validateContext(InterviewAiContext context) {
        if (context == null) {
            throw new IllegalArgumentException("Interview AI context is required.");
        }
        if (context.fieldCategory() == null || context.interviewDomain() == null
                || context.interviewMode() == null || context.experienceLevel() == null
                || context.difficulty() == null) {
            throw new IllegalArgumentException("Interview AI context is incomplete.");
        }
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new InvalidAiResponseException(message);
        }
    }

    private static final class InvalidAiResponseException extends RuntimeException {
        private InvalidAiResponseException(String message) { super(message); }
        private InvalidAiResponseException(String message, Throwable cause) { super(message, cause); }
    }
}
