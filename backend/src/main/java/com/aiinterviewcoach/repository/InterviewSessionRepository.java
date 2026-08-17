package com.aiinterviewcoach.repository;

import com.aiinterviewcoach.entity.InterviewSession;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterviewSessionRepository extends JpaRepository<InterviewSession, UUID> {
    Optional<InterviewSession> findByIdAndUserId(UUID id, Long userId);
    List<InterviewSession> findAllByUserIdOrderByCreatedAtDesc(Long userId);
    boolean existsByIdAndUserId(UUID id, Long userId);
}

