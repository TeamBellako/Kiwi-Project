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
DROP TABLE IF EXISTS metrics;
DROP TABLE IF EXISTS personality;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS settings;

-- Create settings table
CREATE TABLE IF NOT EXISTS settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,

    sound_volume FLOAT NOT NULL CHECK (sound_volume >= 0 AND sound_volume <= 1),
    music_volume FLOAT NOT NULL CHECK (music_volume >= 0 AND music_volume <= 1)
);

-- Create users table with a foreign key to settings
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,

    -- Reference to settings table using email
    CONSTRAINT fk_user_to_settings FOREIGN KEY (email) REFERENCES settings(email) ON DELETE CASCADE
);

-- Create metrics table with a foreign key to users
CREATE TABLE IF NOT EXISTS metrics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,  -- Foreign key to users
    date DATE NOT NULL,
    steps INT NOT NULL CHECK (steps >= 0),  -- Steps must be non-negative
    screen_time_seconds INT NOT NULL CHECK (screen_time_seconds >= 0),  -- Screen time must be non-negative, stored in seconds

    -- Foreign key to users table
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
    -- Foreign key to users table
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_users FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);


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