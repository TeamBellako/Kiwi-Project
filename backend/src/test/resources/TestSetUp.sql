
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

    -- Foreign key to users table
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_personality_users FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create nodes table (needed to restart id count)
DROP TABLE IF EXISTS node_edges;
DROP TABLE IF EXISTS nodes;
-- Create nodes table
CREATE TABLE IF NOT EXISTS nodes (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  price INT NOT NULL,
  icon INT NOT NULL,
  cord_x FLOAT NOT NULL,
  cord_y FLOAT NOT NULL,
  name VARCHAR(255) NOT NULL,
  display_name VARCHAR(255),
  map_id INT NOT NULL,
  is_first_node_of_map BOOLEAN DEFAULT FALSE,
  on_execution_action VARCHAR(255),
  on_execution_entity VARCHAR(255),
  on_execution_entity_id INT,
  CONSTRAINT uq_nodes_name UNIQUE (name),
  CHECK (
    cord_x >= 0.0 AND cord_x <= 1.0
    AND cord_y >= 0.0 AND cord_y <= 1.0
  )
);

-- Create node_edges table
CREATE TABLE IF NOT EXISTS node_edges (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  from_node_id BIGINT NOT NULL,
  to_node_id BIGINT NOT NULL,
  CONSTRAINT fk_node_edges_from_node FOREIGN KEY (from_node_id) REFERENCES nodes(id) ON DELETE CASCADE,
  CONSTRAINT fk_node_edges_to_node FOREIGN KEY (to_node_id) REFERENCES nodes(id) ON DELETE CASCADE
);

-- Drop and recreate goals-related tables to reset auto-increment and seed data.
-- user_goal_status must be dropped first due to FK dependency on goals.
DROP TABLE IF EXISTS user_goal_status;
DROP TABLE IF EXISTS goals;

-- Create goals table (goal definitions, formerly suggested_goals)
CREATE TABLE IF NOT EXISTS goals (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    action TEXT,
    target INT NOT NULL,
    type VARCHAR(50) NOT NULL,
    category VARCHAR(50) NOT NULL,
    reward INT NOT NULL
);

-- Seed goal definitions used by tests (IDs: 1=exercise, 2=app, 3=skill)
INSERT INTO goals (name, action, target, type, category, reward)
    VALUES ('Exercise Goal', 'Exercise for 30 minutes', 30, 'EXERCISE', 'DAILY_CHALLENGES', 10);
INSERT INTO goals (name, action, target, type, category, reward)
    VALUES ('App Usage Goal', 'Improve Java skills', 100, 'PRODUCTIVITY', 'APP_USAGE', 50);
INSERT INTO goals (name, action, target, type, category, reward)
    VALUES ('Skill Goal', 'Improve Java skills', 100, 'PRODUCTIVITY', 'SKILL', 50);

-- Create user_goal_status table (per-user goal tracking, formerly goals)
CREATE TABLE IF NOT EXISTS user_goal_status (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    goal_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    date DATE NOT NULL,
    value INT NOT NULL,
    CONSTRAINT fk_ugs_users FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_ugs_goals FOREIGN KEY (goal_id) REFERENCES goals(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tips (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    text VARCHAR(255) NOT NULL,
    read_more_url VARCHAR(255)
);

INSERT INTO tips (title, text, read_more_url) 
    VALUES ('Pomodoro Timer', 'Just work, bro', 'https://www.todoist.com/es/productivity-methods/pomodoro-technique');
    
CREATE TABLE IF NOT EXISTS incidences (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS user_incidences (
    user_id BIGINT NOT NULL,
    incidence_id BIGINT NOT NULL,
    value BOOLEAN NOT NULL DEFAULT FALSE
);



