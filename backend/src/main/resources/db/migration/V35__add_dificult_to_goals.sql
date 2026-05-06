ALTER TABLE goals
ADD COLUMN difficulty INT NOT NULL DEFAULT 1;

CREATE TABLE IF NOT EXISTS user_goal_progress (
    user_id BIGINT NOT NULL,
    goal_type VARCHAR(50) NOT NULL,
    current_difficulty INT NOT NULL DEFAULT 1,
    goals_completed_at_difficulty INT NOT NULL DEFAULT 0,
    goals_failed_at_difficulty INT NOT NULL DEFAULT 0,
    PRIMARY KEY (user_id, goal_type),
    CONSTRAINT fk_ugp_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE
);