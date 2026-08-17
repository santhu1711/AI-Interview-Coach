package com.aiinterviewcoach.service.report;

import com.aiinterviewcoach.dto.response.InterviewReportResponse;
import com.aiinterviewcoach.dto.response.QuestionFeedbackResponse;
import com.aiinterviewcoach.entity.InterviewMessage;
import com.aiinterviewcoach.entity.InterviewReport;
import com.aiinterviewcoach.entity.InterviewSession;
import com.aiinterviewcoach.enums.InterviewStatus;
import com.aiinterviewcoach.exception.DuplicateReportException;
import com.aiinterviewcoach.exception.InvalidInterviewStateException;
import com.aiinterviewcoach.exception.ResourceNotFoundException;
import com.aiinterviewcoach.repository.InterviewMessageRepository;
import com.aiinterviewcoach.repository.InterviewReportRepository;
import com.aiinterviewcoach.repository.InterviewSessionRepository;
import com.aiinterviewcoach.service.ai.ReportAiResponse;
import com.aiinterviewcoach.service.ai.ReportAiService;
import com.aiinterviewcoach.service.ai.ReportQuestionFeedback;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InterviewReportService {
    private static final Logger log = LoggerFactory.getLogger(InterviewReportService.class);
    private static final TypeReference<List<String>> STRING_LIST = new TypeReference<>() {};
    private static final TypeReference<List<QuestionFeedbackResponse>> FEEDBACK_LIST = new TypeReference<>() {};

    private final InterviewSessionRepository sessionRepository;
    private final InterviewMessageRepository messageRepository;
    private final InterviewReportRepository reportRepository;
    private final ReportAiService reportAiService;
    private final ObjectMapper objectMapper;

    public InterviewReportService(
            InterviewSessionRepository sessionRepository,
            InterviewMessageRepository messageRepository,
            InterviewReportRepository reportRepository,
            ReportAiService reportAiService,
            ObjectMapper objectMapper) {
        this.sessionRepository = sessionRepository;
        this.messageRepository = messageRepository;
        this.reportRepository = reportRepository;
        this.reportAiService = reportAiService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public InterviewReportResponse generate(Long userId, UUID sessionId) {
        InterviewSession session = sessionRepository.findOwnedForUpdate(sessionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found."));
        if (reportRepository.existsByInterviewSessionId(sessionId)) {
            throw new DuplicateReportException("A report has already been generated for this interview.");
        }
        if (session.getStatus() != InterviewStatus.COMPLETED) {
            throw new InvalidInterviewStateException("Reports can only be generated for completed interviews.");
        }

        List<InterviewMessage> messages =
                messageRepository.findAllByInterviewSessionIdOrderBySequenceNumberAsc(sessionId);
        ReportAiResponse generated = reportAiService.generate(session, messages);
        InterviewReport report = toEntity(session, generated);
        reportRepository.save(report);
        session.setOverallScore(generated.overallScore());
        session.setStatus(InterviewStatus.REPORT_GENERATED);
        sessionRepository.save(session);
        log.info("Report generated for interview {} by user {}", sessionId, userId);
        return response(report, generated.strengths(), generated.weaknesses(),
                generated.revisionAreas(), generated.questionFeedback().stream()
                        .map(InterviewReportService::feedbackResponse)
                        .toList());
    }

    @Transactional(readOnly = true)
    public InterviewReportResponse get(Long userId, UUID sessionId) {
        InterviewReport report = reportRepository
                .findByInterviewSessionIdAndInterviewSessionUserId(sessionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview report not found."));
        return response(
                report,
                read(report.getStrengths(), STRING_LIST),
                read(report.getWeaknesses(), STRING_LIST),
                read(report.getRevisionAreas(), STRING_LIST),
                read(report.getQuestionFeedbackJson(), FEEDBACK_LIST));
    }

    private InterviewReport toEntity(InterviewSession session, ReportAiResponse generated) {
        InterviewReport report = new InterviewReport();
        report.setInterviewSession(session);
        report.setOverallScore(generated.overallScore());
        report.setTechnicalAccuracyScore(generated.technicalAccuracyScore());
        report.setConceptualUnderstandingScore(generated.conceptualUnderstandingScore());
        report.setProblemSolvingScore(generated.problemSolvingScore());
        report.setCommunicationScore(generated.communicationScore());
        report.setConfidenceScore(generated.confidenceScore());
        report.setSituationalJudgementScore(generated.situationalJudgementScore());
        report.setRoleUnderstandingScore(generated.roleUnderstandingScore());
        report.setStrengths(write(generated.strengths()));
        report.setWeaknesses(write(generated.weaknesses()));
        report.setRevisionAreas(write(generated.revisionAreas()));
        report.setVerdict(generated.verdict());
        report.setRecommendation(generated.recommendation());
        report.setQuestionFeedbackJson(write(generated.questionFeedback().stream()
                .map(InterviewReportService::feedbackResponse)
                .toList()));
        return report;
    }

    private InterviewReportResponse response(
            InterviewReport report,
            List<String> strengths,
            List<String> weaknesses,
            List<String> revisionAreas,
            List<QuestionFeedbackResponse> feedback) {
        return new InterviewReportResponse(
                report.getId(), report.getInterviewSession().getId(),
                report.getInterviewSession().getFieldCategory(), report.getOverallScore(),
                interpretation(report.getOverallScore()), report.getTechnicalAccuracyScore(),
                report.getConceptualUnderstandingScore(), report.getProblemSolvingScore(),
                report.getCommunicationScore(), report.getConfidenceScore(),
                report.getSituationalJudgementScore(), report.getRoleUnderstandingScore(),
                strengths, weaknesses, revisionAreas, report.getVerdict(), report.getRecommendation(),
                feedback, report.getGeneratedAt());
    }

    private static QuestionFeedbackResponse feedbackResponse(ReportQuestionFeedback feedback) {
        return new QuestionFeedbackResponse(
                feedback.question(), feedback.answerSummary(), feedback.evaluation(), feedback.feedback());
    }

    private String write(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Could not serialize interview report.", exception);
        }
    }

    private <T> T read(String value, TypeReference<T> type) {
        try {
            return objectMapper.readValue(value, type);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Stored interview report is invalid.", exception);
        }
    }

    public static String interpretation(int score) {
        if (score >= 85) return "Excellent";
        if (score >= 70) return "Good";
        if (score >= 55) return "Adequate";
        return "Weak";
    }
}
