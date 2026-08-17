package com.aiinterviewcoach.entity;

import com.aiinterviewcoach.enums.Recommendation;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "interview_reports")
public class InterviewReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "interview_session_id", nullable = false, unique = true)
    private InterviewSession interviewSession;

    @Column(name = "overall_score", nullable = false)
    private int overallScore;
    @Column(name = "technical_accuracy_score")
    private Integer technicalAccuracyScore;
    @Column(name = "conceptual_understanding_score")
    private Integer conceptualUnderstandingScore;
    @Column(name = "problem_solving_score")
    private Integer problemSolvingScore;
    @Column(name = "communication_score")
    private Integer communicationScore;
    @Column(name = "confidence_score")
    private Integer confidenceScore;
    @Column(name = "situational_judgement_score")
    private Integer situationalJudgementScore;
    @Column(name = "role_understanding_score")
    private Integer roleUnderstandingScore;

    @Column(nullable = false, columnDefinition = "text")
    private String strengths;
    @Column(nullable = false, columnDefinition = "text")
    private String weaknesses;
    @Column(name = "revision_areas", nullable = false, columnDefinition = "text")
    private String revisionAreas;
    @Column(nullable = false, columnDefinition = "text")
    private String verdict;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Recommendation recommendation;

    @Column(name = "question_feedback_json", nullable = false, columnDefinition = "longtext")
    private String questionFeedbackJson;

    @CreationTimestamp
    @Column(name = "generated_at", nullable = false, updatable = false)
    private Instant generatedAt;

    public InterviewReport() {}

    public Long getId() { return id; }
    public InterviewSession getInterviewSession() { return interviewSession; }
    public void setInterviewSession(InterviewSession interviewSession) { this.interviewSession = interviewSession; }
    public int getOverallScore() { return overallScore; }
    public void setOverallScore(int overallScore) { this.overallScore = overallScore; }
    public Integer getTechnicalAccuracyScore() { return technicalAccuracyScore; }
    public void setTechnicalAccuracyScore(Integer value) { this.technicalAccuracyScore = value; }
    public Integer getConceptualUnderstandingScore() { return conceptualUnderstandingScore; }
    public void setConceptualUnderstandingScore(Integer value) { this.conceptualUnderstandingScore = value; }
    public Integer getProblemSolvingScore() { return problemSolvingScore; }
    public void setProblemSolvingScore(Integer value) { this.problemSolvingScore = value; }
    public Integer getCommunicationScore() { return communicationScore; }
    public void setCommunicationScore(Integer value) { this.communicationScore = value; }
    public Integer getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(Integer value) { this.confidenceScore = value; }
    public Integer getSituationalJudgementScore() { return situationalJudgementScore; }
    public void setSituationalJudgementScore(Integer value) { this.situationalJudgementScore = value; }
    public Integer getRoleUnderstandingScore() { return roleUnderstandingScore; }
    public void setRoleUnderstandingScore(Integer value) { this.roleUnderstandingScore = value; }
    public String getStrengths() { return strengths; }
    public void setStrengths(String strengths) { this.strengths = strengths; }
    public String getWeaknesses() { return weaknesses; }
    public void setWeaknesses(String weaknesses) { this.weaknesses = weaknesses; }
    public String getRevisionAreas() { return revisionAreas; }
    public void setRevisionAreas(String revisionAreas) { this.revisionAreas = revisionAreas; }
    public String getVerdict() { return verdict; }
    public void setVerdict(String verdict) { this.verdict = verdict; }
    public Recommendation getRecommendation() { return recommendation; }
    public void setRecommendation(Recommendation recommendation) { this.recommendation = recommendation; }
    public String getQuestionFeedbackJson() { return questionFeedbackJson; }
    public void setQuestionFeedbackJson(String value) { this.questionFeedbackJson = value; }
    public Instant getGeneratedAt() { return generatedAt; }
}
