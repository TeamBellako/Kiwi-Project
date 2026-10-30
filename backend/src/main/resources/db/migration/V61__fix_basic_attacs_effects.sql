UPDATE skill_effects
SET target = 'OPPONENT',
    stat_affected = NULL,
    stat_modification = NULL,
    power = CASE skill_id WHEN 1000 THEN 40 WHEN 1001 THEN 1 END,
    attack_type = 'PHYSICAL',
    hit_chance = 100,
    state_id = NULL,
    status_duration = NULL,
    turns = NULL
WHERE skill_id IN (1000, 1001)
  AND effect_type = 'DAMAGE';

INSERT INTO skill_effects
    (skill_id, target, effect_type, stat_affected, stat_modification,
     power, attack_type, hit_chance, state_id, status_duration, turns)
SELECT desired.skill_id, 'OPPONENT', 'DAMAGE', NULL, NULL,
       desired.damage_power, 'PHYSICAL', 100, NULL, NULL, NULL
FROM (
    SELECT 1000 AS skill_id, 40 AS damage_power
    UNION ALL
    SELECT 1001, 1
) AS desired
WHERE NOT EXISTS (
    SELECT 1
    FROM skill_effects existing
    WHERE existing.skill_id = desired.skill_id
      AND existing.effect_type = 'DAMAGE'
);