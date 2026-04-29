-- ============================================================================
-- COMBAT ELEMENTS
-- ============================================================================
INSERT IGNORE INTO combat_elements (id, name, icon, description) VALUES
(1, 'Adaptability', 1, ''),
(2, 'Control',      2, ''),
(3, 'Empathy',      3, ''),
(4, 'Focus',        4, ''),
(5, 'Motivation',   5, ''),
(6, 'Resilience',   6, '');

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

-- Rename the placeholder background to the combat-specific asset name.
UPDATE backgrounds SET name = 'background_mindveil' WHERE id = 1;

-- ============================================================================
-- COOLDOWN GOAL FOR THE PLACEHOLDER SKILL
-- IDs 1-5 are taken by the rows V12 migrated out of suggested_goals, so we
-- use a high id to avoid colliding with that range. The module-connection
-- columns on goals are added later (V31) and backfilled there.
-- ============================================================================
INSERT IGNORE INTO goals (id, name, action, target, type, category, reward) VALUES
(100, 'Attack cooldown', 'Placeholder goal to gate the Attack skill cooldown', 1, 'EXERCISE', 'SKILL', 0);

-- ============================================================================
-- SKILLS
-- A single 40-damage attack used by every build, plus the dummy enemy's slap.
-- Skill 1000's cooldown is gated by goal 100 (FK was repointed at goals in V28).
-- ============================================================================
INSERT IGNORE INTO skills
    (id, type, name, description, cooldown_type, cooldown_goal_id, cooldown_time_minutes, cooldown_other_description, levelup_skill_id) VALUES
(1000, 'MOTIVATION', 'Attack',     'Deals 40 physical damage.', 'GOAL',  100,  NULL, NULL, NULL),
(2000, 'MOTIVATION', 'Dummy Slap', 'Deals 10 physical damage.', 'OTHER', NULL, NULL, NULL, NULL);

-- Place the Attack skill in the test user's deck so it is usable without going
-- through build initialization. INSERT IGNORE silently no-ops if user 19 has
-- not been created yet (FK violations are downgraded to warnings).
INSERT IGNORE INTO user_skill_status (user_id, skill_id, is_cooldown, cooldown_until, deck_slot) VALUES
(19, 1000, FALSE, NULL, 1);

INSERT IGNORE INTO skill_effects
    (skill_id, target,    effect_type, stat_affected, stat_modification, power, attack_type, element_id, hit_chance, state_id, status_duration) VALUES
(1000,        'OPPONENT', 'DAMAGE',    NULL,          NULL,              40.0,  'PHYSICAL',  5,          100,        NULL,     NULL),
(2000,        'OPPONENT', 'DAMAGE',    NULL,          NULL,              10.0,  'PHYSICAL',  5,          100,        NULL,     NULL);

-- ============================================================================
-- TRAINING DUMMY ENEMY
-- ============================================================================
INSERT IGNORE INTO enemies (id, name, sprite, max_hp, patk, matk, pdef, mdef, acc, eva, lck) VALUES
(1, 'Training Dummy', 'training_dummy', 100, 6, 6, 5, 5, 6, 4, 4);

INSERT IGNORE INTO enemy_elemental_multipliers (enemy_id, element_id, multiplier) VALUES
(1, 1, 1.0),
(1, 2, 1.0),
(1, 3, 1.0),
(1, 4, 1.0),
(1, 5, 1.0),
(1, 6, 1.0);

INSERT IGNORE INTO enemy_status_resistances (enemy_id, state_id, resistance) VALUES
(1, 1, 0.0),
(1, 2, 0.0),
(1, 3, 0.0),
(1, 4, 0.0),
(1, 5, 0.0);

INSERT IGNORE INTO enemy_skill (enemy_id, skill_id) VALUES
(1, 2000);

-- ============================================================================
-- PLACEHOLDER COMBAT CONFIG
-- Used by POST /api/combat/start/{combatConfigId}.
-- ============================================================================
INSERT IGNORE INTO combat_configs
    (id, enemy_id, time_limit, on_completed_action, on_completed_entity, on_completed_entity_id, background, sfx) VALUES
(1, 1, 30, '', '', 0, 1, 1);
