UPDATE conversations
SET
  on_completed_action = 'COMPLETE',
  on_completed_entity = 'NODE',
  on_completed_entity_id = 1
WHERE id = 8;

UPDATE combat_configs
SET
  on_completed_action = 'COMPLETE',
  on_completed_entity = 'NODE',
  on_completed_entity_id = 3
WHERE id = 1;

UPDATE conversations
SET
  on_completed_action = 'COMPLETE',
  on_completed_entity = 'NODE',
  on_completed_entity_id = 4
WHERE id = 10;
