CREATE TABLE IF NOT EXISTS combat_elements (
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
	
	name VARCHAR(30) NOT NULL UNIQUE,
	
	icon INT NOT NULL,
	
	description VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS combat_states (
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
	
	name VARCHAR(30) NOT NULL UNIQUE,
	
	icon INT NOT NULL,
	
	description VARCHAR(255) NOT NULL
);


CREATE TABLE IF NOT EXISTS user_stats (
    user_id BIGINT PRIMARY KEY,

    max_hp INT NOT NULL,

    patk INT NOT NULL,
    matk INT NOT NULL,

    pdef INT NOT NULL,
    mdef INT NOT NULL,

    acc INT NOT NULL,
    eva INT NOT NULL,

    lck INT NOT NULL,

    CONSTRAINT fk_user_stats_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS user_elemental_multipliers (
    user_id BIGINT NOT NULL,
    element_id BIGINT NOT NULL,

    multiplier FLOAT NOT NULL,

    PRIMARY KEY (user_id, element_id),

    CONSTRAINT fk_user_elemental_multipliers_users
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_user_elemental_multipliers_elements
        FOREIGN KEY (element_id)
        REFERENCES combat_elements(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS user_status_resistances (
    user_id BIGINT NOT NULL,
    state_id BIGINT NOT NULL,

    resistance FLOAT NOT NULL,

    PRIMARY KEY (user_id, state_id),

    CONSTRAINT fk_user_status_resistances_users
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_user_status_resistances_states
        FOREIGN KEY (state_id)
        REFERENCES combat_states(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS enemies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    name VARCHAR(255) NOT NULL,
	sprite VARCHAR(100) NOT NULL,

    max_hp INT NOT NULL,

    patk INT NOT NULL,
    matk INT NOT NULL,

    pdef INT NOT NULL,
    mdef INT NOT NULL,

    acc INT NOT NULL,
    eva INT NOT NULL,

    lck INT NOT NULL
);

CREATE TABLE IF NOT EXISTS enemy_elemental_multipliers (
    enemy_id BIGINT NOT NULL,
    element_id BIGINT NOT NULL,

    multiplier FLOAT NOT NULL,
	
	PRIMARY KEY (enemy_id, element_id),
	
    CONSTRAINT fk_enemy_elemental_multipliers_enemies
        FOREIGN KEY (enemy_id)
        REFERENCES enemies(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
	
	CONSTRAINT fk_enemy_elemental_multipliers_elements
		FOREIGN KEY (element_id)
		REFERENCES combat_elements(id)
		ON DELETE CASCADE
		ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS enemy_status_resistances (
    enemy_id BIGINT NOT NULL,
    state_id BIGINT NOT NULL,

    resistance FLOAT NOT NULL,
	
	PRIMARY KEY (enemy_id, state_id),
	
    CONSTRAINT fk_enemy_status_resistances_enemies
        FOREIGN KEY (enemy_id)
        REFERENCES enemies(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
	
	CONSTRAINT fk_enemy_status_resistances_states
		FOREIGN KEY (state_id)
		REFERENCES combat_states(id)
		ON DELETE CASCADE
		ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS enemy_skill (
    enemy_id BIGINT NOT NULL,
    skill_id BIGINT NOT NULL,

    PRIMARY KEY (enemy_id, skill_id),

    CONSTRAINT fk_enemy
        FOREIGN KEY (enemy_id)
        REFERENCES enemies(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_skill
        FOREIGN KEY (skill_id)
        REFERENCES skills(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS combat_configs (
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
	enemy_id BIGINT NOT NULL,
	time_limit INT,	
	
    CONSTRAINT fk_combat_config_enemy
        FOREIGN KEY (enemy_id)
        REFERENCES enemies(id)
		ON DELETE CASCADE
		ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS combats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

	combat_config_id BIGINT NOT NULL,
	
    user_id BIGINT NOT NULL,
    enemy_id BIGINT NOT NULL,

	user_hp INT NOT NULL,
	user_max_hp INT NOT NULL,
    user_patk INT NOT NULL,
    user_matk INT NOT NULL,
    user_pdef INT NOT NULL,
    user_mdef INT NOT NULL,
    user_acc INT NOT NULL,
    user_eva INT NOT NULL,
    user_lck INT NOT NULL,
	
	enemy_hp INT NOT NULL,
	enemy_max_hp INT NOT NULL,
    enemy_patk INT NOT NULL,
    enemy_matk INT NOT NULL,
    enemy_pdef INT NOT NULL,
    enemy_mdef INT NOT NULL,
    enemy_acc INT NOT NULL,
    enemy_eva INT NOT NULL,
    enemy_lck INT NOT NULL,

    turn_number INT NOT NULL DEFAULT 1,
	
	ends_at TIMESTAMP NULL,

    combat_status ENUM('ONGOING','USER_WON','USER_LOST') NOT NULL,

	INDEX idx_combats_user_config (user_id, combat_config_id),
	
    CONSTRAINT fk_combats_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

	CONSTRAINT fk_combats_config
		FOREIGN KEY (combat_config_id)
		REFERENCES combat_configs(id)
		ON DELETE CASCADE
		ON UPDATE CASCADE,
	
    CONSTRAINT fk_combats_enemy
        FOREIGN KEY (enemy_id)
        REFERENCES enemies(id)
		ON DELETE CASCADE
		ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS skill_effects (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    skill_id BIGINT NOT NULL,
	
	target ENUM('OPPONENT','SELF','ALLY') NOT NULL DEFAULT 'OPPONENT',

    effect_type ENUM('DAMAGE','HEAL','APPLY_STATUS','MODIFY_STAT') NOT NULL,
	
	stat_affected ENUM('CURRENT_HP','MAX_HP','PATK','MATK','PDEF','MDEF','ACC','EVA','LCK') NULL,
	
	stat_modification ENUM('SUM','SUB','MUL','DIV') NULL,

    power FLOAT,

    attack_type ENUM('PHYSICAL','MAGICAL'),
	
	element_id BIGINT,

    hit_chance INT,

    state_id BIGINT,

    status_duration INT,

    CONSTRAINT fk_skill_effects_skill
        FOREIGN KEY (skill_id)
        REFERENCES skills(id)
        ON DELETE CASCADE
		ON UPDATE CASCADE,
		
	CONSTRAINT fk_skill_effects_elements
		FOREIGN KEY (element_id)
		REFERENCES combat_elements(id)
		ON DELETE CASCADE
		ON UPDATE CASCADE,
	
	CONSTRAINT fk_skill_effects_states
		FOREIGN KEY (state_id)
		REFERENCES combat_states(id)
		ON DELETE CASCADE
		ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS combat_active_status (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    combat_id BIGINT NOT NULL,
	
	source_skill_id BIGINT NOT NULL,

    target ENUM('USER','ENEMY') NOT NULL,

    state_id BIGINT NOT NULL,
	
	value FLOAT,

    remaining_turns INT NOT NULL,
	
	INDEX idx_combat_status_effects_combat (combat_id),

    CONSTRAINT fk_combat_status_effects_combats
        FOREIGN KEY (combat_id)
        REFERENCES combats(id)
		ON DELETE CASCADE
		ON UPDATE CASCADE,
		
    CONSTRAINT fk_combat_status_effects_skills
        FOREIGN KEY (source_skill_id)
        REFERENCES skills(id)
		ON DELETE CASCADE
		ON UPDATE CASCADE,
		
	CONSTRAINT fk_combat_status_effects_states
		FOREIGN KEY (state_id)
		REFERENCES combat_states(id)
		ON DELETE CASCADE
		ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS combat_last_skill (
    combat_id BIGINT NOT NULL,
    actor ENUM('USER','ENEMY') NOT NULL,
    skill_id BIGINT NOT NULL,

    PRIMARY KEY (combat_id, actor),

    INDEX idx_combat_last_skill_combat (combat_id),

    CONSTRAINT fk_combat_last_skill_combats
        FOREIGN KEY (combat_id)
        REFERENCES combats(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_combat_last_skill_skills
        FOREIGN KEY (skill_id)
        REFERENCES skills(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS combat_blocked_skills (
    combat_id BIGINT NOT NULL,
    actor ENUM('USER','ENEMY') NOT NULL,
    skill_id BIGINT NOT NULL,

    PRIMARY KEY (combat_id, actor, skill_id),

    INDEX idx_combat_blocked_skills_combat (combat_id),

    CONSTRAINT fk_combat_blocked_skills_combats
        FOREIGN KEY (combat_id)
        REFERENCES combats(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_combat_blocked_skills_skills
        FOREIGN KEY (skill_id)
        REFERENCES skills(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS combat_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    combat_id BIGINT NOT NULL,
    turn_number INT NOT NULL,
    actor ENUM('USER','ENEMY') NOT NULL,

    action_type ENUM(
        'SKILL_USED',
        'ACTOR_BLOCKED_BY_STATE',
        'SKILL_REPEAT_BY_STATE',
        'ACTOR_DAMAGED_BY_STATE',
        'BLOCKED_SKILLS_BY_STATE',
		'RELEASED_SKILLS_BY_STATE',
        'SKIP',
        'STATUS_TURN_REDUCED',
        'STATUS_FINISHED',
        'TIMEOUT'
    ) NOT NULL,

    skill_id BIGINT NULL,
    skill_name VARCHAR(100) NULL,

    target ENUM('OPPONENT','SELF','ALLY') NULL,

    effect_type ENUM(
        'DAMAGE',
        'HEAL',
		'MODIFY_STAT',
        'STATUS_APPLIED',
        'STATUS_REMOVED',
        'MISS'
    ) NULL,
	
	stat_affected ENUM('CURRENT_HP','MAX_HP','PATK','MATK','PDEF','MDEF','ACC','EVA','LCK') NULL,

    value FLOAT NULL,
	
    critic BOOLEAN DEFAULT FALSE,

    state_id BIGINT NULL,
	state_name VARCHAR(100) NULL,
	
    status_duration INT NULL, -- remainingTurns

    blocked_skills VARCHAR(255) NULL, -- ids separados por comas

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_combat_log_combat (combat_id),

    CONSTRAINT fk_combat_log_combats
        FOREIGN KEY (combat_id)
        REFERENCES combats(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_combat_log_states
        FOREIGN KEY (state_id)
        REFERENCES combat_states(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_combat_log_skills
        FOREIGN KEY (skill_id)
        REFERENCES skills(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);



