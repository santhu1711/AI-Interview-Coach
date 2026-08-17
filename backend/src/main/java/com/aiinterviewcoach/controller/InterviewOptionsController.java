package com.aiinterviewcoach.controller;

import com.aiinterviewcoach.dto.response.InterviewOptionsResponse;
import com.aiinterviewcoach.service.interview.InterviewOptionsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/interview-options")
public class InterviewOptionsController {
    private final InterviewOptionsService optionsService;

    public InterviewOptionsController(InterviewOptionsService optionsService) {
        this.optionsService = optionsService;
    }

    @GetMapping
    public InterviewOptionsResponse getOptions() {
        return optionsService.getOptions();
    }
}
