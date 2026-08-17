package com.aiinterviewcoach.entity;

import com.aiinterviewcoach.enums.Difficulty;
import com.aiinterviewcoach.enums.ExperienceLevel;
import com.aiinterviewcoach.enums.FieldCategory;
import com.aiinterviewcoach.enums.InterviewDomain;
import com.aiinterviewcoach.enums.InterviewMode;
import com.aiinterviewcoach.enums.InterviewStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "interview_sessions")
public class InterviewSession {
    @Id
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(columnDefinition = "char(36)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "field_category", nullable = false, length = 20)
    private FieldCategory fieldCategory;

    @Enumerated(EnumType.STRING)
    @Column(name = "interview_domain", nullable = false, length = 50)
    private InterviewDomain interviewDomain;

    @Column(name = "custom_domain", length = 120)
    private String customDomain;

    @Column(nullable = false, length = 200)
    private String topic;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Difficulty difficulty;

    @Enumerated(EnumType.STRING)
    @Column(name = "interview_mode", nullable = false, length = 40)
    private InterviewMode interviewMode;

    @Column(name = "target_role", nullable = false, length = 150)
    private String targetRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "experience_level", nullable = false, length = 20)
    private ExperienceLevel experienceLevel;

    @Column(name = "total_questions", nullable = false)
    private int totalQuestions;

    @Column(name = "current_question_number", nullable = false)
    private int currentQuestionNumber;

    @Column(name = "follow_up_count", nullable = false)
    private int followUpCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private InterviewStatus status = InterviewStatus.CREATED;

    @Column(name = "overall_score")
    private Integer overallScore;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(nullable = false)
    private long version;

    public InterviewSession() {}

    public UUID getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public FieldCategory getFieldCategory() { return fieldCategory; }
    public void setFieldCategory(FieldCategory fieldCategory) { this.fieldCategory = fieldCategory; }
    public InterviewDomain getInterviewDomain() { return interviewDomain; }
    public void setInterviewDomain(InterviewDomain interviewDomain) { this.interviewDomain = interviewDomain; }
    public String getCustomDomain() { return customDomain; }
    public void setCustomDomain(String customDomain) { this.customDomain = customDomain; }
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    public Difficulty getDifficulty() { return difficulty; }
    public void setDifficulty(Difficulty difficulty) { this.difficulty = difficulty; }
    public InterviewMode getInterviewMode() { return interviewMode; }
    public void setInterviewMode(InterviewMode interviewMode) { this.interviewMode = interviewMode; }
    public String getTargetRole() { return targetRole; }
    public void setTargetRole(String targetRole) { this.targetRole = targetRole; }
    public ExperienceLevel getExperienceLevel() { return experienceLevel; }
    public void setExperienceLevel(ExperienceLevel experienceLevel) { this.experienceLevel = experienceLevel; }
    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }
    public int getCurrentQuestionNumber() { return currentQuestionNumber; }
    public void setCurrentQuestionNumber(int currentQuestionNumber) { this.currentQuestionNumber = currentQuestionNumber; }
    public int getFollowUpCount() { return followUpCount; }
    public void setFollowUpCount(int followUpCount) { this.followUpCount = followUpCount; }
    public InterviewStatus getStatus() { return status; }
    public void setStatus(InterviewStatus status) { this.status = status; }
    public Integer getOverallScore() { return overallScore; }
    public void setOverallScore(Integer overallScore) { this.overallScore = overallScore; }
    public Instant getStartedAt() { return startedAt; }
    public void setStartedAt(Instant startedAt) { this.startedAt = startedAt; }
    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public long getVersion() { return version; }
}
