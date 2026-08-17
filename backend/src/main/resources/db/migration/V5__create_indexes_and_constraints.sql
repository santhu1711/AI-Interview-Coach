CREATE INDEX idx_interview_sessions_user_created
    ON interview_sessions (user_id, created_at DESC);

CREATE INDEX idx_interview_sessions_user_status
    ON interview_sessions (user_id, status);

CREATE INDEX idx_interview_sessions_user_field
    ON interview_sessions (user_id, field_category);

CREATE INDEX idx_interview_sessions_domain
    ON interview_sessions (interview_domain);

CREATE UNIQUE INDEX uk_interview_messages_session_sequence
    ON interview_messages (interview_session_id, sequence_number);

CREATE INDEX idx_interview_messages_session_question
    ON interview_messages (interview_session_id, question_number);

CREATE UNIQUE INDEX uk_interview_reports_session
    ON interview_reports (interview_session_id);

