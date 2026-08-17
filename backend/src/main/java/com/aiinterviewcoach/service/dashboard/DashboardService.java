package com.aiinterviewcoach.service.dashboard;

import com.aiinterviewcoach.dto.response.CategoryPerformanceResponse;
import com.aiinterviewcoach.dto.response.DashboardPerformanceResponse;
import com.aiinterviewcoach.dto.response.DashboardSummaryResponse;
import com.aiinterviewcoach.dto.response.DomainPerformanceResponse;
import com.aiinterviewcoach.dto.response.InterviewSummaryResponse;
import com.aiinterviewcoach.dto.response.ScoreTrendPointResponse;
import com.aiinterviewcoach.entity.InterviewReport;
import com.aiinterviewcoach.entity.InterviewSession;
import com.aiinterviewcoach.enums.FieldCategory;
import com.aiinterviewcoach.enums.InterviewDomain;
import com.aiinterviewcoach.enums.InterviewStatus;
import com.aiinterviewcoach.enums.Recommendation;
import com.aiinterviewcoach.repository.InterviewReportRepository;
import com.aiinterviewcoach.repository.InterviewSessionRepository;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardService {
    private final InterviewSessionRepository sessionRepository;
    private final InterviewReportRepository reportRepository;

    public DashboardService(
            InterviewSessionRepository sessionRepository, InterviewReportRepository reportRepository) {
        this.sessionRepository = sessionRepository;
        this.reportRepository = reportRepository;
    }

    @Transactional(readOnly = true)
    public DashboardSummaryResponse summary(Long userId) {
        List<InterviewSession> sessions = sessionRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
        List<InterviewReport> reports = reportRepository.findAllByInterviewSessionUserId(userId);
        Map<String, ScoreAccumulator> domains = domainScores(reports);
        return new DashboardSummaryResponse(
                sessions.size(),
                sessions.stream().filter(DashboardService::isCompleted).count(),
                sessions.stream().filter(DashboardService::isActive).count(),
                sessions.stream().filter(session -> session.getFieldCategory() == FieldCategory.IT).count(),
                sessions.stream().filter(session -> session.getFieldCategory() == FieldCategory.NON_IT).count(),
                average(reports),
                average(reports.stream().filter(report ->
                        report.getInterviewSession().getFieldCategory() == FieldCategory.IT).toList()),
                average(reports.stream().filter(report ->
                        report.getInterviewSession().getFieldCategory() == FieldCategory.NON_IT).toList()),
                reports.stream().map(InterviewReport::getOverallScore).max(Integer::compareTo).orElse(null),
                reports.isEmpty() ? 0.0 : round(reports.stream()
                        .filter(report -> report.getRecommendation() == Recommendation.PASS)
                        .count() * 100.0 / reports.size()),
                extremeDomain(domains, true),
                extremeDomain(domains, false),
                sessions.stream().limit(5).map(DashboardService::summaryResponse).toList());
    }

    @Transactional(readOnly = true)
    public DashboardPerformanceResponse performance(Long userId) {
        List<InterviewReport> reports = reportRepository.findAllByInterviewSessionUserId(userId);
        List<ScoreTrendPointResponse> trend = reports.stream()
                .sorted(Comparator.comparing(InterviewReport::getGeneratedAt))
                .map(report -> new ScoreTrendPointResponse(
                        report.getInterviewSession().getId(), report.getGeneratedAt(),
                        report.getInterviewSession().getFieldCategory(), domain(report.getInterviewSession()),
                        report.getOverallScore()))
                .toList();
        List<DomainPerformanceResponse> domainPerformance = domainScores(reports).entrySet().stream()
                .map(entry -> new DomainPerformanceResponse(
                        entry.getKey(), entry.getValue().count, entry.getValue().average()))
                .sorted(Comparator.comparing(DomainPerformanceResponse::domain))
                .toList();
        Map<FieldCategory, List<InterviewReport>> byCategory = new EnumMap<>(FieldCategory.class);
        for (FieldCategory category : FieldCategory.values()) {
            byCategory.put(category, reports.stream().filter(report ->
                    report.getInterviewSession().getFieldCategory() == category).toList());
        }
        List<CategoryPerformanceResponse> comparison = byCategory.entrySet().stream()
                .map(entry -> new CategoryPerformanceResponse(
                        entry.getKey(), entry.getValue().size(), average(entry.getValue())))
                .toList();
        return new DashboardPerformanceResponse(trend, domainPerformance, comparison);
    }

    private static Map<String, ScoreAccumulator> domainScores(List<InterviewReport> reports) {
        Map<String, ScoreAccumulator> result = new LinkedHashMap<>();
        reports.forEach(report -> result.computeIfAbsent(
                        domain(report.getInterviewSession()), ignored -> new ScoreAccumulator())
                .add(report.getOverallScore()));
        return result;
    }

    private static String extremeDomain(Map<String, ScoreAccumulator> domains, boolean strongest) {
        Comparator<Map.Entry<String, ScoreAccumulator>> comparator = Comparator
                .comparingDouble((Map.Entry<String, ScoreAccumulator> entry) -> entry.getValue().average())
                .thenComparing(Map.Entry::getKey);
        return domains.entrySet().stream()
                .min(strongest ? comparator.reversed() : comparator)
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    private static Double average(List<InterviewReport> reports) {
        return reports.isEmpty()
                ? null
                : round(reports.stream().mapToInt(InterviewReport::getOverallScore).average().orElseThrow());
    }

    private static boolean isCompleted(InterviewSession session) {
        return session.getStatus() == InterviewStatus.COMPLETED
                || session.getStatus() == InterviewStatus.REPORT_GENERATED;
    }

    private static boolean isActive(InterviewSession session) {
        return session.getStatus() == InterviewStatus.CREATED
                || session.getStatus() == InterviewStatus.IN_PROGRESS;
    }

    private static String domain(InterviewSession session) {
        return session.getInterviewDomain() == InterviewDomain.CUSTOM
                ? session.getCustomDomain()
                : session.getInterviewDomain().name();
    }

    private static InterviewSummaryResponse summaryResponse(InterviewSession session) {
        return new InterviewSummaryResponse(
                session.getId(), session.getFieldCategory(), session.getInterviewDomain(), session.getCustomDomain(),
                session.getTopic(), session.getDifficulty(), session.getInterviewMode(), session.getTargetRole(),
                session.getExperienceLevel(), session.getTotalQuestions(), session.getCurrentQuestionNumber(),
                session.getFollowUpCount(), progress(session), session.getStatus(), session.getOverallScore(),
                session.getStartedAt(), session.getCompletedAt(), session.getCreatedAt(), session.getUpdatedAt());
    }

    private static int progress(InterviewSession session) {
        return session.getTotalQuestions() == 0
                ? 0
                : Math.min(100, session.getCurrentQuestionNumber() * 100 / session.getTotalQuestions());
    }

    private static double round(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    private static final class ScoreAccumulator {
        private long count;
        private long total;

        private void add(int score) {
            count++;
            total += score;
        }

        private double average() {
            return round((double) total / count);
        }
    }
}
