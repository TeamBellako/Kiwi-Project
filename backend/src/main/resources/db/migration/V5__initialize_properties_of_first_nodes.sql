UPDATE nodes
SET
  on_execution_action = '',
  on_execution_entity = '',
  on_execution_entity_id = 0;  

UPDATE nodes
SET
  on_execution_action = 'SWITCH',
  on_execution_entity = 'MAP', 
  on_execution_entity_id = 2 
WHERE id = 6;

UPDATE nodes
SET
  on_execution_action = 'SWITCH',
  on_execution_entity = 'MAP', 
  on_execution_entity_id = 1 
WHERE id = 7;