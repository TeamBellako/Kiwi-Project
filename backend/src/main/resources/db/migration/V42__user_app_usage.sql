-- V42__user_app_usage.sql
-- Stores the baseline average daily usage (last 7 days) for the user's good and bad apps,
-- captured once at account creation and never updated again.

CREATE TABLE IF NOT EXISTS user_app_usage (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    app_type ENUM('GOOD', 'BAD') NOT NULL,
    avg_daily_usage_ms BIGINT NOT NULL DEFAULT 0,
    recorded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_app_usage_users FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT unique_user_app_type UNIQUE (user_id, app_type)
);
