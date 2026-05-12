-- ============================================================================
-- FAKE TURNS SKILLS
-- Two debug skills used to exercise the TURNS action-economy feature.
-- 1002 "Inner Focus" — SELF, SUM 1 TURNS (next round enemy is skipped).
-- 1003 "Mental Lock" — OPPONENT, SUB 1 TURNS (next round enemy is skipped).
-- Both are GOAL-gated by their own placeholder goals (mirrors V32's pattern).
-- Auto-granted to every new user via BuildType.skillIds.
-- ============================================================================

INSERT IGNORE INTO goals
    (id, name, action, target, type, category, reward, on_completed_action, on_completed_entity, on_completed_entity_id) VALUES
(102, 'Inner Focus cooldown', 'Placeholder goal to gate the Inner Focus skill cooldown', 1, 'EXERCISE', 'SKILL', 0, '', '', 0),
(103, 'Mental Lock cooldown', 'Placeholder goal to gate the Mental Lock skill cooldown', 1, 'EXERCISE', 'SKILL', 0, '', '', 0);

INSERT IGNORE INTO skills
    (id, name, description, cooldown_type, cooldown_goal_id, cooldown_time_minutes, cooldown_other_description, levelup_skill_id, element_id) VALUES
(1002, 'Inner Focus', 'Grants you an extra turn on the next exchange.', 'GOAL', 102, NULL, NULL, NULL, 5),
(1003, 'Mental Lock', 'Skips the opponent''s next turn.',             'GOAL', 103, NULL, NULL, NULL, 5);

INSERT IGNORE INTO skill_effects
    (skill_id, target,    effect_type,    stat_affected, stat_modification, power, attack_type, hit_chance, state_id, status_duration, turns) VALUES
(1002,        'SELF',     'MODIFY_STAT',  'TURNS',       'SUM',             1.0,   NULL,        NULL,       NULL,     NULL,            NULL),
(1003,        'OPPONENT', 'MODIFY_STAT',  'TURNS',       'SUB',             1.0,   NULL,        NULL,       NULL,     NULL,            NULL);
