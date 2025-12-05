#!/usr/bin/bash
set -e

TMP_MY_CNF="/tmp/.my.cnf"

cat <<EOF > ${TMP_MY_CNF}
[client]
user=root
password=${MYSQL_ROOT_PASSWORD}
EOF

chmod 600 ${TMP_MY_CNF}

cat <<EOF > /tmp/init-db.sql
CREATE DATABASE IF NOT EXISTS ${MYSQL_DATABASE};

USE ${MYSQL_DATABASE};

USE kiwi_db_dev;

-- Drop tables if they exist
DROP TABLE IF EXISTS user_node_status;
DROP TABLE IF EXISTS nodes;
DROP TABLE IF EXISTS metrics;
DROP TABLE IF EXISTS personality;
DROP TABLE IF EXISTS settings;
DROP TABLE IF EXISTS users;

-- Create users table with a foreign key to settings
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    register_date DATE NOT NULL
);

-- Create settings table
CREATE TABLE IF NOT EXISTS settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sound_volume FLOAT NOT NULL CHECK (sound_volume >= 0 AND sound_volume <= 1),
    music_volume FLOAT NOT NULL CHECK (music_volume >= 0 AND music_volume <= 1),

    -- Foreign key to users table
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_settings_users FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create metrics table with a foreign key to users
CREATE TABLE IF NOT EXISTS metrics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date DATE NOT NULL,
    max_good_time_seconds INT NOT NULL CHECK (max_good_time_seconds >= 0),
    current_good_time_seconds INT NOT NULL CHECK (current_good_time_seconds >= 0),
    max_bad_time_seconds INT NOT NULL CHECK (max_bad_time_seconds >= 0),
    current_bad_time_seconds INT NOT NULL CHECK (current_bad_time_seconds >= 0),

    -- Foreign key to users table
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_metrics_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,

    -- Add a unique constraint for each date for each user to avoid multiple entries per day
    CONSTRAINT unique_user_date UNIQUE (user_id, date)
);

-- Create personality table
CREATE TABLE IF NOT EXISTS personality (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    real_name VARCHAR(255),
    knight_name VARCHAR(255),
    build VARCHAR(255),
    good_apps TEXT,
    bad_apps TEXT,
    neutral_apps TEXT,

    -- Foreign key to users table
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_personality_users FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create nodes table
CREATE TABLE IF NOT EXISTS nodes (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  node_order INT NOT NULL,
  price INT NOT NULL,
  cord_x FLOAT  NOT NULL,
  cord_y FLOAT NOT NULL ,
  CHECK (cord_x >= 0.0 & cord_x <= 1.0 & cord_y >= 0.0 & cord_y <= 1.0)
);

-- Create user_nodes_status table
CREATE TABLE IF NOT EXISTS user_node_status (
  user_id BIGINT NOT NULL,
  node_id BIGINT NOT NULL,
  status ENUM('LOCKED', 'OPEN', 'COMPLETED') NOT NULL,
  PRIMARY KEY (user_id, node_id),
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (node_id) REFERENCES nodes(id) ON DELETE CASCADE
);

-- Insert placeholder values for user_nodes_status
INSERT INTO nodes VALUES (1,1,120,0.57,0.25),(2,2,140,0.61,0.33),(3,3,100,0.58,0.44),(4,4,180,0.59,0.55);

-- Create quest table
CREATE TABLE IF NOT EXISTS quests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    experience INT NOT NULL
);

-- Create subquests table
CREATE TABLE IF NOT EXISTS subquests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quest_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    experience INT NOT NULL,
    order_index INT NOT NULL,

    FOREIGN KEY (quest_id) REFERENCES quests(id)
);

-- Create user_quest_status table
CREATE TABLE IF NOT EXISTS user_quest_status (
    user_id BIGINT NOT NULL,
    quest_id BIGINT NOT NULL,

    status ENUM('ACTIVE', 'COMPLETED') NOT NULL,

    PRIMARY KEY (user_id, quest_id),
    FOREIGN KEY (quest_id) REFERENCES quests(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Create user_subquest_status table
CREATE TABLE IF NOT EXISTS user_subquest_status (
    user_id BIGINT NOT NULL,
    subquest_id BIGINT NOT NULL,
    status ENUM('LOCKED','ACTIVE','COMPLETED','FAILED') NOT NULL,
    PRIMARY KEY (user_id, subquest_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (subquest_id) REFERENCES subquests(id)
);

-- Trigger to DELETE ON CASCADE user_quest_status table
DELIMITER $$

CREATE TRIGGER user_quest_status_after_delete
AFTER DELETE ON user_quest_status
FOR EACH ROW
BEGIN
    DELETE FROM user_subquests
    WHERE user_id = OLD.user_id
      AND subquest_id IN (
          SELECT id FROM subquests WHERE quest_id = OLD.quest_id
      );    
END$$

DELIMITER ;
--

CREATE USER '${BACKEND_DB_USERNAME}'@'%' IDENTIFIED BY '${BACKEND_DB_PASSWORD}';
GRANT ALL PRIVILEGES ON ${MYSQL_DATABASE}.* TO '${BACKEND_DB_USERNAME}'@'%';

CREATE ROLE '${TEAM_ROLE}';
GRANT SELECT, INSERT ON ${MYSQL_DATABASE}.* TO '${TEAM_ROLE}';

CREATE USER '${TEAM_01_USERNAME}'@'%' IDENTIFIED BY '${TEAM_01_PASSWORD}';
CREATE USER '${TEAM_02_USERNAME}'@'%' IDENTIFIED BY '${TEAM_02_PASSWORD}';
CREATE USER '${TEAM_03_USERNAME}'@'%' IDENTIFIED BY '${TEAM_03_PASSWORD}';

GRANT ${TEAM_ROLE} TO '${TEAM_01_USERNAME}'@'%';
GRANT ${TEAM_ROLE} TO '${TEAM_02_USERNAME}'@'%';
GRANT ${TEAM_ROLE} TO '${TEAM_03_USERNAME}'@'%';

FLUSH PRIVILEGES;

EOF

mysql --defaults-extra-file=${TMP_MY_CNF} < /tmp/init-db.sql

rm -f /tmp/init-db.sql
rm -f ${TMP_MY_CNF}