INSERT IGNORE INTO skills (
    name,
    description,
    quote,
    icon,
    cooldown_type,
    cooldown_goal_id,
    cooldown_time_minutes,
    cooldown_other_description,
    levelup_skill_id
)
VALUES (
    'Fireball',
    'A powerful fire attack',
    'Let the flames consume you',
    1,
    'TIME',
    NULL,
    5,
    '',
    NULL
);