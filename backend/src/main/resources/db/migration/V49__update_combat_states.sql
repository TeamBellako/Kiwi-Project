-- Seed data for the `combat_states` table.
-- Source: Combat States Notion table (screenshot).
-- Notes:
--   * IDs follow the Notion table numbering (STAT DOWN=1 ... INVINCIBLE=15), NOT the
--     CombatStateTypes enum numbering. This is required so that skill_effects.state_id
--     references resolve correctly (verified against the skill_effects `state` column).
--     ⚠ CombatStateTypes enum IDs are currently out of sync with this data and should be realigned.
--   * `name` uses the Notion "Internal Name" column (identifier-style), per request.
--   * `icon` equals the row ID for every state, as shown in the source table.
--   * ⚠ POISON (id 6) description was truncated in the source; completed as
--     "...at the start of each turn". Verify/replace if the intended wording differs.
-- Idempotent: INSERT ... ON DUPLICATE KEY UPDATE updates existing rows (matched by the
-- primary key `id` or the unique `name`) instead of failing with a duplicate-key error.

INSERT INTO combat_states (id, name, icon, description) VALUES
    (1,  'STAT DOWN',  1,  'Reduces a stat during 3 turns (0.5)'),
    (2,  'BUFFBLOCK',    2,  'Cannot gain positive stat modifiers'),
    (3,  'STAT UP',    3,  'Amplifies a stat during 3 turns (1.5)'),
    (4,  'FREEZE',       4,  '80 - 100% chance to do nothing during 1 turn'),
    (5,  'CONFUSION',    5,  '30 - 50% chance to automatically repeat previous action'),
    (6,  'POISON',       6,  'Takes minor Vitality damage (5% Max Health TBC) at the start of each turn'),
    (7,  'MUTIS',        7,  'Cannot use two random skills of the deck during 2 turns'),
    (8,  'REVERSION',    8,  'Reverts positive stat modifiers'),
    (9,  'LOOP',         9,  'Forced to repeat previous action'),
    (10, 'BOND',         10, 'Damage dealt to self also applies to enemy (0.5)'),
    (11, 'SURVIVE',      11, 'Resist mortal damage with 1 HP'),
    (12, 'FURY',         12, 'Increase PATK by 10% when attacking'),
    (13, 'REGENERATION', 13, 'Heal 10% of MAXHP per turn'),
    (14, 'PRECISION',    14, 'Ignore enemy EVA and resistances and self ACC'),
    (15, 'INVINCIBLE',   15, 'Ignore skill effect')
AS new
ON DUPLICATE KEY UPDATE
    name = new.name,
    icon = new.icon,
    description = new.description;