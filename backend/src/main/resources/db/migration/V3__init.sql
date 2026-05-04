-- sprites
INSERT IGNORE INTO sprites (id, name) VALUES (1, 'Liria');

-- expressions
INSERT IGNORE INTO expressions (id, name) VALUES (1, 'Normal');

-- backgrounds
INSERT IGNORE INTO backgrounds (id, name) VALUES (1, 'Castel');

-- fx
INSERT IGNORE INTO fx (id, name) VALUES (1, 'Pasos');

-- conversations
INSERT IGNORE INTO conversations (
    id, name, type, sprite, expresion, background, fx, dark, dialog, dialog_m, dialog_w, delay_start_ms, delay_end_ms, next_event, event_id
) VALUES
(1, 'Intro', 'SMALL', 1, 1, NULL, NULL, 0, "Wellcome to GrowTale and wellcome to Minveil. I'm Liria 😊", "Wellcome to GrowTale and wellcome to Minveil. I'm Liria 😊", "Wellcome to GrowTale and wellcome to Minveil. I'm Liria 😊", NULL, NULL, 'END', NULL),
(2, 'Ejemplo', 'FULL', 1, 1, NULL, 1, 0, '¿Te gusta este sitio?', '¿Te gusta este sitio?', '¿Te gusta este sitio?', NULL, NULL, 'CONVERSATION', NULL),
(3, 'Ejemplo 2', 'FULL', 1, 1, NULL, 1, 0, 'Te dije que tenías que venir abrigada. ¿Quieres que te deje el abrigo?', 'Te dije que tenías que venir abrigada. ¿Quieres que te deje el abrigo?', 'Te dije que tenías que venir abrigada. ¿Quieres que te deje el abrigo?', NULL, NULL, 'CONVERSATION', NULL),
(4, 'Ejemplo 3', 'FULL', 1, 1, NULL, 1, 0, 'Sabía que te iba a gustar', 'Sabía que te iba a gustar', 'Sabía que te iba a gustar', NULL, NULL, 'END', NULL),
(5, 'Ejemplo 4', 'FULL', 1, 1, NULL, 1, 0, 'Bueno... solo quería ser amable', 'Bueno... solo quería ser amable', 'Bueno... solo quería ser amable', NULL, NULL, 'END', NULL),
(6, 'Ejemplo 5', 'FULL', 1, 1, NULL, 1, 1, 'Las tortas te las doy yo', 'Las tortas te las doy yo', 'Las tortas te las doy yo', NULL, NULL, 'BATTLE', NULL);
-- conversation_options
INSERT IGNORE INTO conversation_options (
    id, conversation_id, text, text_m, text_w, next_event_id, cost
) VALUES
(1, 2, 'No, tengo frío', 'No, tengo frío', 'No, tengo frío', 3, NULL),
(2, 2, 'Sí, se está genial', 'Sí, se está genial', 'Sí, se está genial', 4, NULL),
(3, 3, 'No sé de qué vas, nos acabamos de conocer', 'No sé de qué vas, nos acabamos de conocer', 'No sé de qué vas, nos acabamos de conocer', 5, NULL),
(4, 4, 'Venga, dame el abrigo que te caliento yo a tortas', 'Venga, dame el abrigo que te caliento yo a tortas', 'Venga, dame el abrigo que te caliento yo a tortas', 6, NULL);
