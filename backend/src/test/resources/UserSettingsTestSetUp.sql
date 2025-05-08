DROP TABLE IF EXISTS user_settings;

CREATE TABLE IF NOT EXISTS user_settings (
    email VARCHAR(255) PRIMARY KEY,
    are_notifications_enabled BOOLEAN NOT NULL,
    theme VARCHAR(255) NOT NULL
);

INSERT INTO user_settings (email, are_notifications_enabled, theme)
VALUES ('finn@thehuman.com', TRUE, 'LIGHT');