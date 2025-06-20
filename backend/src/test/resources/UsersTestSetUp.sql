-- Create settings table
CREATE TABLE IF NOT EXISTS settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,

    sound_volume INT NOT NULL CHECK (sound_volume >= 0 AND sound_volume <= 100),
    music_volume INT NOT NULL CHECK (music_volume >= 0 AND music_volume <= 100)
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
