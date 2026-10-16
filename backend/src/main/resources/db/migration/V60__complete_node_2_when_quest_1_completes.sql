UPDATE quests
SET on_completed_action = 'COMPLETE',
    on_completed_entity = 'NODE',
    on_completed_entity_id = 2
WHERE id = 1;