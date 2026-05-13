UPDATE nodes
SET price = 0,
    on_execution_action = 'START',
    on_execution_entity = 'COMBAT',
    on_execution_entity_id = 1
WHERE id = 1;
