-- Module connection, background, and sfx columns on combat_configs.
-- combat_configs has no rows yet (created in V27, populated in V29) so no backfill is needed.
DROP PROCEDURE IF EXISTS migrate_v28;

DELIMITER $$

CREATE PROCEDURE migrate_v28()
BEGIN
    IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE TABLE_SCHEMA = DATABASE()
                     AND TABLE_NAME = 'combat_configs'
                     AND COLUMN_NAME = 'on_completed_action') THEN
        ALTER TABLE combat_configs ADD COLUMN on_completed_action VARCHAR(255);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE TABLE_SCHEMA = DATABASE()
                     AND TABLE_NAME = 'combat_configs'
                     AND COLUMN_NAME = 'on_completed_entity') THEN
        ALTER TABLE combat_configs ADD COLUMN on_completed_entity VARCHAR(255);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE TABLE_SCHEMA = DATABASE()
                     AND TABLE_NAME = 'combat_configs'
                     AND COLUMN_NAME = 'on_completed_entity_id') THEN
        ALTER TABLE combat_configs ADD COLUMN on_completed_entity_id INT;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE TABLE_SCHEMA = DATABASE()
                     AND TABLE_NAME = 'combat_configs'
                     AND COLUMN_NAME = 'background') THEN
        ALTER TABLE combat_configs ADD COLUMN background BIGINT NULL;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE TABLE_SCHEMA = DATABASE()
                     AND TABLE_NAME = 'combat_configs'
                     AND COLUMN_NAME = 'sfx') THEN
        ALTER TABLE combat_configs ADD COLUMN sfx BIGINT NULL;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS
                   WHERE TABLE_SCHEMA = DATABASE()
                     AND TABLE_NAME = 'combat_configs'
                     AND CONSTRAINT_NAME = 'fk_combat_configs_background') THEN
        ALTER TABLE combat_configs
            ADD CONSTRAINT fk_combat_configs_background
                FOREIGN KEY (background)
                REFERENCES backgrounds(id)
                ON DELETE SET NULL
                ON UPDATE CASCADE;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS
                   WHERE TABLE_SCHEMA = DATABASE()
                     AND TABLE_NAME = 'combat_configs'
                     AND CONSTRAINT_NAME = 'fk_combat_configs_sfx') THEN
        ALTER TABLE combat_configs
            ADD CONSTRAINT fk_combat_configs_sfx
                FOREIGN KEY (sfx)
                REFERENCES fx(id)
                ON DELETE SET NULL
                ON UPDATE CASCADE;
    END IF;

    -- Re-point skills.cooldown_goal_id at the goal definition table.
    -- V12 originally wired this FK to user_goal_status(id), which transitively
    -- requires a user row to exist before any skill cooldown can reference a
    -- goal. That made it impossible to seed a goal-based cooldown in migrations.
    -- We drop and re-add the FK against goals(id) instead; the column already
    -- carries the right values for the new target.
    IF EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.REFERENTIAL_CONSTRAINTS
               WHERE CONSTRAINT_SCHEMA = DATABASE()
                 AND TABLE_NAME = 'skills'
                 AND CONSTRAINT_NAME = 'fk_skills_goal'
                 AND REFERENCED_TABLE_NAME = 'user_goal_status') THEN
        ALTER TABLE skills DROP FOREIGN KEY fk_skills_goal;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS
                   WHERE TABLE_SCHEMA = DATABASE()
                     AND TABLE_NAME = 'skills'
                     AND CONSTRAINT_NAME = 'fk_skills_goal') THEN
        ALTER TABLE skills
            ADD CONSTRAINT fk_skills_goal
                FOREIGN KEY (cooldown_goal_id)
                REFERENCES goals(id)
                ON DELETE SET NULL
                ON UPDATE CASCADE;
    END IF;
END$$

DELIMITER ;

CALL migrate_v28();
DROP PROCEDURE migrate_v28;

-- Align combat_log enums with the values produced by the engine.
-- MODIFY COLUMN to the same definition is a no-op so this is safe to re-run.
ALTER TABLE combat_log
    MODIFY COLUMN target ENUM('USER','ENEMY','ALLY') NULL,
    MODIFY COLUMN actor  ENUM('USER','ENEMY','ALLY') NOT NULL,
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
        'TIMEOUT',
        'ABANDON'
    ) NOT NULL;
