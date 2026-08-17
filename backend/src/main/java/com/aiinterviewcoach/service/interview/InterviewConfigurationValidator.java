package com.aiinterviewcoach.service.interview;

import com.aiinterviewcoach.enums.FieldCategory;
import com.aiinterviewcoach.enums.InterviewDomain;
import com.aiinterviewcoach.enums.InterviewMode;
import com.aiinterviewcoach.exception.InvalidInterviewConfigurationException;
import org.springframework.stereotype.Component;

@Component
public class InterviewConfigurationValidator {
    private final InterviewOptionsService optionsService;

    public InterviewConfigurationValidator(InterviewOptionsService optionsService) {
        this.optionsService = optionsService;
    }

    public void validateSelection(
            FieldCategory fieldCategory,
            InterviewDomain interviewDomain,
            InterviewMode interviewMode,
            String customDomain) {
        require(fieldCategory != null, "fieldCategory", "Field category is required.");
        require(interviewDomain != null, "interviewDomain", "Interview domain is required.");
        require(interviewMode != null, "interviewMode", "Interview mode is required.");

        require(
                optionsService.supportsDomain(fieldCategory, interviewDomain),
                "interviewDomain",
                "The selected domain does not belong to the selected field category.");
        require(
                optionsService.supportsMode(fieldCategory, interviewMode),
                "interviewMode",
                "The selected mode does not belong to the selected field category.");

        if (interviewDomain == InterviewDomain.CUSTOM) {
            validateLength(
                    customDomain,
                    "customDomain",
                    InterviewOptionsService.CUSTOM_DOMAIN_MINIMUM_LENGTH,
                    InterviewOptionsService.CUSTOM_DOMAIN_MAXIMUM_LENGTH,
                    "Custom domain");
        } else {
            require(
                    customDomain == null || customDomain.isBlank(),
                    "customDomain",
                    "Custom domain is only allowed when the CUSTOM domain is selected.");
        }
    }

    public void validateTargetRole(String targetRole) {
        validateLength(
                targetRole,
                "targetRole",
                InterviewOptionsService.TARGET_ROLE_MINIMUM_LENGTH,
                InterviewOptionsService.TARGET_ROLE_MAXIMUM_LENGTH,
                "Target role");
    }

    public void validateTotalQuestions(int totalQuestions) {
        require(
                totalQuestions >= InterviewOptionsService.MINIMUM_QUESTIONS
                        && totalQuestions <= InterviewOptionsService.MAXIMUM_QUESTIONS,
                "totalQuestions",
                "Total questions must be between " + InterviewOptionsService.MINIMUM_QUESTIONS
                        + " and " + InterviewOptionsService.MAXIMUM_QUESTIONS + ".");
    }

    private static void validateLength(String value, String field, int minimum, int maximum, String label) {
        require(value != null && !value.isBlank(), field, label + " is required.");
        int length = value.trim().length();
        require(
                length >= minimum && length <= maximum,
                field,
                label + " must be between " + minimum + " and " + maximum + " characters.");
    }

    private static void require(boolean condition, String field, String message) {
        if (!condition) {
            throw new InvalidInterviewConfigurationException(field, message);
        }
    }
}
