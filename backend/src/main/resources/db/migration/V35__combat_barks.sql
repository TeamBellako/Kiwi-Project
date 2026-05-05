-- Combat barks: short in-combat speech bubbles fired on HP/skill/elapsed-time
-- triggers. Trigger definitions are per CombatConfig (static, authored upstream)
-- and reference an existing ConversationPersistence as the spoken content.
-- Per-active-combat tracking lives in combat_fired_barks so a trigger fires at
-- most once per combat across resumes.
--
-- Idempotent: CREATE TABLE IF NOT EXISTS, no destructive ops.

CREATE TABLE IF NOT EXISTS combat_bark_triggers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    combat_config_id BIGINT NOT NULL,

    type ENUM(
        'ENEMY_HP_PERCENT',
        'PLAYER_HP_PERCENT',
        'SKILL_USED',
        'COMBAT_ELAPSED_SECONDS'
    ) NOT NULL,

    threshold FLOAT NULL,
    skill_id BIGINT NULL,
    conversation_id BIGINT NOT NULL,

    dismiss_mode ENUM('AUTO','CLICK') NOT NULL DEFAULT 'AUTO',
    priority INT NOT NULL DEFAULT 0,

    INDEX idx_combat_bark_triggers_config (combat_config_id),

    CONSTRAINT fk_combat_bark_triggers_config
        FOREIGN KEY (combat_config_id)
        REFERENCES combat_configs(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_combat_bark_triggers_skill
        FOREIGN KEY (skill_id)
        REFERENCES skills(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_combat_bark_triggers_conversation
        FOREIGN KEY (conversation_id)
        REFERENCES conversations(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS combat_fired_barks (
    combat_id BIGINT NOT NULL,
    trigger_id BIGINT NOT NULL,

    PRIMARY KEY (combat_id, trigger_id),

    INDEX idx_combat_fired_barks_combat (combat_id),

    CONSTRAINT fk_combat_fired_barks_combat
        FOREIGN KEY (combat_id)
        REFERENCES combats(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_combat_fired_barks_trigger
        FOREIGN KEY (trigger_id)
        REFERENCES combat_bark_triggers(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);
