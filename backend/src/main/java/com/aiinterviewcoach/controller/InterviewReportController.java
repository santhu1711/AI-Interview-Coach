package com.aiinterviewcoach.controller;

import com.aiinterviewcoach.dto.response.InterviewReportResponse;
import com.aiinterviewcoach.security.AuthenticatedUser;
import com.aiinterviewcoach.service.report.InterviewReportService;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/interviews/{sessionId}/report")
public class InterviewReportController {
    private final InterviewReportService reportService;

    public InterviewReportController(InterviewReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InterviewReportResponse generate(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable UUID sessionId) {
        return reportService.generate(principal.getId(), sessionId);
    }

    @GetMapping
    public InterviewReportResponse get(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable UUID sessionId) {
        return reportService.get(principal.getId(), sessionId);
    }
}
