package com.aiinterviewcoach.service.interview;

import static org.assertj.core.api.Assertions.assertThat;

import com.aiinterviewcoach.dto.response.InterviewOptionResponse;
import com.aiinterviewcoach.dto.response.InterviewOptionsResponse;
import com.aiinterviewcoach.enums.FieldCategory;
import java.util.List;
import org.junit.jupiter.api.Test;

class InterviewOptionsServiceTest {
    private final InterviewOptionsService service = new InterviewOptionsService();

    @Test
    void returnsCompleteItConfigurationWithReadableLabels() {
        InterviewOptionsResponse response = service.getOptions();
        List<InterviewOptionResponse> domains = response.domains().get(FieldCategory.IT);
        List<InterviewOptionResponse> modes = response.modes().get(FieldCategory.IT);

        assertThat(response.domainLabels().get(FieldCategory.IT)).isEqualTo("Technical Domain");
        assertThat(domains).hasSize(24)
                .contains(new InterviewOptionResponse("SPRING_BOOT", "Spring Boot"))
                .contains(new InterviewOptionResponse("NEXT_JS", "Next.js"))
                .contains(new InterviewOptionResponse("CUSTOM", "Custom Domain"))
                .noneMatch(option -> option.value().equals("CUSTOMER_SUPPORT"));
        assertThat(modes).extracting(InterviewOptionResponse::value)
                .containsExactly("TECHNICAL", "CODING", "CONCEPTUAL", "SCENARIO_BASED",
                        "DEBUGGING", "SYSTEM_DESIGN", "MIXED");
    }

    @Test
    void returnsCompleteNonItConfigurationAndSharedLimits() {
        InterviewOptionsResponse response = service.getOptions();
        List<InterviewOptionResponse> domains = response.domains().get(FieldCategory.NON_IT);
        List<InterviewOptionResponse> modes = response.modes().get(FieldCategory.NON_IT);

        assertThat(response.fieldCategories()).containsExactly(
                new InterviewOptionResponse("IT", "IT Field"),
                new InterviewOptionResponse("NON_IT", "Non-IT Field"));
        assertThat(response.domainLabels().get(FieldCategory.NON_IT)).isEqualTo("Professional Domain");
        assertThat(domains).hasSize(20)
                .contains(new InterviewOptionResponse("HUMAN_RESOURCES", "Human Resources"))
                .contains(new InterviewOptionResponse("CUSTOMER_SUCCESS", "Customer Success"))
                .contains(new InterviewOptionResponse("CUSTOM", "Custom Domain"))
                .noneMatch(option -> option.value().equals("JAVA"));
        assertThat(modes).extracting(InterviewOptionResponse::value)
                .containsExactly("HR", "BEHAVIOURAL", "SITUATIONAL", "ROLE_SPECIFIC",
                        "COMMUNICATION", "CUSTOMER_HANDLING", "LEADERSHIP", "MIXED");
        assertThat(response.difficulties()).extracting(InterviewOptionResponse::value)
                .containsExactly("EASY", "MEDIUM", "HARD");
        assertThat(response.experienceLevels()).extracting(InterviewOptionResponse::value)
                .containsExactly("BEGINNER", "INTERMEDIATE", "EXPERIENCED");
        assertThat(response.minimumQuestions()).isEqualTo(5);
        assertThat(response.maximumQuestions()).isEqualTo(20);
        assertThat(response.defaultQuestions()).isEqualTo(10);
    }
}
