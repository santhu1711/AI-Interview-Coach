package com.aiinterviewcoach.service.ai;

import static org.assertj.core.api.Assertions.assertThat;

import com.aiinterviewcoach.enums.AnswerEvaluation;
import com.aiinterviewcoach.enums.Difficulty;
import com.aiinterviewcoach.enums.ExperienceLevel;
import com.aiinterviewcoach.enums.FieldCategory;
import com.aiinterviewcoach.enums.InterviewDomain;
import com.aiinterviewcoach.enums.InterviewMode;
import com.aiinterviewcoach.enums.MessageRole;
import java.util.List;
import org.junit.jupiter.api.Test;

class InterviewPromptFactoryTest {
    private final InterviewPromptFactory factory = new InterviewPromptFactory();

    @Test
    void includesCompleteItContextTranscriptAndHardDifficultyRules() {
        InterviewAiContext context = new InterviewAiContext(
                FieldCategory.IT, InterviewDomain.SYSTEM_DESIGN, null, "Caching",
                InterviewMode.SYSTEM_DESIGN, "Staff Engineer", ExperienceLevel.EXPERIENCED,
                Difficulty.HARD, 12, 4,
                List.of(new AiTranscriptEntry(MessageRole.USER, "I would use Redis.")),
                List.of("Availability", "Caching"), AnswerEvaluation.PARTIAL, 1);

        String prompt = factory.create(context);

        assertThat(prompt)
                .contains("Field category: IT", "Interview domain: SYSTEM_DESIGN", "Target role: Staff Engineer")
                .contains("Architecture, trade-offs, scalability", "USER: I would use Redis.")
                .contains("Previously covered categories: Availability, Caching")
                .contains("Previous answer evaluation: PARTIAL", "Follow-up count: 1")
                .contains("Ask exactly one question", "Never give hints");
    }

    @Test
    void givesNonItProviderAnExplicitProgrammingGuardrail() {
        InterviewAiContext context = new InterviewAiContext(
                FieldCategory.NON_IT, InterviewDomain.CUSTOMER_SUCCESS, null, "Retention",
                InterviewMode.ROLE_SPECIFIC, "Customer Success Manager", ExperienceLevel.INTERMEDIATE,
                Difficulty.MEDIUM, 10, 0, List.of(), List.of(), null, 0);

        assertThat(factory.create(context))
                .contains("This is a Non-IT interview")
                .contains("Do not ask programming questions unless the CUSTOM domain explicitly requires them")
                .contains("Practical application, comparisons");
    }
}
