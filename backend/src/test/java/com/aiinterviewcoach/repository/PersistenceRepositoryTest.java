package com.aiinterviewcoach.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.aiinterviewcoach.entity.InterviewMessage;
import com.aiinterviewcoach.entity.InterviewReport;
import com.aiinterviewcoach.entity.InterviewSession;
import com.aiinterviewcoach.entity.User;
import com.aiinterviewcoach.enums.Difficulty;
import com.aiinterviewcoach.enums.ExperienceLevel;
import com.aiinterviewcoach.enums.FieldCategory;
import com.aiinterviewcoach.enums.InterviewDomain;
import com.aiinterviewcoach.enums.InterviewMode;
import com.aiinterviewcoach.enums.MessageRole;
import com.aiinterviewcoach.enums.Recommendation;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class PersistenceRepositoryTest {
    @Autowired private UserRepository userRepository;
    @Autowired private InterviewSessionRepository sessionRepository;
    @Autowired private InterviewMessageRepository messageRepository;
    @Autowired private InterviewReportRepository reportRepository;

    @Test
    void persistsSessionAndEnforcesOwnershipQueries() {
        User owner = userRepository.save(new User("Owner", "owner@example.com", "hash"));
        User anotherUser = userRepository.save(new User("Other", "other@example.com", "hash"));
        InterviewSession session = sessionRepository.saveAndFlush(javaSession(owner));

        assertThat(session.getId()).isNotNull();
        assertThat(session.getVersion()).isZero();
        assertThat(sessionRepository.findByIdAndUserId(session.getId(), owner.getId())).isPresent();
        assertThat(sessionRepository.findByIdAndUserId(session.getId(), anotherUser.getId())).isEmpty();
    }

    @Test
    void returnsTranscriptInSequenceOrder() {
        User owner = userRepository.save(new User("Owner", "sequence@example.com", "hash"));
        InterviewSession session = sessionRepository.save(javaSession(owner));
        messageRepository.save(message(session, 2, MessageRole.USER, "Second"));
        messageRepository.save(message(session, 1, MessageRole.ASSISTANT, "First"));
        messageRepository.flush();

        List<InterviewMessage> transcript =
                messageRepository.findAllByInterviewSessionIdOrderBySequenceNumberAsc(session.getId());

        assertThat(transcript).extracting(InterviewMessage::getSequenceNumber).containsExactly(1, 2);
        assertThat(messageRepository.findMaximumSequenceNumber(session.getId())).isEqualTo(2);
    }

    @Test
    void allowsOnlyOneReportPerInterview() {
        User owner = userRepository.save(new User("Owner", "report@example.com", "hash"));
        InterviewSession session = sessionRepository.save(javaSession(owner));
        reportRepository.saveAndFlush(report(session, "First report"));

        assertThatThrownBy(() -> reportRepository.saveAndFlush(report(session, "Duplicate report")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    private static InterviewSession javaSession(User owner) {
        InterviewSession session = new InterviewSession();
        session.setUser(owner);
        session.setFieldCategory(FieldCategory.IT);
        session.setInterviewDomain(InterviewDomain.JAVA);
        session.setTopic("Core Java");
        session.setDifficulty(Difficulty.MEDIUM);
        session.setInterviewMode(InterviewMode.TECHNICAL);
        session.setTargetRole("Java Developer");
        session.setExperienceLevel(ExperienceLevel.BEGINNER);
        session.setTotalQuestions(10);
        return session;
    }

    private static InterviewMessage message(
            InterviewSession session, int sequence, MessageRole role, String content) {
        InterviewMessage message = new InterviewMessage();
        message.setInterviewSession(session);
        message.setSequenceNumber(sequence);
        message.setRole(role);
        message.setContent(content);
        return message;
    }

    private static InterviewReport report(InterviewSession session, String verdict) {
        InterviewReport report = new InterviewReport();
        report.setInterviewSession(session);
        report.setOverallScore(75);
        report.setStrengths("[]");
        report.setWeaknesses("[]");
        report.setRevisionAreas("[]");
        report.setVerdict(verdict);
        report.setRecommendation(Recommendation.PASS);
        report.setQuestionFeedbackJson("[]");
        return report;
    }
}

