-- Placeholder bark configuration for combat_configs.id = 1 (the seeded
-- Flicker fight from V29). Five triggers: combat-start, victory, defeat,
-- elapsed-time, and a low-HP threshold. Each trigger points at a
-- ConversationPersistence row (created here with IDs 1000-1004 to stay
-- clear of the existing low-id range).
--
-- Idempotent: INSERT IGNORE on explicit PKs makes a re-run a no-op.
-- combat_bark_triggers ids are also fixed (1-5) so re-running does not
-- duplicate triggers.
--
-- Notes:
--  * Enemy 'Flicker' has max_hp = 100 (V29), so the "enemy HP reaches 40
--    points" requirement is expressed as ENEMY_HP_PERCENT threshold=40.
--  * threshold=100 on ENEMY_HP_PERCENT fires immediately on combat start
--    (per the mobile controller's special case).
--  * threshold=0 fires when the actor's HP crosses to zero, which is the
--    victory/defeat moment.
--  * dialog/dialog_m/dialog_w are required NOT NULL — same text in all
--    three since these barks aren't gendered.
--  * sprite is unused by the bark bubble (it renders text-only over the
--    enemy sprite already on-screen) but the column is NOT NULL.

INSERT IGNORE INTO conversations (
    id, name, type, sprite, dark,
    dialog, dialog_m, dialog_w,
    delay_start_ms, delay_end_ms,
    next_event, event_id,
    on_completed_action, on_completed_entity, on_completed_entity_id
) VALUES
(1000, 'Bark: Flicker intro',     'SMALL', 'enemy_flicker_base', 0,
 '¿QUIEN ES EL MENSAJERO DE DIOS?',
 '¿QUIEN ES EL MENSAJERO DE DIOS?',
 '¿QUIEN ES EL MENSAJERO DE DIOS?',
 NULL, 3000, 'END', NULL, "", "", 0),

(1001, 'Bark: Flicker victory',   'SMALL', 'enemy_flicker_base', 0,
 'Estoy cansado jefe...',
 'Estoy cansado jefe...',
 'Estoy cansado jefe...',
 NULL, 3000, 'END', NULL, "", "", 0),

(1002, 'Bark: Flicker defeat',    'SMALL', 'enemy_flicker_base', 0,
 'No metais cosas de esas, porfa',
 'No metais cosas de esas, porfa',
 'No metais cosas de esas, porfa',
 NULL, 3000, 'END', NULL, "", "", 0),

(1003, 'Bark: Flicker 15s',       'SMALL', 'enemy_flicker_base', 0,
 '¿Y QUIEN ES EL MENSAJERO DEL MENSAJERO?',
 '¿Y QUIEN ES EL MENSAJERO DEL MENSAJERO?',
 '¿Y QUIEN ES EL MENSAJERO DEL MENSAJERO?',
 NULL, 3000, 'END', NULL, "", "", 0),

(1004, 'Bark: Flicker low HP',    'SMALL', 'enemy_flicker_base', 0,
 'O CORTAMOS ESTO O INCREMENTAMOS LA PANDEMIA',
 'O CORTAMOS ESTO O INCREMENTAMOS LA PANDEMIA',
 'O CORTAMOS ESTO O INCREMENTAMOS LA PANDEMIA',
 NULL, 3000, 'END', NULL, "", "", 0);

INSERT IGNORE INTO combat_bark_triggers (
    id, combat_config_id, type, threshold, skill_id, conversation_id, dismiss_mode, priority
) VALUES
-- Combat start (enemy at full HP).
(1, 1, 'ENEMY_HP_PERCENT',         100, NULL, 1000, 'AUTO', 0),
-- Victory (enemy crosses to 0%).
(2, 1, 'ENEMY_HP_PERCENT',           0, NULL, 1001, 'AUTO', 0),
-- Defeat (player crosses to 0%).
(3, 1, 'PLAYER_HP_PERCENT',          0, NULL, 1002, 'AUTO', 0),
-- 15 seconds elapsed since combat started.
(4, 1, 'COMBAT_ELAPSED_SECONDS',    15, NULL, 1003, 'AUTO', 0),
-- Enemy HP reaches 40 points (= 40% of max_hp=100).
(5, 1, 'ENEMY_HP_PERCENT',          40, NULL, 1004, 'CLICK', 0);
