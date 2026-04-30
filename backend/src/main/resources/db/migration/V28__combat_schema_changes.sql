-- Module connection, background, and sfx columns on combat_configs.
-- combat_configs has no rows yet (created in V27, populated in V29) so no backfill is needed.
ALTER TABLE combat_configs
    ADD COLUMN on_completed_action VARCHAR(255),
    ADD COLUMN on_completed_entity VARCHAR(255),
    ADD COLUMN on_completed_entity_id INT,
    ADD COLUMN background BIGINT NULL,
    ADD COLUMN sfx BIGINT NULL,
    ADD CONSTRAINT fk_combat_configs_background
        FOREIGN KEY (background)
        REFERENCES backgrounds(id)
        ON DELETE SET NULL
        ON UPDATE CASCADE,
    ADD CONSTRAINT fk_combat_configs_sfx
        FOREIGN KEY (sfx)
        REFERENCES fx(id)
        ON DELETE SET NULL
        ON UPDATE CASCADE;

-- Re-point skills.cooldown_goal_id at the goal definition table.
-- V12 originally wired this FK to user_goal_status(id), which transitively
-- requires a user row to exist before any skill cooldown can reference a
-- goal. That made it impossible to seed a goal-based cooldown in migrations.
-- We drop and re-add the FK against goals(id) instead; the column already
-- carries the right values for the new target.
ALTER TABLE skills DROP FOREIGN KEY fk_skills_goal;
ALTER TABLE skills
    ADD CONSTRAINT fk_skills_goal
        FOREIGN KEY (cooldown_goal_id)
        REFERENCES goals(id)
        ON DELETE SET NULL
        ON UPDATE CASCADE;

-- Align combat_log enums with the values produced by the engine.
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
