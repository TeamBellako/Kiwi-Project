CREATE TABLE IF NOT EXISTS user_settings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    are_notifications_enabled BOOLEAN NOT NULL,
    theme ENUM('LIGHT', 'DARK') NOT NULL
);