-- Force every seeded row that has a sprite or background configured to use
-- the canonical default asset key. V33 backfilled these columns from the
-- old FK-linked asset names ('Liria', 'background_mindveil', 'Castel', etc.);
-- this migration normalises them so every existing row points at the same
-- default sprite and background regardless of the legacy id it once had.
--
-- Defaults:
--   sprite     -> 'character_liria_base'
--   background -> 'background_mindveil'
--
-- sfx/fx are left untouched: no canonical default has been chosen for them.
--
-- Idempotent: every UPDATE sets a constant value, so re-runs are no-ops.

-- conversations.sprite is NOT NULL, so every row has a configured sprite.
UPDATE conversations
SET sprite = 'character_liria_base';

-- background is nullable: only rows that already had one configured get the
-- default. NULL stays NULL so future rows can opt out.
UPDATE conversations
SET background = 'background_mindveil'
WHERE background IS NOT NULL;

UPDATE combat_configs
SET background = 'background_mindveil'
WHERE background IS NOT NULL;

-- Reskin the seeded placeholder enemy so it shares Liria's portrait. The
-- enemy was originally named 'Training Dummy' with sprite 'training_dummy'
-- in V29; we keep the row (combat_configs still references id=1) but swap
-- the name and sprite over to the default character.
UPDATE enemies
SET name = 'Flicker',
    sprite = 'enemy_flicker_base'
WHERE id = 1;
