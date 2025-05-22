DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS user_settings;

CREATE TABLE IF NOT EXISTS user_settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,

    sound_volume INT NOT NULL,
    music_volume INT NOT NULL,
    is_rumbling_on BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,

    CONSTRAINT fk_user_email_to_settings FOREIGN KEY (email) REFERENCES user_settings(email) ON DELETE CASCADE
);