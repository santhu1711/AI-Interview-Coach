CREATE TABLE interview_sessions (
    id CHAR(36) NOT NULL,
    user_id BIGINT NOT NULL,
    field_category VARCHAR(20) NOT NULL,
    interview_domain VARCHAR(50) NOT NULL,
    custom_domain VARCHAR(120) NULL,
    topic VARCHAR(200) NOT NULL,
    difficulty VARCHAR(20) NOT NULL,
    interview_mode VARCHAR(40) NOT NULL,
    target_role VARCHAR(150) NOT NULL,
    experience_level VARCHAR(20) NOT NULL,
    total_questions INT NOT NULL,
    current_question_number INT NOT NULL DEFAULT 0,
    follow_up_count INT NOT NULL DEFAULT 0,
    status VARCHAR(30) NOT NULL DEFAULT 'CREATED',
    overall_score INT NULL,
    started_at TIMESTAMP(6) NULL,
    completed_at TIMESTAMP(6) NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    version BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT fk_interview_sessions_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT chk_interview_sessions_field CHECK (field_category IN ('IT', 'NON_IT')),
    CONSTRAINT chk_interview_sessions_difficulty CHECK (difficulty IN ('EASY', 'MEDIUM', 'HARD')),
    CONSTRAINT chk_interview_sessions_experience CHECK (experience_level IN ('BEGINNER', 'INTERMEDIATE', 'EXPERIENCED')),
    CONSTRAINT chk_interview_sessions_status CHECK (status IN ('CREATED', 'IN_PROGRESS', 'COMPLETED', 'ABANDONED', 'REPORT_GENERATED')),
    CONSTRAINT chk_interview_sessions_question_counts CHECK (
        total_questions > 0 AND current_question_number >= 0 AND current_question_number <= total_questions AND follow_up_count >= 0
    ),
    CONSTRAINT chk_interview_sessions_score CHECK (overall_score IS NULL OR overall_score BETWEEN 0 AND 100),
    CONSTRAINT chk_interview_sessions_custom_domain CHECK (
        (interview_domain = 'CUSTOM' AND custom_domain IS NOT NULL AND CHAR_LENGTH(TRIM(custom_domain)) > 0)
        OR (interview_domain <> 'CUSTOM' AND custom_domain IS NULL)
    )
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

