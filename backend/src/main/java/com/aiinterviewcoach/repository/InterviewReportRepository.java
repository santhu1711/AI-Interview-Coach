package com.aiinterviewcoach.repository;

import com.aiinterviewcoach.entity.InterviewReport;
import java.util.Optional;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterviewReportRepository extends JpaRepository<InterviewReport, Long> {
    Optional<InterviewReport> findByInterviewSessionId(UUID sessionId);
    Optional<InterviewReport> findByInterviewSessionIdAndInterviewSessionUserId(UUID sessionId, Long userId);
    boolean existsByInterviewSessionId(UUID sessionId);
    List<InterviewReport> findAllByInterviewSessionUserId(Long userId);
}
