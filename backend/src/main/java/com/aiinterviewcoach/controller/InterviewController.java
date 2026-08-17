package com.aiinterviewcoach.controller;

import com.aiinterviewcoach.dto.request.CreateInterviewRequest;
import com.aiinterviewcoach.dto.request.SubmitAnswerRequest;
import com.aiinterviewcoach.dto.response.InterviewResponse;
import com.aiinterviewcoach.dto.response.InterviewSummaryResponse;
import com.aiinterviewcoach.security.AuthenticatedUser;
import com.aiinterviewcoach.service.interview.InterviewService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/interviews")
public class InterviewController {
    private final InterviewService interviewService;

    public InterviewController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InterviewResponse create(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @Valid @RequestBody CreateInterviewRequest request) {
        return interviewService.create(principal.getId(), request);
    }

    @GetMapping
    public List<InterviewSummaryResponse> list(@AuthenticationPrincipal AuthenticatedUser principal) {
        return interviewService.list(principal.getId());
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
