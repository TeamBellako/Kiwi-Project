
-- Create sprites DB
CREATE TABLE IF NOT EXISTS sprites (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- Create expressions DB
CREATE TABLE IF NOT EXISTS expressions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- Create backgrounds DB
CREATE TABLE IF NOT EXISTS backgrounds (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- Create fx DB
CREATE TABLE IF NOT EXISTS fx (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- Create conversation table
CREATE TABLE IF NOT EXISTS conversations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type ENUM('FULL', 'SMALL') NOT NULL,
    sprite BIGINT NOT NULL,
    expresion BIGINT NOT NULL,
    background BIGINT,
    fx BIGINT,
    dark TINYINT(1) NOT NULL DEFAULT 0,
    dialog VARCHAR(1000) NOT NULL,
    dialog_m VARCHAR(1000) NOT NULL,
    dialog_w VARCHAR(1000) NOT NULL,
    delay_start_ms INT,
    delay_end_ms INT,
    next_event ENUM('CONVERSATION', 'BATTLE', 'END') NOT NULL,
    event_id BIGINT,

    FOREIGN KEY (sprite) REFERENCES sprites(id),
    FOREIGN KEY (expresion) REFERENCES expressions(id),
    FOREIGN KEY (background) REFERENCES backgrounds(id),
    FOREIGN KEY (fx) REFERENCES fx(id)
);

-- Create conversation_options table
CREATE TABLE IF NOT EXISTS conversation_options (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    conversation_id BIGINT NOT NULL,
    text VARCHAR(500) NOT NULL,
    text_m VARCHAR(500) NOT NULL,
    text_w VARCHAR(500) NOT NULL,
    next_event_id BIGINT NOT NULL,
    cost INT 
);

-- Create user_conversation_selection table
CREATE TABLE IF NOT EXISTS user_conversation_selection (
    user_id BIGINT NOT NULL,
    conversation_id BIGINT NOT NULL,
    option_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, conversation_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (conversation_id) REFERENCES conversations(id),
    FOREIGN KEY (option_id) REFERENCES conversation_options(id)
);