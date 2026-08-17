package com.aiinterviewcoach.entity;

import com.aiinterviewcoach.enums.AnswerEvaluation;
import com.aiinterviewcoach.enums.MessageRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "interview_messages")
public class InterviewMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "interview_session_id", nullable = false)
    private InterviewSession interviewSession;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MessageRole role;

    @Column(nullable = false, columnDefinition = "text")
    private String content;

    @Column(name = "sequence_number", nullable = false)
    private int sequenceNumber;

    @Column(name = "question_number")
    private Integer questionNumber;

    @Column(name = "question_category", length = 120)
    private String questionCategory;

    @Enumerated(EnumType.STRING)
    @Column(name = "answer_evaluation", nullable = false, length = 30)
    private AnswerEvaluation answerEvaluation = AnswerEvaluation.NOT_APPLICABLE;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public InterviewMessage() {}

    public Long getId() { return id; }
    public InterviewSession getInterviewSession() { return interviewSession; }
    public void setInterviewSession(InterviewSession interviewSession) { this.interviewSession = interviewSession; }
    public MessageRole getRole() { return role; }
    public void setRole(MessageRole role) { this.role = role; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public int getSequenceNumber() { return sequenceNumber; }
    public void setSequenceNumber(int sequenceNumber) { this.sequenceNumber = sequenceNumber; }
    public Integer getQuestionNumber() { return questionNumber; }
    public void setQuestionNumber(Integer questionNumber) { this.questionNumber = questionNumber; }
    public String getQuestionCategory() { return questionCategory; }
    public void setQuestionCategory(String questionCategory) { this.questionCategory = questionCategory; }
    public AnswerEvaluation getAnswerEvaluation() { return answerEvaluation; }
    public void setAnswerEvaluation(AnswerEvaluation answerEvaluation) { this.answerEvaluation = answerEvaluation; }
    public Instant getCreatedAt() { return createdAt; }
}
