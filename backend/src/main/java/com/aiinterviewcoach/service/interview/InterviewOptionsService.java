package com.aiinterviewcoach.service.interview;

import com.aiinterviewcoach.dto.response.InterviewOptionResponse;
import com.aiinterviewcoach.dto.response.InterviewOptionsResponse;
import com.aiinterviewcoach.dto.response.TextInputConstraintResponse;
import com.aiinterviewcoach.enums.Difficulty;
import com.aiinterviewcoach.enums.ExperienceLevel;
import com.aiinterviewcoach.enums.FieldCategory;
import com.aiinterviewcoach.enums.InterviewDomain;
import com.aiinterviewcoach.enums.InterviewMode;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class InterviewOptionsService {
    public static final int MINIMUM_QUESTIONS = 5;
    public static final int MAXIMUM_QUESTIONS = 20;
    public static final int DEFAULT_QUESTIONS = 10;
    public static final int CUSTOM_DOMAIN_MINIMUM_LENGTH = 2;
    public static final int CUSTOM_DOMAIN_MAXIMUM_LENGTH = 120;
    public static final int TARGET_ROLE_MINIMUM_LENGTH = 2;
    public static final int TARGET_ROLE_MAXIMUM_LENGTH = 150;

    private static final List<InterviewDomain> IT_DOMAINS = List.of(
            InterviewDomain.JAVA, InterviewDomain.SPRING_BOOT, InterviewDomain.DSA,
            InterviewDomain.SQL, InterviewDomain.PYTHON, InterviewDomain.JAVASCRIPT,
            InterviewDomain.TYPESCRIPT, InterviewDomain.REACT, InterviewDomain.NEXT_JS,
            InterviewDomain.FRONTEND_DEVELOPMENT, InterviewDomain.BACKEND_DEVELOPMENT,
            InterviewDomain.FULL_STACK_DEVELOPMENT, InterviewDomain.DEVOPS,
            InterviewDomain.CLOUD_COMPUTING, InterviewDomain.CYBERSECURITY,
            InterviewDomain.DATA_SCIENCE, InterviewDomain.ARTIFICIAL_INTELLIGENCE,
            InterviewDomain.MACHINE_LEARNING, InterviewDomain.IOT,
            InterviewDomain.EMBEDDED_SYSTEMS, InterviewDomain.SYSTEM_DESIGN,
            InterviewDomain.SOFTWARE_TESTING, InterviewDomain.TECHNICAL_SUPPORT,
            InterviewDomain.CUSTOM);

    private static final List<InterviewDomain> NON_IT_DOMAINS = List.of(
            InterviewDomain.HUMAN_RESOURCES, InterviewDomain.CUSTOMER_SUPPORT,
            InterviewDomain.CUSTOMER_SUCCESS, InterviewDomain.SALES, InterviewDomain.MARKETING,
            InterviewDomain.DIGITAL_MARKETING, InterviewDomain.BUSINESS_DEVELOPMENT,
            InterviewDomain.OPERATIONS, InterviewDomain.PROJECT_COORDINATION,
            InterviewDomain.ADMINISTRATION, InterviewDomain.FINANCE, InterviewDomain.BANKING,
            InterviewDomain.TEACHING, InterviewDomain.TRAINING, InterviewDomain.CONTENT_WRITING,
            InterviewDomain.RECRUITMENT, InterviewDomain.MANAGEMENT, InterviewDomain.LEADERSHIP,
            InterviewDomain.GENERAL_HR, InterviewDomain.CUSTOM);

    private static final List<InterviewMode> IT_MODES = List.of(
            InterviewMode.TECHNICAL, InterviewMode.CODING, InterviewMode.CONCEPTUAL,
            InterviewMode.SCENARIO_BASED, InterviewMode.DEBUGGING, InterviewMode.SYSTEM_DESIGN,
            InterviewMode.MIXED);

    private static final List<InterviewMode> NON_IT_MODES = List.of(
            InterviewMode.HR, InterviewMode.BEHAVIOURAL, InterviewMode.SITUATIONAL,
            InterviewMode.ROLE_SPECIFIC, InterviewMode.COMMUNICATION,
            InterviewMode.CUSTOMER_HANDLING, InterviewMode.LEADERSHIP, InterviewMode.MIXED);

    private static final Map<Enum<?>, String> LABELS = Map.ofEntries(
            Map.entry(FieldCategory.IT, "IT Field"), Map.entry(FieldCategory.NON_IT, "Non-IT Field"),
            Map.entry(InterviewDomain.JAVA, "Java"), Map.entry(InterviewDomain.SPRING_BOOT, "Spring Boot"),
            Map.entry(InterviewDomain.DSA, "Data Structures and Algorithms"), Map.entry(InterviewDomain.SQL, "SQL"),
            Map.entry(InterviewDomain.PYTHON, "Python"), Map.entry(InterviewDomain.JAVASCRIPT, "JavaScript"),
            Map.entry(InterviewDomain.TYPESCRIPT, "TypeScript"), Map.entry(InterviewDomain.REACT, "React"),
            Map.entry(InterviewDomain.NEXT_JS, "Next.js"), Map.entry(InterviewDomain.FRONTEND_DEVELOPMENT, "Frontend Development"),
            Map.entry(InterviewDomain.BACKEND_DEVELOPMENT, "Backend Development"), Map.entry(InterviewDomain.FULL_STACK_DEVELOPMENT, "Full Stack Development"),
            Map.entry(InterviewDomain.DEVOPS, "DevOps"), Map.entry(InterviewDomain.CLOUD_COMPUTING, "Cloud Computing"),
            Map.entry(InterviewDomain.CYBERSECURITY, "Cybersecurity"), Map.entry(InterviewDomain.DATA_SCIENCE, "Data Science"),
            Map.entry(InterviewDomain.ARTIFICIAL_INTELLIGENCE, "Artificial Intelligence"), Map.entry(InterviewDomain.MACHINE_LEARNING, "Machine Learning"),
            Map.entry(InterviewDomain.IOT, "IoT"), Map.entry(InterviewDomain.EMBEDDED_SYSTEMS, "Embedded Systems"),
            Map.entry(InterviewDomain.SYSTEM_DESIGN, "System Design"), Map.entry(InterviewDomain.SOFTWARE_TESTING, "Software Testing"),
            Map.entry(InterviewDomain.TECHNICAL_SUPPORT, "Technical Support"), Map.entry(InterviewDomain.HUMAN_RESOURCES, "Human Resources"),
            Map.entry(InterviewDomain.CUSTOMER_SUPPORT, "Customer Support"), Map.entry(InterviewDomain.CUSTOMER_SUCCESS, "Customer Success"),
            Map.entry(InterviewDomain.SALES, "Sales"), Map.entry(InterviewDomain.MARKETING, "Marketing"),
            Map.entry(InterviewDomain.DIGITAL_MARKETING, "Digital Marketing"), Map.entry(InterviewDomain.BUSINESS_DEVELOPMENT, "Business Development"),
            Map.entry(InterviewDomain.OPERATIONS, "Operations"), Map.entry(InterviewDomain.PROJECT_COORDINATION, "Project Coordination"),
            Map.entry(InterviewDomain.ADMINISTRATION, "Administration"), Map.entry(InterviewDomain.FINANCE, "Finance"),
            Map.entry(InterviewDomain.BANKING, "Banking"), Map.entry(InterviewDomain.TEACHING, "Teaching"),
            Map.entry(InterviewDomain.TRAINING, "Training"), Map.entry(InterviewDomain.CONTENT_WRITING, "Content Writing"),
            Map.entry(InterviewDomain.RECRUITMENT, "Recruitment"), Map.entry(InterviewDomain.MANAGEMENT, "Management"),
            Map.entry(InterviewDomain.LEADERSHIP, "Leadership"), Map.entry(InterviewDomain.GENERAL_HR, "General HR"),
            Map.entry(InterviewDomain.CUSTOM, "Custom Domain"),
            Map.entry(InterviewMode.TECHNICAL, "Technical"), Map.entry(InterviewMode.CODING, "Coding"),
            Map.entry(InterviewMode.CONCEPTUAL, "Conceptual"), Map.entry(InterviewMode.SCENARIO_BASED, "Scenario Based"),
            Map.entry(InterviewMode.DEBUGGING, "Debugging"), Map.entry(InterviewMode.SYSTEM_DESIGN, "System Design"),
            Map.entry(InterviewMode.HR, "HR"), Map.entry(InterviewMode.BEHAVIOURAL, "Behavioural"),
            Map.entry(InterviewMode.SITUATIONAL, "Situational"), Map.entry(InterviewMode.ROLE_SPECIFIC, "Role Specific"),
            Map.entry(InterviewMode.COMMUNICATION, "Communication"), Map.entry(InterviewMode.CUSTOMER_HANDLING, "Customer Handling"),
            Map.entry(InterviewMode.LEADERSHIP, "Leadership"), Map.entry(InterviewMode.MIXED, "Mixed"),
            Map.entry(Difficulty.EASY, "Easy"), Map.entry(Difficulty.MEDIUM, "Medium"), Map.entry(Difficulty.HARD, "Hard"),
            Map.entry(ExperienceLevel.BEGINNER, "Beginner"), Map.entry(ExperienceLevel.INTERMEDIATE, "Intermediate"),
            Map.entry(ExperienceLevel.EXPERIENCED, "Experienced"));

    private final InterviewOptionsResponse options = buildOptions();

    public InterviewOptionsResponse getOptions() {
        return options;
    }

    public boolean supportsDomain(FieldCategory category, InterviewDomain domain) {
        return domainsFor(category).contains(domain);
    }

    public boolean supportsMode(FieldCategory category, InterviewMode mode) {
        return modesFor(category).contains(mode);
    }

    private static List<InterviewDomain> domainsFor(FieldCategory category) {
        return category == FieldCategory.IT ? IT_DOMAINS : NON_IT_DOMAINS;
    }

    private static List<InterviewMode> modesFor(FieldCategory category) {
        return category == FieldCategory.IT ? IT_MODES : NON_IT_MODES;
    }

    private static InterviewOptionsResponse buildOptions() {
        Map<FieldCategory, String> domainLabels = new EnumMap<>(FieldCategory.class);
        domainLabels.put(FieldCategory.IT, "Technical Domain");
        domainLabels.put(FieldCategory.NON_IT, "Professional Domain");

        Map<FieldCategory, List<InterviewOptionResponse>> domains = new EnumMap<>(FieldCategory.class);
        domains.put(FieldCategory.IT, options(IT_DOMAINS));
        domains.put(FieldCategory.NON_IT, options(NON_IT_DOMAINS));

        Map<FieldCategory, List<InterviewOptionResponse>> modes = new EnumMap<>(FieldCategory.class);
        modes.put(FieldCategory.IT, options(IT_MODES));
        modes.put(FieldCategory.NON_IT, options(NON_IT_MODES));

        return new InterviewOptionsResponse(
                options(List.of(FieldCategory.values())),
                Collections.unmodifiableMap(domainLabels),
                Collections.unmodifiableMap(domains),
                Collections.unmodifiableMap(modes),
                options(List.of(Difficulty.values())),
                options(List.of(ExperienceLevel.values())),
                MINIMUM_QUESTIONS,
                MAXIMUM_QUESTIONS,
                DEFAULT_QUESTIONS,
                new TextInputConstraintResponse(CUSTOM_DOMAIN_MINIMUM_LENGTH, CUSTOM_DOMAIN_MAXIMUM_LENGTH),
                new TextInputConstraintResponse(TARGET_ROLE_MINIMUM_LENGTH, TARGET_ROLE_MAXIMUM_LENGTH));
    }

    private static List<InterviewOptionResponse> options(List<? extends Enum<?>> values) {
        return values.stream().map(value -> new InterviewOptionResponse(value.name(), LABELS.get(value))).toList();
    }
}
