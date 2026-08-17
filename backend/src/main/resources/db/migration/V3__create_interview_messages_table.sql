CREATE TABLE interview_messages (
    id BIGINT NOT NULL AUTO_INCREMENT,
    interview_session_id CHAR(36) NOT NULL,
    role VARCHAR(20) NOT NULL,
    content TEXT NOT NULL,
    sequence_number INT NOT NULL,
    question_number INT NULL,
    question_category VARCHAR(120) NULL,
    answer_evaluation VARCHAR(30) NOT NULL DEFAULT 'NOT_APPLICABLE',
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT fk_interview_messages_session FOREIGN KEY (interview_session_id) REFERENCES interview_sessions (id) ON DELETE CASCADE,
    CONSTRAINT chk_interview_messages_role CHECK (role IN ('SYSTEM', 'ASSISTANT', 'USER')),
    CONSTRAINT chk_interview_messages_evaluation CHECK (answer_evaluation IN ('NOT_APPLICABLE', 'STRONG', 'PARTIAL', 'INCORRECT')),
    CONSTRAINT chk_interview_messages_sequence CHECK (sequence_number > 0),
    CONSTRAINT chk_interview_messages_question CHECK (question_number IS NULL OR question_number > 0)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

