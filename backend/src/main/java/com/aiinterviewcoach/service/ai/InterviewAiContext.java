package com.aiinterviewcoach.service.ai;

import com.aiinterviewcoach.enums.AnswerEvaluation;
import com.aiinterviewcoach.enums.Difficulty;
import com.aiinterviewcoach.enums.ExperienceLevel;
import com.aiinterviewcoach.enums.FieldCategory;
import com.aiinterviewcoach.enums.InterviewDomain;
import com.aiinterviewcoach.enums.InterviewMode;
import java.util.List;

public record InterviewAiContext(
        FieldCategory fieldCategory,
        InterviewDomain interviewDomain,
        String customDomain,
        String topic,
        InterviewMode interviewMode,
        String targetRole,
        ExperienceLevel experienceLevel,
        Difficulty difficulty,
        int totalQuestions,
        int currentQuestionNumber,
        List<AiTranscriptEntry> transcript,
        List<String> previouslyCoveredCategories,
        AnswerEvaluation previousAnswerEvaluation,
        int followUpCount) {

    public InterviewAiContext {
        transcript = transcript == null ? List.of() : List.copyOf(transcript);
        previouslyCoveredCategories = previouslyCoveredCategories == null
                ? List.of()
                : List.copyOf(previouslyCoveredCategories);
        previousAnswerEvaluation = previousAnswerEvaluation == null
                ? AnswerEvaluation.NOT_APPLICABLE
                : previousAnswerEvaluation;
    }

    public boolean firstQuestion() {
        return currentQuestionNumber == 0 && transcript.isEmpty();
    }
}
