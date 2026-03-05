UPDATE quests
SET
  on_completed_action = 'GAIN',
  on_completed_entity = 'SKILL', 
  on_completed_entity_id = 1 
WHERE id = 1;