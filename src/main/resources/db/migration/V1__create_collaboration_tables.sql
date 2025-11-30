-- V1__create_collaboration_tables.sql

CREATE TABLE study_groups (
    id CHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(500),
    owner_id CHAR(36) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE group_members (
    group_id CHAR(36) NOT NULL,
    user_id CHAR(36) NOT NULL,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (group_id, user_id),
    INDEX idx_user_id (user_id),
    FOREIGN KEY (group_id) REFERENCES study_groups(id) ON DELETE CASCADE
);

CREATE TABLE group_messages (
    id CHAR(36) PRIMARY KEY,
    group_id CHAR(36) NOT NULL,
    sender_id CHAR(36) NOT NULL,
    content VARCHAR(2000) NOT NULL,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_group_sent (group_id, sent_at),
    FOREIGN KEY (group_id) REFERENCES study_groups(id) ON DELETE CASCADE
);

CREATE TABLE group_notes (
    id CHAR(36) PRIMARY KEY,
    group_id CHAR(36) NOT NULL,
    uploaded_by CHAR(36) NOT NULL,
    title VARCHAR(255),
    file_url TEXT NOT NULL,
    file_type VARCHAR(50),
    file_size_bytes BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_group (group_id),
    FOREIGN KEY (group_id) REFERENCES study_groups(id) ON DELETE CASCADE
);

CREATE TABLE pomodoro_sessions (
    id CHAR(36) PRIMARY KEY,
    user_id CHAR(36) NOT NULL,
    group_id CHAR(36),
    started_at TIMESTAMP NOT NULL,
    ended_at TIMESTAMP NOT NULL,
    minutes INT NOT NULL,
    INDEX idx_user (user_id),
    INDEX idx_group (group_id),
    INDEX idx_time (started_at)
);
