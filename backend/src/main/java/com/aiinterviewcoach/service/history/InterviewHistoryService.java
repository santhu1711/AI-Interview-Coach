package com.aiinterviewcoach.service.history;

import com.aiinterviewcoach.dto.response.InterviewHistoryResponse;
import com.aiinterviewcoach.dto.response.InterviewSummaryResponse;
import com.aiinterviewcoach.entity.InterviewSession;
import com.aiinterviewcoach.enums.Difficulty;
import com.aiinterviewcoach.enums.FieldCategory;
import com.aiinterviewcoach.enums.HistorySortOrder;
import com.aiinterviewcoach.enums.InterviewDomain;
import com.aiinterviewcoach.enums.InterviewMode;
import com.aiinterviewcoach.enums.InterviewStatus;
import com.aiinterviewcoach.repository.InterviewSessionRepository;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Expression;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InterviewHistoryService {
    private final InterviewSessionRepository sessionRepository;

    public InterviewHistoryService(InterviewSessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Transactional(readOnly = true)
    public InterviewHistoryResponse search(
            Long userId,
            String search,
            FieldCategory fieldCategory,
            InterviewDomain domain,
            InterviewMode mode,
            Difficulty difficulty,
            InterviewStatus status,
            HistorySortOrder sortOrder,
            int page,
            int size) {
        Specification<InterviewSession> specification = (root, query, criteria) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteria.equal(root.get("user").get("id"), userId));
            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.trim().toLowerCase() + "%";
                predicates.add(criteria.or(
                        criteria.like(criteria.lower(root.get("topic")), pattern),
                        criteria.like(criteria.lower(root.get("targetRole")), pattern)));
            }
            if (fieldCategory != null) predicates.add(criteria.equal(root.get("fieldCategory"), fieldCategory));
            if (domain != null) predicates.add(criteria.equal(root.get("interviewDomain"), domain));
            if (mode != null) predicates.add(criteria.equal(root.get("interviewMode"), mode));
            if (difficulty != null) predicates.add(criteria.equal(root.get("difficulty"), difficulty));
            if (status != null) predicates.add(criteria.equal(root.get("status"), status));
            if (isScoreSort(sortOrder) && query.getResultType() != Long.class) {
                Expression<Integer> nullRank = criteria.<Integer>selectCase()
                        .when(criteria.isNull(root.get("overallScore")), 1)
                        .otherwise(0);
                query.orderBy(
                        criteria.asc(nullRank),
                        sortOrder == HistorySortOrder.HIGHEST_SCORE
                                ? criteria.desc(root.get("overallScore"))
                                : criteria.asc(root.get("overallScore")),
                        criteria.desc(root.get("createdAt")));
            }
            return criteria.and(predicates.toArray(Predicate[]::new));
        };
        Page<InterviewSession> result = sessionRepository.findAll(
                specification, PageRequest.of(page, size, sort(sortOrder)));
        return new InterviewHistoryResponse(
                result.getContent().stream().map(InterviewHistoryService::summary).toList(),
                result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages(),
                result.isFirst(), result.isLast());
    }

    private static Sort sort(HistorySortOrder order) {
        return switch (order) {
            case OLDEST -> Sort.by(Sort.Order.asc("createdAt"));
            case HIGHEST_SCORE, LOWEST_SCORE -> Sort.unsorted();
            case NEWEST -> Sort.by(Sort.Order.desc("createdAt"));
        };
    }

    private static boolean isScoreSort(HistorySortOrder order) {
        return order == HistorySortOrder.HIGHEST_SCORE || order == HistorySortOrder.LOWEST_SCORE;
    }

    private static InterviewSummaryResponse summary(InterviewSession session) {
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
}
