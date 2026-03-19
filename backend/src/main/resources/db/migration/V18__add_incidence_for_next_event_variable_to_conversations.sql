ALTER TABLE conversations
ADD COLUMN incidence_for_next_event VARCHAR(255),
ADD COLUMN fallback_event_id BIGINT;