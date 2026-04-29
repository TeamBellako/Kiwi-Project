-- Module connection columns on goals
ALTER TABLE goals
    ADD COLUMN on_completed_action VARCHAR(255),
    ADD COLUMN on_completed_entity VARCHAR(255),
    ADD COLUMN on_completed_entity_id INT;

UPDATE goals
SET on_completed_action = '',
    on_completed_entity = '',
    on_completed_entity_id = 0;