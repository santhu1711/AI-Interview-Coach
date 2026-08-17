package com.aiinterviewcoach.service.interview;

import com.aiinterviewcoach.dto.request.CreateInterviewRequest;
import com.aiinterviewcoach.dto.request.SubmitAnswerRequest;
import com.aiinterviewcoach.dto.response.InterviewMessageResponse;
import com.aiinterviewcoach.dto.response.InterviewResponse;
import com.aiinterviewcoach.dto.response.InterviewSummaryResponse;
import com.aiinterviewcoach.entity.InterviewMessage;
import com.aiinterviewcoach.entity.InterviewSession;
import com.aiinterviewcoach.entity.User;
import com.aiinterviewcoach.enums.AnswerEvaluation;
import com.aiinterviewcoach.enums.InterviewStatus;
import com.aiinterviewcoach.enums.MessageRole;
import com.aiinterviewcoach.exception.DuplicateAnswerException;
import com.aiinterviewcoach.exception.InvalidInterviewStateException;
import com.aiinterviewcoach.exception.ResourceNotFoundException;
import com.aiinterviewcoach.repository.InterviewMessageRepository;
import com.aiinterviewcoach.repository.InterviewSessionRepository;
import com.aiinterviewcoach.repository.UserRepository;
import com.aiinterviewcoach.service.ai.AiTranscriptEntry;
import com.aiinterviewcoach.service.ai.InterviewAiContext;
import com.aiinterviewcoach.service.ai.InterviewAiResponse;
import com.aiinterviewcoach.service.ai.InterviewAiService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InterviewService {
    private static final Logger log = LoggerFactory.getLogger(InterviewService.class);

    private final InterviewSessionRepository sessionRepository;
    private final InterviewMessageRepository messageRepository;
    private final UserRepository userRepository;
    private final InterviewConfigurationValidator configurationValidator;
    private final InterviewAiService aiService;

    public InterviewService(
            InterviewSessionRepository sessionRepository,
            InterviewMessageRepository messageRepository,
            UserRepository userRepository,
            InterviewConfigurationValidator configurationValidator,
            InterviewAiService aiService) {
        this.sessionRepository = sessionRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.configurationValidator = configurationValidator;
        this.aiService = aiService;
    }

    @Transactional
    public InterviewResponse create(Long userId, CreateInterviewRequest request) {
        configurationValidator.validateSelection(
                request.fieldCategory(), request.interviewDomain(), request.interviewMode(), request.customDomain());
        configurationValidator.validateTargetRole(request.targetRole());
        configurationValidator.validateTotalQuestions(request.totalQuestions());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        InterviewSession session = new InterviewSession();
        session.setUser(user);
        session.setFieldCategory(request.fieldCategory());
        session.setInterviewDomain(request.interviewDomain());
        session.setCustomDomain(normalizeCustomDomain(request));
        session.setTopic(request.topic().trim());
        session.setDifficulty(request.difficulty());
        session.setInterviewMode(request.interviewMode());
        session.setTargetRole(request.targetRole().trim());
        session.setExperienceLevel(request.experienceLevel());
        session.setTotalQuestions(request.totalQuestions());
        session.setCurrentQuestionNumber(0);
        session.setFollowUpCount(0);
        session.setStatus(InterviewStatus.CREATED);
        sessionRepository.save(session);

        InterviewAiResponse firstQuestion = aiService.generate(context(session, List.of(), AnswerEvaluation.NOT_APPLICABLE));
        InterviewMessage assistant = assistantMessage(session, firstQuestion, 1, 1);
        messageRepository.save(assistant);
        session.setCurrentQuestionNumber(1);
        session.setStatus(InterviewStatus.IN_PROGRESS);
        session.setStartedAt(Instant.now());
        sessionRepository.save(session);
        log.info("Interview {} created for user {}", session.getId(), userId);
        return response(session, List.of(assistant));
    }

    @Transactional(readOnly = true)
    public InterviewResponse get(Long userId, UUID sessionId) {
        InterviewSession session = ownedSession(userId, sessionId);
        return response(session, messageRepository.findAllByInterviewSessionIdOrderBySequenceNumberAsc(sessionId));
    }

    @Transactional(readOnly = true)
    public List<InterviewSummaryResponse> list(Long userId) {
        return sessionRepository.findAllByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(InterviewService::summary)
                .toList();
    }

    @Transactional
    public InterviewResponse submitAnswer(Long userId, UUID sessionId, SubmitAnswerRequest request) {
        InterviewSession session = ownedSession(userId, sessionId);
        requireInProgress(session, "Answers can only be submitted to an interview in progress.");
        List<InterviewMessage> messages = new ArrayList<>(
                messageRepository.findAllByInterviewSessionIdOrderBySequenceNumberAsc(sessionId));
        if (messages.isEmpty() || messages.getLast().getRole() != MessageRole.ASSISTANT) {
            throw new DuplicateAnswerException("The current question has already been answered.");
        }

        AnswerEvaluation previousEvaluation = messages.reversed().stream()
                .filter(message -> message.getRole() == MessageRole.USER)
                .map(InterviewMessage::getAnswerEvaluation)
                .findFirst()
                .orElse(AnswerEvaluation.NOT_APPLICABLE);
        InterviewMessage userMessage = new InterviewMessage();
        userMessage.setInterviewSession(session);
        userMessage.setRole(MessageRole.USER);
        userMessage.setContent(request.answer().trim());
        userMessage.setSequenceNumber(messages.getLast().getSequenceNumber() + 1);
        userMessage.setQuestionNumber(session.getCurrentQuestionNumber());
        userMessage.setAnswerEvaluation(AnswerEvaluation.NOT_APPLICABLE);
        messageRepository.save(userMessage);
        messages.add(userMessage);

        InterviewAiResponse aiResponse = aiService.generate(context(session, messages, previousEvaluation));
        userMessage.setAnswerEvaluation(aiResponse.evaluation());

        boolean followUpAlreadyAsked = messages.stream()
                .filter(message -> message.getRole() == MessageRole.ASSISTANT)
                .filter(message -> Integer.valueOf(session.getCurrentQuestionNumber()).equals(message.getQuestionNumber()))
                .count() > 1;
        boolean askFollowUp = aiResponse.isFollowUp() && !followUpAlreadyAsked;

        if (askFollowUp) {
            InterviewMessage assistant = assistantMessage(
                    session, aiResponse, userMessage.getSequenceNumber() + 1, session.getCurrentQuestionNumber());
            messageRepository.save(assistant);
            messages.add(assistant);
            session.setFollowUpCount(session.getFollowUpCount() + 1);
        } else if (session.getCurrentQuestionNumber() >= session.getTotalQuestions()) {
            transitionToCompleted(session);
        } else {
            int nextQuestionNumber = session.getCurrentQuestionNumber() + 1;
            InterviewMessage assistant = assistantMessage(
                    session, aiResponse, userMessage.getSequenceNumber() + 1, nextQuestionNumber);
            messageRepository.save(assistant);
            messages.add(assistant);
            session.setCurrentQuestionNumber(nextQuestionNumber);
        }
        sessionRepository.save(session);
        log.info("Answer submitted for interview {} by user {}", sessionId, userId);
        return response(session, messages);
    }

    @Transactional
    public InterviewResponse complete(Long userId, UUID sessionId) {
        InterviewSession session = ownedSession(userId, sessionId);
        requireInProgress(session, "Only an interview in progress can be completed.");
        transitionToCompleted(session);
        sessionRepository.save(session);
        log.info("Interview {} completed by user {}", sessionId, userId);
        return response(session, messageRepository.findAllByInterviewSessionIdOrderBySequenceNumberAsc(sessionId));
    }

    @Transactional
    public InterviewResponse abandon(Long userId, UUID sessionId) {
        InterviewSession session = ownedSession(userId, sessionId);
        if (session.getStatus() != InterviewStatus.CREATED && session.getStatus() != InterviewStatus.IN_PROGRESS) {
            throw new InvalidInterviewStateException("Only a created or in-progress interview can be abandoned.");
        }
        session.setStatus(InterviewStatus.ABANDONED);
        session.setCompletedAt(Instant.now());
        sessionRepository.save(session);
        log.info("Interview {} abandoned by user {}", sessionId, userId);
        return response(session, messageRepository.findAllByInterviewSessionIdOrderBySequenceNumberAsc(sessionId));
    }

    @Transactional
    public void delete(Long userId, UUID sessionId) {
        InterviewSession session = ownedSession(userId, sessionId);
        messageRepository.deleteAll(
                messageRepository.findAllByInterviewSessionIdOrderBySequenceNumberAsc(sessionId));
        sessionRepository.delete(session);
        log.info("Interview {} deleted by user {}", sessionId, userId);
    }

    private InterviewSession ownedSession(Long userId, UUID sessionId) {
        return sessionRepository.findByIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found."));
    }

    private InterviewAiContext context(
            InterviewSession session, List<InterviewMessage> messages, AnswerEvaluation previousEvaluation) {
        List<AiTranscriptEntry> transcript = messages.stream()
                .map(message -> new AiTranscriptEntry(message.getRole(), message.getContent()))
                .toList();
        List<String> coveredCategories = new ArrayList<>(messages.stream()
                .filter(message -> message.getRole() == MessageRole.ASSISTANT)
                .map(InterviewMessage::getQuestionCategory)
                .filter(category -> category != null && !category.isBlank())
                .collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new)));
        return new InterviewAiContext(
                session.getFieldCategory(), session.getInterviewDomain(), session.getCustomDomain(),
                session.getTopic(), session.getInterviewMode(), session.getTargetRole(),
                session.getExperienceLevel(), session.getDifficulty(), session.getTotalQuestions(),
                session.getCurrentQuestionNumber(), transcript, coveredCategories,
                previousEvaluation, session.getFollowUpCount());
    }

    private static InterviewMessage assistantMessage(
            InterviewSession session, InterviewAiResponse aiResponse, int sequence, int questionNumber) {
        InterviewMessage message = new InterviewMessage();
        message.setInterviewSession(session);
        message.setRole(MessageRole.ASSISTANT);
        message.setContent(aiResponse.message());
        message.setSequenceNumber(sequence);
        message.setQuestionNumber(questionNumber);
        message.setQuestionCategory(aiResponse.questionCategory());
        message.setAnswerEvaluation(AnswerEvaluation.NOT_APPLICABLE);
        return message;
    }

    private static void requireInProgress(InterviewSession session, String message) {
        if (session.getStatus() != InterviewStatus.IN_PROGRESS) {
            throw new InvalidInterviewStateException(message);
        }
    }

    private static void transitionToCompleted(InterviewSession session) {
        session.setStatus(InterviewStatus.COMPLETED);
        session.setCompletedAt(Instant.now());
    }

    private static String normalizeCustomDomain(CreateInterviewRequest request) {
        return request.interviewDomain() == com.aiinterviewcoach.enums.InterviewDomain.CUSTOM
                ? request.customDomain().trim()
                : null;
    }

    private static InterviewResponse response(InterviewSession session, List<InterviewMessage> messages) {
        return new InterviewResponse(
                session.getId(), session.getFieldCategory(), session.getInterviewDomain(), session.getCustomDomain(),
                session.getTopic(), session.getDifficulty(), session.getInterviewMode(), session.getTargetRole(),
                session.getExperienceLevel(), session.getTotalQuestions(), session.getCurrentQuestionNumber(),
                session.getFollowUpCount(), progress(session), session.getStatus(), session.getOverallScore(),
                session.getStartedAt(), session.getCompletedAt(), session.getCreatedAt(), session.getUpdatedAt(),
                messages.stream().map(InterviewService::messageResponse).toList());
    }

    private static InterviewSummaryResponse summary(InterviewSession session) {
        return new InterviewSummaryResponse(
                session.getId(), session.getFieldCategory(), session.getInterviewDomain(), session.getCustomDomain(),
                session.getTopic(), session.getDifficulty(), session.getInterviewMode(), session.getTargetRole(),
                session.getExperienceLevel(), session.getTotalQuestions(), session.getCurrentQuestionNumber(), session.getFollowUpCount(),
                progress(session), session.getStatus(), session.getOverallScore(), session.getStartedAt(),
                session.getCompletedAt(), session.getCreatedAt(), session.getUpdatedAt());
    }

    private static InterviewMessageResponse messageResponse(InterviewMessage message) {
        return new InterviewMessageResponse(
                message.getId(), message.getRole(), message.getContent(), message.getSequenceNumber(),
                message.getQuestionNumber(), message.getQuestionCategory(), message.getAnswerEvaluation(),
                message.getCreatedAt());
    }

    private static int progress(InterviewSession session) {
        return session.getTotalQuestions() == 0
                ? 0
                : Math.min(100, session.getCurrentQuestionNumber() * 100 / session.getTotalQuestions());
    }
}
