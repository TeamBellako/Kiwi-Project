-- V1__init.sql (Flyway)

-- users
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    register_date DATE NOT NULL,
    current_points INT NOT NULL DEFAULT 0,
    total_points INT NOT NULL DEFAULT 0
);

-- settings
CREATE TABLE IF NOT EXISTS settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sound_volume FLOAT NOT NULL CHECK (sound_volume >= 0 AND sound_volume <= 1),
    music_volume FLOAT NOT NULL CHECK (music_volume >= 0 AND music_volume <= 1),
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_settings_users FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- metrics
CREATE TABLE IF NOT EXISTS metrics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date DATE NOT NULL,
    max_good_time_seconds INT NOT NULL CHECK (max_good_time_seconds >= 0),
    current_good_time_seconds INT NOT NULL CHECK (current_good_time_seconds >= 0),
    max_bad_time_seconds INT NOT NULL CHECK (max_bad_time_seconds >= 0),
    current_bad_time_seconds INT NOT NULL CHECK (current_bad_time_seconds >= 0),
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_metrics_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT unique_user_date UNIQUE (user_id, date)
);

-- personality
CREATE TABLE IF NOT EXISTS personality (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    real_name VARCHAR(255),
    knight_name VARCHAR(255),
    build VARCHAR(255),
    good_apps TEXT,
    bad_apps TEXT,
    neutral_apps TEXT,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_personality_users FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- goals
CREATE TABLE IF NOT EXISTS goals (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    target BIGINT NOT NULL,
    action TEXT,
    type ENUM('EXERCISE', 'SLEEP', 'MEDITATION', 'NUTRITION', 'PRODUCTIVITY') NOT NULL,
    category ENUM('DAILY_CHALLENGES', 'APP_USAGE', 'SKILL') NOT NULL,
    status ENUM('COMPLETED', 'NOT_COMPLETED', 'IN_PROGRESS') NOT NULL,
    reward INT NOT NULL,
    date DATE NOT NULL,
    value BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_goals_users FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_date (user_id, date)
);

-- suggested_goals
CREATE TABLE IF NOT EXISTS suggested_goals (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    target BIGINT NOT NULL,
    action TEXT,
    type ENUM('EXERCISE', 'SLEEP', 'MEDITATION', 'NUTRITION', 'PRODUCTIVITY') NOT NULL,
    category ENUM('DAILY_CHALLENGES', 'APP_USAGE') NOT NULL,
    reward INT NOT NULL
);

INSERT IGNORE INTO suggested_goals (id, name, target, action, type, category, reward)
VALUES
('1', "", 10000, "Da 10000 pasos", "EXERCISE", "DAILY_CHALLENGES", 100),
('2', "", 10, "Medita 10 minutos", "MEDITATION", "DAILY_CHALLENGES", 200),
('3', "", 7500, "Da 7500 pasos", "EXERCISE", "DAILY_CHALLENGES", 100),
('4', "", 10, "Usa Duolingo 10 minutos", "MEDITATION", "DAILY_CHALLENGES", 200),
('5', "", 5, "Haz 5 flexiones", "EXERCISE", "DAILY_CHALLENGES", 100);

-- nodes
CREATE TABLE IF NOT EXISTS nodes (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  icon INT DEFAULT 0,
  price INT NOT NULL,
  cord_x FLOAT NOT NULL,
  cord_y FLOAT NOT NULL,
  event_on_execution BIGINT NOT NULL,
  name VARCHAR(255) NOT NULL,
  display_name VARCHAR(255),
  map_id INT NOT NULL,
  is_first_node_of_map BOOLEAN DEFAULT FALSE,
  CONSTRAINT uq_nodes_name UNIQUE (name),
  CHECK (
    cord_x >= 0.0 AND cord_x <= 1.0
    AND cord_y >= 0.0 AND cord_y <= 1.0
  )
);

CREATE TABLE IF NOT EXISTS user_node_status (
  user_id BIGINT NOT NULL,
  node_id BIGINT NOT NULL,
  status ENUM('LOCKED', 'OPEN', 'COMPLETED') NOT NULL,
  PRIMARY KEY (user_id, node_id),
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (node_id) REFERENCES nodes(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS node_edges (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    from_node_id BIGINT NOT NULL,
    to_node_id BIGINT NOT NULL,
    CONSTRAINT fk_node_edges_from FOREIGN KEY (from_node_id) REFERENCES nodes(id) ON DELETE CASCADE,
    CONSTRAINT fk_node_edges_to   FOREIGN KEY (to_node_id)   REFERENCES nodes(id) ON DELETE CASCADE,
    UNIQUE KEY uq_node_edge (from_node_id, to_node_id)
);

INSERT IGNORE INTO nodes (id, icon, price, cord_x, cord_y, event_on_execution, name, display_name, map_id, is_first_node_of_map)
VALUES
(1, 1, 120, 0.585, 0.12, 0, 'node_1', 'START', 0, TRUE),
(2, 0, 140, 0.623, 0.175, 0, 'node_2', NULL, 0, FALSE),
(3, 0, 180, 0.66, 0.228, 0, 'node_3', NULL, 0, FALSE),
(4, 2, 100, 0.598, 0.228, 0, 'node_4', 'CAVE OF THE DEEP BREATH', 0, FALSE),
(5, 0, 180, 0.66, 0.275, 0, 'node_5', NULL, 0, FALSE),
(6, 3, 140, 0.615, 0.295, 0, 'node_6', 'CITY', 0, FALSE),
(7, 1, 120, 0.585, 0.12, 0, 'node_7', 'MAP_SWITCH', 1, TRUE);

INSERT IGNORE INTO node_edges (from_node_id, to_node_id)
VALUES (1,2),(2,3),(3,4),(3,5),(5,6);

-- quests
CREATE TABLE IF NOT EXISTS quests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    experience INT NOT NULL,
    icon INT NOT NULL
);

CREATE TABLE IF NOT EXISTS subquests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quest_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    experience INT NOT NULL,
    order_index INT NOT NULL,
    FOREIGN KEY (quest_id) REFERENCES quests(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS user_quest_status (
    user_id BIGINT NOT NULL,
    quest_id BIGINT NOT NULL,
    status ENUM('ACTIVE', 'COMPLETED') NOT NULL,
    PRIMARY KEY (user_id, quest_id),
    FOREIGN KEY (quest_id) REFERENCES quests(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS user_subquest_status (
    user_id BIGINT NOT NULL,
    subquest_id BIGINT NOT NULL,
    status ENUM('LOCKED','ACTIVE','COMPLETED','FAILED') NOT NULL,
    PRIMARY KEY (user_id, subquest_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (subquest_id) REFERENCES subquests(id)
);

-- Drop the trigger if it already exists
DROP TRIGGER IF EXISTS user_quest_status_after_delete;

DELIMITER $$

CREATE TRIGGER user_quest_status_after_delete
AFTER DELETE ON user_quest_status
FOR EACH ROW
BEGIN
    DELETE FROM user_subquest_status
    WHERE user_id = OLD.user_id
      AND subquest_id IN (
          SELECT id FROM subquests WHERE quest_id = OLD.quest_id
      );
END$$

DELIMITER ;

INSERT IGNORE INTO quests (id, name, description, experience, icon)
VALUES
(1, 'First Quest', 'Complete your first challenge', 100, 1),
(2, 'Second Quest', 'A longer mission with multiple steps', 100, 2);

INSERT IGNORE INTO subquests (id, quest_id, name, experience, order_index)
VALUES
(1,1,'Objective 1',20,1),
(2,1,'Objective 2',20,2),
(3,1,'Objective 3',20,3),
(4,2,'Objective 1',20,1),
(5,2,'Objective 2',20,2),
(6,2,'Objective 3',20,3),
(7,2,'Objective 4',20,4);

-- skills
CREATE TABLE IF NOT EXISTS skills (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(500) NOT NULL,
    quote VARCHAR(255),
    icon INT NOT NULL,
    cooldown_type ENUM('GOAL', 'TIME', 'OTHER') NOT NULL,
    cooldown_goal_id BIGINT,
    cooldown_time_minutes INT,
    cooldown_other_description VARCHAR(500),
    levelup_skill_id BIGINT,
    CONSTRAINT fk_skills_goal
        FOREIGN KEY (cooldown_goal_id) REFERENCES goals(id)
        ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_skills_levelup
        FOREIGN KEY (levelup_skill_id) REFERENCES skills(id)
        ON DELETE SET NULL ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS user_skill_status (
    user_id BIGINT NOT NULL,
    skill_id BIGINT NOT NULL,
    is_cooldown BOOLEAN NOT NULL,
    cooldown_until TIMESTAMP NULL,
    deck_slot INT NOT NULL,
    PRIMARY KEY (user_id, skill_id),
    CONSTRAINT fk_user_skill_status_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_user_skill_status_skill
        FOREIGN KEY (skill_id) REFERENCES skills(id)
        ON DELETE CASCADE ON UPDATE CASCADE
);
