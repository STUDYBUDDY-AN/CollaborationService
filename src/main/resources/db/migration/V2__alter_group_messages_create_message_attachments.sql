-- V2__alter_group_messages_create_message_attachments

ALTER TABLE group_messages
    ADD COLUMN edited_at TIMESTAMP NULL,
    ADD COLUMN deleted BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN deleted_at TIMESTAMP NULL,
    ADD COLUMN deleted_by CHAR(36) NULL;

CREATE TABLE message_attachments (
    id CHAR(36) PRIMARY KEY,
    message_id CHAR(36) NOT NULL,
    file_url TEXT NOT NULL,
    file_type VARCHAR(50),
    file_size_bytes BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (message_id) REFERENCES group_messages(id) ON DELETE CASCADE
);

CREATE INDEX idx_message_attachments_msgid ON message_attachments (message_id);