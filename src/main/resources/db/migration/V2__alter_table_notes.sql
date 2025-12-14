-- Preview metadata
ALTER TABLE notes
ADD COLUMN preview_type VARCHAR(20),
ADD COLUMN preview_available BOOLEAN DEFAULT FALSE;

CREATE INDEX idx_notes_group_created ON notes (group_id, created_at DESC);
CREATE INDEX idx_notes_group_title ON notes (group_id, title);
CREATE INDEX idx_notes_group_filetype ON notes (group_id, file_type);
CREATE INDEX idx_notes_group_uploader ON notes (group_id, uploaded_by);
