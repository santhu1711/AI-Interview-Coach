package com.aiinterviewcoach.repository;

import com.aiinterviewcoach.entity.InterviewSession;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

public interface InterviewSessionRepository
        extends JpaRepository<InterviewSession, UUID>, JpaSpecificationExecutor<InterviewSession> {
    Optional<InterviewSession> findByIdAndUserId(UUID id, Long userId);
    List<InterviewSession> findAllByUserIdOrderByCreatedAtDesc(Long userId);
    boolean existsByIdAndUserId(UUID id, Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select session from InterviewSession session where session.id = :id and session.user.id = :userId")
    Optional<InterviewSession> findOwnedForUpdate(@Param("id") UUID id, @Param("userId") Long userId);
}
