UPDATE conversations
SET
  on_completed_action = 'START',
  on_completed_entity = 'QUEST', 
  on_completed_entity_id = 1 
WHERE id = 1;