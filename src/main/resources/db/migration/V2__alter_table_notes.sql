-- Preview metadata
ALTER TABLE group_notes
ADD COLUMN preview_type VARCHAR(20),
ADD COLUMN preview_available BOOLEAN DEFAULT FALSE;

CREATE INDEX idx_notes_group_created ON group_notes (group_id, created_at DESC);
CREATE INDEX idx_notes_group_title ON group_notes (group_id, title);
CREATE INDEX idx_notes_group_filetype ON group_notes (group_id, file_type);
CREATE INDEX idx_notes_group_uploader ON group_notes (group_id, uploaded_by);