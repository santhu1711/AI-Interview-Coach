package com.aiinterviewcoach.service.report;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class InterviewReportServiceTest {
    @Test
    void interpretsEveryDefinedScoreBandAtItsBoundaries() {
        assertThat(InterviewReportService.interpretation(100)).isEqualTo("Excellent");
        assertThat(InterviewReportService.interpretation(85)).isEqualTo("Excellent");
        assertThat(InterviewReportService.interpretation(84)).isEqualTo("Good");
        assertThat(InterviewReportService.interpretation(70)).isEqualTo("Good");
        assertThat(InterviewReportService.interpretation(69)).isEqualTo("Adequate");
        assertThat(InterviewReportService.interpretation(55)).isEqualTo("Adequate");
        assertThat(InterviewReportService.interpretation(54)).isEqualTo("Weak");
        assertThat(InterviewReportService.interpretation(0)).isEqualTo("Weak");
    }
}
