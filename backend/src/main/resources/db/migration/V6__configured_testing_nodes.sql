UPDATE nodes
SET
  on_execution_action = 'START',
  on_execution_entity = 'CNV', 
  on_execution_entity_id = 1 
WHERE id = 1;

UPDATE nodes
SET
  on_execution_action = 'COMPLETE',
  on_execution_entity = 'QUEST', 
  on_execution_entity_id = 1 
WHERE id = 2;