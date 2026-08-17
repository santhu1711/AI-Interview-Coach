package com.aiinterviewcoach;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AiInterviewCoachApplication {
    private static final Logger log = LoggerFactory.getLogger(AiInterviewCoachApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(AiInterviewCoachApplication.class, args);
    }

    @Bean
    CommandLineRunner startupMessage() {
        return args -> log.info("AI Interview Coach API started successfully");
    }
}

