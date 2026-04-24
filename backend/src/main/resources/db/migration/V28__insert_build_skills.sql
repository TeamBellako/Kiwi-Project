INSERT INTO skills (id, type, name, description, cooldown_type, cooldown_goal_id, cooldown_time_minutes, cooldown_other_description, levelup_skill_id) VALUES
-- Berserker (Motivation)
(1000, 'MOTIVATION',   'Battle Cry',        'Berserker exclusive trait. Attack becomes increasingly powerful (10% each turn; max 200%) as long as offensive abilities are used in a streak.', 'OTHER', NULL, NULL, NULL, NULL),
(1070, 'MOTIVATION',   'Adrenaline Rush',   '+PATK 100% (2 turns)',                                                                                                                           'OTHER', NULL, NULL, NULL, NULL),
(1080, 'MOTIVATION',   'Overdrive',         'Deal 80 damage points (VIT).',                                                                                                                   'OTHER', NULL, NULL, NULL, NULL),

-- Focus (shared across builds)
(2010, 'FOCUS',        'Tunnel Vision',     'Gain PATK UP and MATK UP (3 turns)',                                                                                                             'OTHER', NULL, NULL, NULL, NULL),
(2020, 'FOCUS',        'Insight Strike',    'Deal 30 magic damage +20 damage per debuff on target',                                                                                           'OTHER', NULL, NULL, NULL, NULL),
(2040, 'FOCUS',        'Clean Execution',   'Deal 50 magic damage. If target has no buffs, deal +30% damage',                                                                                 'OTHER', NULL, NULL, NULL, NULL),

-- Resilience (Berserker)
(3010, 'RESILIENCE',   'Iron Wall',         '+ PDEF buff (3 turns)',                                                                                                                          'OTHER', NULL, NULL, NULL, NULL),
(3030, 'RESILIENCE',   'Sturdy Resolve',    'Receive any debuff causes an accumulative PDEF UP',                                                                                              'OTHER', NULL, NULL, NULL, NULL),

-- Empathy (Monk)
(4000, 'EMPATHY',      'Quiet Restoration', 'Monk exclusive trait. Heals a small amount of HP each turn (10% HP) while no direct attacks are made.',                                         'OTHER', NULL, NULL, NULL, NULL),
(4020, 'EMPATHY',      'Supportive Aura',   '+ MDEF buff (3 turns)',                                                                                                                          'OTHER', NULL, NULL, NULL, NULL),
(4030, 'EMPATHY',      'Cleanse',           'Consumes 1 turn of debuffs. Deal minor damage to enemies per debuff cleansed (20 per debuff)',                                                   'OTHER', NULL, NULL, NULL, NULL),

-- Adaptability (Shaman)
(5000, 'ADAPTABILITY', 'Tactical Thinking', 'Shaman exclusive trait. Store your current action for double the next turn.',                                                                    'OTHER', NULL, NULL, NULL, NULL),
(5010, 'ADAPTABILITY', 'Elemental Shift',   'Switch next attack to a random, different element',                                                                                              'OTHER', NULL, NULL, NULL, NULL),
(5030, 'ADAPTABILITY', 'Cooldown Reset',    'Reset cooldown of all skills',                                                                                                                   'OTHER', NULL, NULL, NULL, NULL),

-- Control (Monk)
(6010, 'CONTROL',      'Mind Bind',         'Applies @FREEZE to an enemy (+3 turns)',                                                                                                         'OTHER', NULL, NULL, NULL, NULL),
(6020, 'CONTROL',      'Confuse',           'Applies @CONFUSION to an enemy (+3 turns)',                                                                                                      'OTHER', NULL, NULL, NULL, NULL);
