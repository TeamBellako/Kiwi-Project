INSERT INTO conversations (
    id, name, type, sprite, expresion, background, fx, dark, dialog, dialog_m, dialog_w, delay_start_ms, delay_end_ms, next_event, event_id, incidence_name_to_set, incidence_value_to_set
) VALUES
(10, 'Conditional Options Testing', 'SMALL', 1, 1, NULL, NULL, 0, "I'm going to show you some options...", "", "", NULL, NULL, 'END', NULL, "showDialogOption", TRUE),
(11, 'Conditional Options Testing End', 'SMALL', 1, 1, NULL, NULL, 0, "Bye bye!", "", "", NULL, NULL, 'END', NULL, "", TRUE);

INSERT INTO conversation_options (
    id, conversation_id, text, text_m, text_w, next_event_id, cost, incidence_to_show
) VALUES
(5, 10, 'This option should always show', '', '', 11, NULL, ""),
(6, 10, 'This means the incidence is true', '', '', 11, NULL, "showDialogOption");

UPDATE nodes
SET
  on_execution_action = 'START',
  on_execution_entity = 'CNV', 
  on_execution_entity_id = 10
WHERE id = 4;