-- V43__app_usage_goals.sql

ALTER TABLE user_goal_status
    ADD COLUMN target_override INT DEFAULT NULL;

-- IDs 200-204: APP_USAGE_GOOD difficulty 1..5
-- IDs 210-214: APP_USAGE_BAD  difficulty 1..5
-- Reward scales with difficulty; targets are 0 (computed per-user at runtime).
INSERT IGNORE INTO goals (id, name, action, target, type, category, reward, difficulty,
                          on_completed_action, on_completed_entity, on_completed_entity_id)
VALUES
    (200, 'Good Apps – Level 1', 'Use your good apps for at least the daily target',  0, 'APP_USAGE_GOOD', 'APP_USAGE', 100, 1, '', '', 0),
    (201, 'Good Apps – Level 2', 'Use your good apps for at least the daily target',  0, 'APP_USAGE_GOOD', 'APP_USAGE', 120, 2, '', '', 0),
    (202, 'Good Apps – Level 3', 'Use your good apps for at least the daily target',  0, 'APP_USAGE_GOOD', 'APP_USAGE', 140, 3, '', '', 0),
    (203, 'Good Apps – Level 4', 'Use your good apps for at least the daily target',  0, 'APP_USAGE_GOOD', 'APP_USAGE', 160, 4, '', '', 0),
    (204, 'Good Apps – Level 5', 'Use your good apps for at least the daily target',  0, 'APP_USAGE_GOOD', 'APP_USAGE', 180, 5, '', '', 0),

    (210, 'Bad Apps – Level 1',  'Keep your bad apps usage below the daily target',   0, 'APP_USAGE_BAD',  'APP_USAGE', 100, 1, '', '', 0),
    (211, 'Bad Apps – Level 2',  'Keep your bad apps usage below the daily target',   0, 'APP_USAGE_BAD',  'APP_USAGE', 120, 2, '', '', 0),
    (212, 'Bad Apps – Level 3',  'Keep your bad apps usage below the daily target',   0, 'APP_USAGE_BAD',  'APP_USAGE', 140, 3, '', '', 0),
    (213, 'Bad Apps – Level 4',  'Keep your bad apps usage below the daily target',   0, 'APP_USAGE_BAD',  'APP_USAGE', 160, 4, '', '', 0),
    (214, 'Bad Apps – Level 5',  'Keep your bad apps usage below the daily target',   0, 'APP_USAGE_BAD',  'APP_USAGE', 180, 5, '', '', 0);
