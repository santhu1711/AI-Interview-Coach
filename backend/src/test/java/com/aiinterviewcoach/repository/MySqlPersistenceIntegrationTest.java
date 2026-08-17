package com.aiinterviewcoach.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.aiinterviewcoach.entity.InterviewMessage;
import com.aiinterviewcoach.entity.InterviewReport;
import com.aiinterviewcoach.entity.InterviewSession;
import com.aiinterviewcoach.entity.User;
import com.aiinterviewcoach.enums.AnswerEvaluation;
import com.aiinterviewcoach.enums.Difficulty;
import com.aiinterviewcoach.enums.ExperienceLevel;
import com.aiinterviewcoach.enums.FieldCategory;
import com.aiinterviewcoach.enums.InterviewDomain;
import com.aiinterviewcoach.enums.InterviewMode;
import com.aiinterviewcoach.enums.InterviewStatus;
import com.aiinterviewcoach.enums.MessageRole;
import com.aiinterviewcoach.enums.Recommendation;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("dev")
@EnabledIfEnvironmentVariable(named = "MYSQL_INTEGRATION_TESTS", matches = "true")
class MySqlPersistenceIntegrationTest {
    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private EntityManager entityManager;
    @Autowired private UserRepository userRepository;
    @Autowired private InterviewSessionRepository sessionRepository;
    @Autowired private InterviewMessageRepository messageRepository;
    @Autowired private InterviewReportRepository reportRepository;

    @Test
    void flywaySchemaContainsRequiredDatabaseObjects() {
        Integer successfulMigrations = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM flyway_schema_history WHERE success = 1 AND version BETWEEN '1' AND '5'",
                Integer.class);
        String currentVersion = jdbcTemplate.queryForObject(
                "SELECT MAX(version) FROM flyway_schema_history WHERE success = 1", String.class);
        Integer tables = countMetadata(
                "SELECT COUNT(*) FROM information_schema.tables "
                        + "WHERE table_schema = DATABASE() AND table_name IN "
                        + "('users', 'interview_sessions', 'interview_messages', 'interview_reports')");
        Integer foreignKeys = countMetadata(
                "SELECT COUNT(*) FROM information_schema.table_constraints "
                        + "WHERE constraint_schema = DATABASE() AND constraint_type = 'FOREIGN KEY' "
                        + "AND constraint_name IN "
                        + "('fk_interview_sessions_user', 'fk_interview_messages_session', 'fk_interview_reports_session')");
        Integer uniqueIndexes = countMetadata(
                "SELECT COUNT(DISTINCT index_name) FROM information_schema.statistics "
                        + "WHERE table_schema = DATABASE() AND non_unique = 0 AND index_name IN "
                        + "('uk_users_email', 'uk_interview_messages_session_sequence', 'uk_interview_reports_session')");

        assertThat(successfulMigrations).isEqualTo(5);
        assertThat(currentVersion).isEqualTo("5");
        assertThat(tables).isEqualTo(4);
        assertThat(foreignKeys).isEqualTo(3);
        assertThat(uniqueIndexes).isEqualTo(3);
        assertColumnNullable("interview_sessions", "custom_domain", true);
        assertColumnNullable("interview_sessions", "topic", false);
        assertColumnNullable("interview_reports", "technical_accuracy_score", true);
        assertColumnNullable("interview_reports", "overall_score", false);
    }

    @Test
    @Transactional
    void performsCrudAndEnumRoundTripsOnMySql() {
        String suffix = UUID.randomUUID().toString();
        User user = userRepository.saveAndFlush(
                new User("MySQL Verification", "mysql-verification-" + suffix + "@example.com", "test-hash"));

        InterviewSession session = nonItSession(user);
        session = sessionRepository.saveAndFlush(session);
        InterviewMessage question = message(session, 1, MessageRole.ASSISTANT, "How would you calm an upset customer?");
        question.setQuestionNumber(1);
        question.setQuestionCategory("Customer handling");
        InterviewMessage answer = message(session, 2, MessageRole.USER, "I would listen and confirm the issue.");
        answer.setQuestionNumber(1);
        answer.setAnswerEvaluation(AnswerEvaluation.STRONG);
        messageRepository.saveAllAndFlush(List.of(question, answer));
        InterviewReport report = report(session);
        reportRepository.saveAndFlush(report);

        UUID sessionId = session.getId();
        Long userId = user.getId();
        entityManager.clear();

        InterviewSession persisted = sessionRepository.findByIdAndUserId(sessionId, userId).orElseThrow();
        assertThat(persisted.getFieldCategory()).isEqualTo(FieldCategory.NON_IT);
        assertThat(persisted.getInterviewDomain()).isEqualTo(InterviewDomain.CUSTOMER_SUPPORT);
        assertThat(persisted.getInterviewMode()).isEqualTo(InterviewMode.SITUATIONAL);
        assertThat(persisted.getDifficulty()).isEqualTo(Difficulty.HARD);
        assertThat(persisted.getExperienceLevel()).isEqualTo(ExperienceLevel.INTERMEDIATE);
        assertThat(messageRepository.findAllByInterviewSessionIdOrderBySequenceNumberAsc(sessionId))
                .extracting(InterviewMessage::getRole)
                .containsExactly(MessageRole.ASSISTANT, MessageRole.USER);
        assertThat(reportRepository.findByInterviewSessionIdAndInterviewSessionUserId(sessionId, userId))
                .get()
                .extracting(InterviewReport::getRecommendation)
                .isEqualTo(Recommendation.PASS);

        persisted.setStatus(InterviewStatus.COMPLETED);
        persisted.setOverallScore(82);
        sessionRepository.saveAndFlush(persisted);
        User persistedUser = userRepository.findById(userId).orElseThrow();
        persistedUser.setFullName("Updated MySQL Verification");
        userRepository.saveAndFlush(persistedUser);
        entityManager.clear();

        assertThat(sessionRepository.findById(sessionId).orElseThrow().getOverallScore()).isEqualTo(82);
        assertThat(userRepository.findById(userId).orElseThrow().getFullName())
                .isEqualTo("Updated MySQL Verification");

        userRepository.deleteById(userId);
        userRepository.flush();
        entityManager.clear();

        assertThat(sessionRepository.existsById(sessionId)).isFalse();
        assertThat(messageRepository.findAllByInterviewSessionIdOrderBySequenceNumberAsc(sessionId)).isEmpty();
        assertThat(reportRepository.existsByInterviewSessionId(sessionId)).isFalse();
    }

    private Integer countMetadata(String sql) {
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    private void assertColumnNullable(String table, String column, boolean expectedNullable) {
        String nullable = jdbcTemplate.queryForObject(
                "SELECT is_nullable FROM information_schema.columns "
                        + "WHERE table_schema = DATABASE() AND table_name = ? AND column_name = ?",
                String.class,
                table,
                column);
        assertThat(nullable).isEqualTo(expectedNullable ? "YES" : "NO");
    }

    private static InterviewSession nonItSession(User user) {
        InterviewSession session = new InterviewSession();
        session.setUser(user);
        session.setFieldCategory(FieldCategory.NON_IT);
        session.setInterviewDomain(InterviewDomain.CUSTOMER_SUPPORT);
        session.setTopic("Escalation handling");
        session.setDifficulty(Difficulty.HARD);
        session.setInterviewMode(InterviewMode.SITUATIONAL);
        session.setTargetRole("Customer Support Lead");
        session.setExperienceLevel(ExperienceLevel.INTERMEDIATE);
        session.setTotalQuestions(8);
        session.setStatus(InterviewStatus.IN_PROGRESS);
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

    private static InterviewReport report(InterviewSession session) {
        InterviewReport report = new InterviewReport();
        report.setInterviewSession(session);
        report.setOverallScore(82);
        report.setProblemSolvingScore(84);
        report.setCommunicationScore(81);
        report.setConfidenceScore(78);
        report.setSituationalJudgementScore(85);
        report.setRoleUnderstandingScore(80);
        report.setStrengths("[\"Clear de-escalation approach\"]");
        report.setWeaknesses("[\"Could define escalation thresholds\"]");
        report.setRevisionAreas("[\"Escalation policy\"]");
        report.setVerdict("Strong customer support response.");
        report.setRecommendation(Recommendation.PASS);
        report.setQuestionFeedbackJson("[]");
        return report;
    }
}

