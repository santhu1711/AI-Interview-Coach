package com.aiinterviewcoach.repository;

import com.aiinterviewcoach.entity.InterviewMessage;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InterviewMessageRepository extends JpaRepository<InterviewMessage, Long> {
    List<InterviewMessage> findAllByInterviewSessionIdOrderBySequenceNumberAsc(UUID sessionId);

    @Query("select coalesce(max(message.sequenceNumber), 0) from InterviewMessage message "
            + "where message.interviewSession.id = :sessionId")
    int findMaximumSequenceNumber(@Param("sessionId") UUID sessionId);
}

