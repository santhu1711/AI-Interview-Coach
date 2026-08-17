package com.aiinterviewcoach.controller;

import com.aiinterviewcoach.dto.response.DashboardPerformanceResponse;
import com.aiinterviewcoach.dto.response.DashboardSummaryResponse;
import com.aiinterviewcoach.security.AuthenticatedUser;
import com.aiinterviewcoach.service.dashboard.DashboardService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    public DashboardSummaryResponse summary(@AuthenticationPrincipal AuthenticatedUser principal) {
        return dashboardService.summary(principal.getId());
    }

    @GetMapping("/performance")
    public DashboardPerformanceResponse performance(@AuthenticationPrincipal AuthenticatedUser principal) {
        return dashboardService.performance(principal.getId());
    }
}
