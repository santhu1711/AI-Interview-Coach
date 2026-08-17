package com.aiinterviewcoach.controller;

import com.aiinterviewcoach.dto.request.CreateInterviewRequest;
import com.aiinterviewcoach.dto.request.SubmitAnswerRequest;
import com.aiinterviewcoach.dto.response.InterviewResponse;
import com.aiinterviewcoach.dto.response.InterviewHistoryResponse;
import com.aiinterviewcoach.enums.Difficulty;
import com.aiinterviewcoach.enums.FieldCategory;
import com.aiinterviewcoach.enums.HistorySortOrder;
import com.aiinterviewcoach.enums.InterviewDomain;
import com.aiinterviewcoach.enums.InterviewMode;
import com.aiinterviewcoach.enums.InterviewStatus;
import com.aiinterviewcoach.security.AuthenticatedUser;
import com.aiinterviewcoach.service.history.InterviewHistoryService;
import com.aiinterviewcoach.service.interview.InterviewService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping("/api/interviews")
@Validated
public class InterviewController {
    private final InterviewService interviewService;
    private final InterviewHistoryService historyService;

    public InterviewController(InterviewService interviewService, InterviewHistoryService historyService) {
        this.interviewService = interviewService;
        this.historyService = historyService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InterviewResponse create(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @Valid @RequestBody CreateInterviewRequest request) {
        return interviewService.create(principal.getId(), request);
    }

    @GetMapping
    public InterviewHistoryResponse list(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) FieldCategory fieldCategory,
            @RequestParam(required = false) InterviewDomain domain,
            @RequestParam(required = false) InterviewMode mode,
            @RequestParam(required = false) Difficulty difficulty,
            @RequestParam(required = false) InterviewStatus status,
            @RequestParam(defaultValue = "NEWEST") HistorySortOrder sort,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) int size) {
        return historyService.search(
                principal.getId(), search, fieldCategory, domain, mode, difficulty, status,
                sort, page, size);
    }

    @GetMapping("/{sessionId}")
    public InterviewResponse get(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable UUID sessionId) {
        return interviewService.get(principal.getId(), sessionId);
    }

    @PostMapping("/{sessionId}/answers")
    public InterviewResponse submitAnswer(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable UUID sessionId,
            @Valid @RequestBody SubmitAnswerRequest request) {
        return interviewService.submitAnswer(principal.getId(), sessionId, request);
    }

    @PostMapping("/{sessionId}/complete")
    public InterviewResponse complete(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable UUID sessionId) {
        return interviewService.complete(principal.getId(), sessionId);
    }

    @PostMapping("/{sessionId}/abandon")
    public InterviewResponse abandon(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable UUID sessionId) {
        return interviewService.abandon(principal.getId(), sessionId);
    }

    @DeleteMapping("/{sessionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable UUID sessionId) {
        interviewService.delete(principal.getId(), sessionId);
    }
}
