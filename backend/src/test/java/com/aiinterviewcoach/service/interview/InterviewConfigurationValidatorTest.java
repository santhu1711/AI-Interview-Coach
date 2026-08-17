package com.aiinterviewcoach.service.interview;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatCode;

import com.aiinterviewcoach.enums.FieldCategory;
import com.aiinterviewcoach.enums.InterviewDomain;
import com.aiinterviewcoach.enums.InterviewMode;
import com.aiinterviewcoach.exception.InvalidInterviewConfigurationException;
import org.junit.jupiter.api.Test;

class InterviewConfigurationValidatorTest {
    private final InterviewConfigurationValidator validator =
            new InterviewConfigurationValidator(new InterviewOptionsService());

    @Test
    void acceptsValidItAndNonItSelections() {
        assertThatCode(() -> validator.validateSelection(
                        FieldCategory.IT, InterviewDomain.JAVA, InterviewMode.CODING, null))
                .doesNotThrowAnyException();
        assertThatCode(() -> validator.validateSelection(
                        FieldCategory.NON_IT, InterviewDomain.CUSTOMER_SUPPORT,
                        InterviewMode.SITUATIONAL, null))
                .doesNotThrowAnyException();
    }

    @Test
    void rejectsDomainsAndModesFromAnotherCategory() {
        assertThatThrownBy(() -> validator.validateSelection(
                        FieldCategory.IT, InterviewDomain.CUSTOMER_SUPPORT,
                        InterviewMode.TECHNICAL, null))
                .isInstanceOf(InvalidInterviewConfigurationException.class)
                .hasMessageContaining("domain does not belong");
        assertThatThrownBy(() -> validator.validateSelection(
                        FieldCategory.NON_IT, InterviewDomain.SALES,
                        InterviewMode.CODING, null))
                .isInstanceOf(InvalidInterviewConfigurationException.class)
                .hasMessageContaining("mode does not belong");
    }

    @Test
    void enforcesConditionalCustomDomainRules() {
        assertThatCode(() -> validator.validateSelection(
                        FieldCategory.IT, InterviewDomain.CUSTOM,
                        InterviewMode.TECHNICAL, "Platform Engineering"))
                .doesNotThrowAnyException();
        assertThatThrownBy(() -> validator.validateSelection(
                        FieldCategory.NON_IT, InterviewDomain.CUSTOM,
                        InterviewMode.ROLE_SPECIFIC, " "))
                .isInstanceOf(InvalidInterviewConfigurationException.class)
                .hasMessage("Custom domain is required.");
        assertThatThrownBy(() -> validator.validateSelection(
                        FieldCategory.IT, InterviewDomain.JAVA,
                        InterviewMode.TECHNICAL, "Spring"))
                .isInstanceOf(InvalidInterviewConfigurationException.class)
                .hasMessageContaining("only allowed");
    }

    @Test
    void validatesTargetRoleAndQuestionBounds() {
        assertThatCode(() -> {
                    validator.validateTargetRole("Backend Engineer");
                    validator.validateTotalQuestions(10);
                })
                .doesNotThrowAnyException();
        assertThatThrownBy(() -> validator.validateTargetRole(" "))
                .isInstanceOf(InvalidInterviewConfigurationException.class)
                .hasMessage("Target role is required.");
        assertThatThrownBy(() -> validator.validateTotalQuestions(21))
                .isInstanceOf(InvalidInterviewConfigurationException.class)
                .hasMessage("Total questions must be between 5 and 20.");
    }
}
