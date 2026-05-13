DROP PROCEDURE IF EXISTS migrate_v33;

DELIMITER $$

CREATE PROCEDURE migrate_v33()
BEGIN

    -- skills
    IF EXISTS (
        SELECT 1
        FROM INFORMATION_SCHEMA.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'skills'
          AND COLUMN_NAME = 'type'
    ) THEN
        ALTER TABLE skills
        DROP COLUMN type;
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM INFORMATION_SCHEMA.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'skills'
          AND COLUMN_NAME = 'element_id'
    ) THEN
        ALTER TABLE skills
        ADD COLUMN element_id BIGINT NOT NULL DEFAULT 1 AFTER levelup_skill_id;
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM INFORMATION_SCHEMA.STATISTICS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'skills'
          AND INDEX_NAME = 'fk_skills_elements_idx'
    ) THEN
        ALTER TABLE skills
        ADD INDEX fk_skills_elements_idx (element_id ASC) VISIBLE;
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'skills'
          AND CONSTRAINT_NAME = 'fk_skills_elements'
    ) THEN
        ALTER TABLE skills
        ADD CONSTRAINT fk_skills_elements
            FOREIGN KEY (element_id)
            REFERENCES combat_elements(id)
            ON DELETE CASCADE
            ON UPDATE CASCADE;
    END IF;

    -- skill_effects
    IF EXISTS (
        SELECT 1
        FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'skill_effects'
          AND CONSTRAINT_NAME = 'fk_skill_effects_elements'
    ) THEN
        ALTER TABLE skill_effects
        DROP FOREIGN KEY fk_skill_effects_elements;
    END IF;

    IF EXISTS (
        SELECT 1
        FROM INFORMATION_SCHEMA.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'skill_effects'
          AND COLUMN_NAME = 'element_id'
    ) THEN
        ALTER TABLE skill_effects
        DROP COLUMN element_id;
    END IF;

    IF EXISTS (
        SELECT 1
        FROM INFORMATION_SCHEMA.STATISTICS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'skill_effects'
          AND INDEX_NAME = 'fk_skill_effects_elements'
    ) THEN
        ALTER TABLE skill_effects
        DROP INDEX fk_skill_effects_elements;
    END IF;

    ALTER TABLE skill_effects
    CHANGE COLUMN effect_type effect_type ENUM(
        'DAMAGE',
        'HEAL',
        'APPLY_STATUS',
        'MODIFY_STAT',
        'CONSUME_STATUS',
        'COPY_BUFFS',
        'SWAP_BUFFS',
        'EXTEND_BUFFS',
        'RESET_COOLDOWNS'
    ) NOT NULL;

    IF NOT EXISTS (
        SELECT 1
        FROM INFORMATION_SCHEMA.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'skill_effects'
          AND COLUMN_NAME = 'turns'
    ) THEN
        ALTER TABLE skill_effects
        ADD COLUMN turns INT NULL DEFAULT NULL AFTER status_duration;
    END IF;

    -- combat_active_status
    IF NOT EXISTS (
        SELECT 1
        FROM INFORMATION_SCHEMA.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'combat_active_status'
          AND COLUMN_NAME = 'stat_affected'
    ) THEN
        ALTER TABLE combat_active_status
        ADD COLUMN stat_affected ENUM(
            'CURRENT_HP',
            'MAX_HP',
            'PATK',
            'MATK',
            'PDEF',
            'MDEF',
            'ACC',
            'EVA',
            'LCK'
        ) NULL DEFAULT NULL AFTER remaining_turns;
    END IF;

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
        'SHIELD'
    ) NULL DEFAULT NULL;

    -- combat_log
    ALTER TABLE combat_log
    CHANGE COLUMN effect_type effect_type ENUM(
        'DAMAGE',
        'MISS_DAMAGE',
        'HEAL',
        'MODIFY_STAT',
        'STATUS_APPLIED',
        'STATUS_REMOVED',
        'STATUS_CONSUMED',
        'STATUS_EXTENDED',
        'MISS_STATUS',
        'BUFFS_COPIED',
        'BUFFS_SWAPPED',
        'RESET_COOLDOWNS',
        'IMMUNE'
    ) NULL DEFAULT NULL;

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
        'SHIELD'
    ) NULL DEFAULT NULL;

    IF NOT EXISTS (
        SELECT 1
        FROM INFORMATION_SCHEMA.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'combat_log'
          AND COLUMN_NAME = 'reset_cooldown_skills'
    ) THEN
        ALTER TABLE combat_log
        ADD COLUMN reset_cooldown_skills VARCHAR(255) NULL DEFAULT NULL AFTER blocked_skills;
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM INFORMATION_SCHEMA.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'combat_log'
          AND COLUMN_NAME = 'turns'
    ) THEN
        ALTER TABLE combat_log
        ADD COLUMN turns INT NULL DEFAULT NULL AFTER reset_cooldown_skills;
    END IF;

    -- user_stats
    IF NOT EXISTS (
        SELECT 1
        FROM INFORMATION_SCHEMA.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'user_stats'
          AND COLUMN_NAME = 'shield'
    ) THEN
        ALTER TABLE user_stats
        ADD COLUMN shield INT NOT NULL DEFAULT 0 AFTER max_hp;
    END IF;

    -- enemies
    IF NOT EXISTS (
        SELECT 1
        FROM INFORMATION_SCHEMA.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'enemies'
          AND COLUMN_NAME = 'shield'
    ) THEN
        ALTER TABLE enemies
        ADD COLUMN shield INT NOT NULL DEFAULT 0 AFTER max_hp;
    END IF;

    -- combats
    IF NOT EXISTS (
        SELECT 1
        FROM INFORMATION_SCHEMA.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'combats'
          AND COLUMN_NAME = 'user_shield'
    ) THEN
        ALTER TABLE combats
        ADD COLUMN user_shield INT NOT NULL DEFAULT 0 AFTER user_max_hp;
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM INFORMATION_SCHEMA.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'combats'
          AND COLUMN_NAME = 'enemy_shield'
    ) THEN
        ALTER TABLE combats
        ADD COLUMN enemy_shield INT NOT NULL DEFAULT 0 AFTER enemy_max_hp;
    END IF;

END$$

DELIMITER ;

CALL migrate_v33();
DROP PROCEDURE migrate_v33;