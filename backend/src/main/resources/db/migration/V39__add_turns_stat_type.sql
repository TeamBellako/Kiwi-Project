-- ============================================================================
-- TURNS stat-type for action-economy modifications.
-- Adds TURNS to the stat_affected enum on every table that references StatType,
-- and adds per-actor pending turns counters on the combats row.
-- ============================================================================

DROP PROCEDURE IF EXISTS migrate_v39;

DELIMITER $$

CREATE PROCEDURE migrate_v39()
BEGIN

    -- skill_effects.stat_affected
    ALTER TABLE skill_effects
    CHANGE COLUMN stat_affected stat_affected ENUM(
        'CURRENT_HP',
        'MAX_HP',
        'PATK',
        'MATK',
        'PDEF',
        'MDEF',
        'ACC',
        'EVA',
        'LCK',
        'SHIELD',
        'TURNS'
    ) NULL DEFAULT NULL;

    -- combat_active_status.stat_affected
    ALTER TABLE combat_active_status
    CHANGE COLUMN stat_affected stat_affected ENUM(
        'CURRENT_HP',
        'MAX_HP',
        'PATK',
        'MATK',
        'PDEF',
        'MDEF',
        'ACC',
        'EVA',
        'LCK',
        'SHIELD',
        'TURNS'
    ) NULL DEFAULT NULL;

    -- combat_log.stat_affected
    ALTER TABLE combat_log
    CHANGE COLUMN stat_affected stat_affected ENUM(
        'CURRENT_HP',
        'MAX_HP',
        'PATK',
        'MATK',
        'PDEF',
        'MDEF',
        'ACC',
        'EVA',
        'LCK',
        'SHIELD',
        'TURNS'
    ) NULL DEFAULT NULL;

    -- combats.user_turns / enemy_turns
    IF NOT EXISTS (
        SELECT 1
        FROM INFORMATION_SCHEMA.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'combats'
          AND COLUMN_NAME = 'user_turns'
    ) THEN
        ALTER TABLE combats
        ADD COLUMN user_turns INT NOT NULL DEFAULT 0 AFTER user_lck;
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM INFORMATION_SCHEMA.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'combats'
          AND COLUMN_NAME = 'enemy_turns'
    ) THEN
        ALTER TABLE combats
        ADD COLUMN enemy_turns INT NOT NULL DEFAULT 0 AFTER enemy_lck;
    END IF;

    -- combat_log.action_type — add ACTOR_SKIPPED_BY_TURNS to existing set (per V28).
    ALTER TABLE combat_log
    MODIFY COLUMN action_type ENUM(
        'SKILL_USED',
        'ACTOR_BLOCKED_BY_STATE',
        'SKILL_REPEAT_BY_STATE',
        'ACTOR_DAMAGED_BY_STATE',
        'BLOCKED_SKILLS_BY_STATE',
        'RELEASED_SKILLS_BY_STATE',
        'SKIP',
        'STATUS_TURN_REDUCED',
        'STATUS_FINISHED',
        'ACTOR_SKIPPED_BY_TURNS',
        'TIMEOUT',
        'ABANDON'
    ) NOT NULL;

END$$

DELIMITER ;

CALL migrate_v39();
DROP PROCEDURE migrate_v39;
