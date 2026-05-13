-- ============================================================================
-- FAKE ATTACK SKILL
-- A second placeholder attack (1 damage) gated by its own cooldown goal.
-- Goal 101 mirrors goal 100 from V29 but for the fake attack.
-- Skill 1001 is granted to every build alongside the main Attack (1000).
-- ============================================================================
INSERT IGNORE INTO goals
    (id, name, action, target, type, category, reward, on_completed_action, on_completed_entity, on_completed_entity_id) VALUES
(101, 'Fake attack cooldown', 'Placeholder goal to gate the Fake Attack skill cooldown', 1, 'EXERCISE', 'SKILL', 0, '', '', 0);

INSERT IGNORE INTO skills
    (id, type, name, description, cooldown_type, cooldown_goal_id, cooldown_time_minutes, cooldown_other_description, levelup_skill_id) VALUES
(1001, 'MOTIVATION', 'Fake Attack', 'Deals 1 physical damage.', 'GOAL', 101, NULL, NULL, NULL);

INSERT IGNORE INTO skill_effects
    (skill_id, target,    effect_type, stat_affected, stat_modification, power, attack_type, element_id, hit_chance, state_id, status_duration) VALUES
(1001,        'OPPONENT', 'DAMAGE',    NULL,          NULL,              1.0,   'PHYSICAL',  5,          100,        NULL,     NULL);
