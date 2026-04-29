-- Simplifies the placeholder combat (id 1) for easier end-to-end testing:
-- and Insight Strike's cooldown is moved to a goal-based cooldown.

-- ============================================================================
-- ENEMY SKILL: a single skill that deals a flat 10 damage to the player.
-- We add a dedicated, enemy-only skill so the player-facing build skills are
-- left untouched.
-- ============================================================================
INSERT IGNORE INTO skills
    (id, type, name, description, cooldown_type, cooldown_goal_id, cooldown_time_minutes, cooldown_other_description, levelup_skill_id) VALUES
(9000, 'MOTIVATION', 'Dummy Slap', 'Deals 10 physical damage.', 'OTHER', NULL, NULL, '', NULL);

INSERT IGNORE INTO skill_effects
    (skill_id, target,    effect_type, stat_affected, stat_modification, power, attack_type, element_id, hit_chance, state_id, status_duration) VALUES
(9000,        'OPPONENT', 'DAMAGE',    NULL,          NULL,              10.0,  'PHYSICAL',  5,          100,        NULL,     NULL);

DELETE FROM enemy_skill WHERE enemy_id = 1;

INSERT IGNORE INTO enemy_skill (enemy_id, skill_id) VALUES
(1, 9000);

-- ============================================================================
-- INSIGHT STRIKE COOLDOWN GOAL
-- skills.cooldown_goal_id references user_goal_status(id) (see V12), so we
-- need both a goal definition AND a matching per-user status row for the FK
-- to be satisfied. Test user is id 17.
-- ============================================================================
INSERT IGNORE INTO goals
    (id, name, action, target, type, category, reward, on_completed_action, on_completed_entity, on_completed_entity_id) VALUES
(4, 'Insight Strike cooldown', 'Placeholder goal to gate Insight Strike cooldown', 1, 'EXERCISE', 'SKILL', 0, '', '', 0);

INSERT IGNORE INTO user_goal_status (id, user_id, goal_id, status, date, value) VALUES
(21, 19, 4, 'NOT_COMPLETED', CURDATE(), 0);

UPDATE skills
SET cooldown_type = 'GOAL',
    cooldown_goal_id = 4,
    cooldown_time_minutes = NULL,
    cooldown_other_description = NULL
WHERE id = 2020;
