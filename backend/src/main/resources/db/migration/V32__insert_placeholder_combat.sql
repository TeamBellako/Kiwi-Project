-- Placeholder rows so a full combat can be started and played end-to-end.
-- Depends on V27 (combat schema), V28 (build skills), V29 (combat_elements),
-- and V31 (combat_configs.on_completed_* columns).

-- ============================================================================
-- COMBAT STATES
-- IDs must match com.kiwi.features.combat.data.enums.CombatStateTypes
-- (BURN=1, POISON=2, FREEZE=3, CONFUSION=4, MUTIS=5)
-- ============================================================================
INSERT IGNORE INTO combat_states (id, name, icon, description) VALUES
(1, 'BURN',      1, 'Damage over time from fire.'),
(2, 'POISON',    2, 'Damage over time from toxin.'),
(3, 'FREEZE',    3, 'The target cannot act while frozen.'),
(4, 'CONFUSION', 4, 'The target may use a random skill.'),
(5, 'MUTIS',     5, 'The target cannot use magical skills.');

-- ============================================================================
-- ENEMY
-- ============================================================================
INSERT IGNORE INTO enemies (id, name, sprite, max_hp, patk, matk, pdef, mdef, acc, eva, lck) VALUES
(1, 'Training Dummy', 'training_dummy', 100, 6, 6, 5, 5, 6, 4, 4);

-- Neutral elemental profile (1.0 for every element).
INSERT IGNORE INTO enemy_elemental_multipliers (enemy_id, element_id, multiplier) VALUES
(1, 1, 1.0),
(1, 2, 1.0),
(1, 3, 1.0),
(1, 4, 1.0),
(1, 5, 1.0),
(1, 6, 1.0);

-- No status resistances by default.
INSERT IGNORE INTO enemy_status_resistances (enemy_id, state_id, resistance) VALUES
(1, 1, 0.0),
(1, 2, 0.0),
(1, 3, 0.0),
(1, 4, 0.0),
(1, 5, 0.0);

-- Skills the enemy can pick from each turn.
INSERT IGNORE INTO enemy_skill (enemy_id, skill_id) VALUES
(1, 1080), -- Overdrive (physical damage)
(1, 2020); -- Insight Strike (magical damage)

-- ============================================================================
-- SKILL EFFECTS
-- Minimal effect rows so the build skills from V28 actually do something
-- in combat. Skills not listed here are traits/complex behaviours handled
-- elsewhere and intentionally have no effect rows.
-- ============================================================================
INSERT IGNORE INTO skill_effects
    (skill_id, target,   effect_type,    stat_affected, stat_modification, power, attack_type, element_id, hit_chance, state_id, status_duration) VALUES
-- 1070 Adrenaline Rush: SELF +PATK x2 for 2 turns
    (1070,    'SELF',    'MODIFY_STAT',  'PATK',        'MUL',             2.0,   NULL,        NULL,       NULL,       NULL,     2),
-- 1080 Overdrive: 80 physical damage
    (1080,    'OPPONENT','DAMAGE',       NULL,          NULL,              80.0,  'PHYSICAL',  5,          95,         NULL,     NULL),
-- 2010 Tunnel Vision: SELF +PATK and +MATK for 3 turns
    (2010,    'SELF',    'MODIFY_STAT',  'PATK',        'MUL',             1.5,   NULL,        NULL,       NULL,       NULL,     3),
    (2010,    'SELF',    'MODIFY_STAT',  'MATK',        'MUL',             1.5,   NULL,        NULL,       NULL,       NULL,     3),
-- 2020 Insight Strike: 30 magical damage
    (2020,    'OPPONENT','DAMAGE',       NULL,          NULL,              30.0,  'MAGICAL',   4,          95,         NULL,     NULL),
-- 2040 Clean Execution: 50 magical damage
    (2040,    'OPPONENT','DAMAGE',       NULL,          NULL,              50.0,  'MAGICAL',   4,          90,         NULL,     NULL),
-- 3010 Iron Wall: SELF +PDEF for 3 turns
    (3010,    'SELF',    'MODIFY_STAT',  'PDEF',        'MUL',             1.5,   NULL,        NULL,       NULL,       NULL,     3),
-- 4020 Supportive Aura: SELF +MDEF for 3 turns
    (4020,    'SELF',    'MODIFY_STAT',  'MDEF',        'MUL',             1.5,   NULL,        NULL,       NULL,       NULL,     3),
-- 4030 Cleanse: minor damage to opponent
    (4030,    'OPPONENT','DAMAGE',       NULL,          NULL,              20.0,  'MAGICAL',   3,          100,        NULL,     NULL),
-- 6010 Mind Bind: applies FREEZE for 3 turns
    (6010,    'OPPONENT','APPLY_STATUS', NULL,          NULL,              NULL,  NULL,        2,          85,         3,        3),
-- 6020 Confuse: applies CONFUSION for 3 turns
    (6020,    'OPPONENT','APPLY_STATUS', NULL,          NULL,              NULL,  NULL,        2,          85,         4,        3);

-- ============================================================================
-- COMBAT CONFIG
-- Used by POST /api/combat/start/{combatConfigId}.
-- ============================================================================
INSERT IGNORE INTO combat_configs
    (id, enemy_id, time_limit, on_completed_action, on_completed_entity, on_completed_entity_id) VALUES
(1, 1, 30, '', '', 0);
