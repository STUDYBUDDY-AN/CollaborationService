-- V2__alter_group_messages_table.sql

ALTER TABLE group_messages
    ADD COLUMN edited_at TIMESTAMP NULL,
    ADD COLUMN deleted BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN deleted_at TIMESTAMP NULL,
    ADD COLUMN deleted_by CHAR(36) NULL;
