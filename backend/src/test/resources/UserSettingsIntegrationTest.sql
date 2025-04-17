CREATE TABLE IF NOT EXISTS user_settings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    are_notifications_enabled BOOLEAN NOT NULL,
    theme VARCHAR(255) NOT NULL
);

INSERT INTO user_settings (id, email, are_notifications_enabled, theme)
VALUES (2, 'finnthehuman@gmail.com', TRUE, 'LIGHT');