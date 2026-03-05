ALTER TABLE quests 
ADD COLUMN on_completed_action VARCHAR(255),
ADD COLUMN on_completed_entity VARCHAR(255),
ADD COLUMN on_completed_entity_id INT;

UPDATE quests
SET
  on_completed_action = '',
  on_completed_entity = '',
  on_completed_entity_id = 0; 