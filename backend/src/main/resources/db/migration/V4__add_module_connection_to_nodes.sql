ALTER TABLE nodes 
DROP COLUMN event_on_execution;

ALTER TABLE nodes 
ADD COLUMN on_execution_action VARCHAR(255),
ADD COLUMN on_execution_entity VARCHAR(255),
ADD COLUMN on_execution_entity_id INT;