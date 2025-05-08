DROP TABLE IF EXISTS user_settings;

DROP TABLE IF EXISTS users;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

INSERT INTO users (email, password) 
VALUES ('finn@thehuman.com', 'mathematical');

DROP TABLE IF EXISTS user_settings;

CREATE TABLE IF NOT EXISTS user_settings (
    email VARCHAR(255) PRIMARY KEY,
    are_notifications_enabled BOOLEAN NOT NULL,
    theme VARCHAR(255) NOT NULL,
    FOREIGN KEY (email) REFERENCES users(email) ON DELETE CASCADE
);

INSERT INTO user_settings (email, are_notifications_enabled, theme)
VALUES ('finn@thehuman.com', TRUE, 'LIGHT');