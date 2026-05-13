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

UPDATE nodes
SET
  on_execution_action = 'START',
  on_execution_entity = 'COMBAT', 
  on_execution_entity_id = 1 
WHERE id = 3;

UPDATE nodes
SET
  on_execution_action = 'SWITCH',
  on_execution_entity = 'MAP', 
  on_execution_entity_id = 1 
WHERE id = 6;

UPDATE nodes
SET
  on_execution_action = 'SWITCH',
  on_execution_entity = 'MAP', 
  on_execution_entity_id = 0 
WHERE id = 7;

UPDATE nodes
SET
  price = 0;