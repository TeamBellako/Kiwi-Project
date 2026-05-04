UPDATE conversations
SET
	next_event = "CONVERSATION",
    event_id = 7
WHERE id = 1;

INSERT IGNORE INTO conversations (
    id, name, type, sprite, expresion, background, fx, dark, dialog, dialog_m, dialog_w, delay_start_ms, delay_end_ms, next_event, event_id, on_completed_action, on_completed_entity, on_completed_entity_id, incidence_for_next_event, fallback_event_id
) VALUES
(7, 'Conditional Placeholder', 'SMALL', 1, 1, NULL, NULL, 0, "This is a conditional dialogue for the variable isSorenInGroup", "", "", NULL, NULL, 'CONVERSATION', 8, "", "", 0, "isSorenInGroup", 9),
(8, 'Conditional Placeholder True', 'SMALL', 1, 1, NULL, NULL, 0, "This is the dialogue if the variable is true", "", "", NULL, NULL, 'END', NULL, "", "", 0, NULL, NULL),
(9, 'Conditional Placeholder False', 'SMALL', 1, 1, NULL, NULL, 0, "This is the fallback dialogue if the variable is false", "", "", NULL, NULL, 'END', NULL, "", "", 0, NULL, NULL);