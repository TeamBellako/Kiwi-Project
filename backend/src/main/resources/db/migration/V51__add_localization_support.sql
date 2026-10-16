-- Migration script for adding localization support to database tables

-- Add language columns to quests table
ALTER TABLE quests
ADD COLUMN name_en VARCHAR(255),
ADD COLUMN name_es VARCHAR(255),
ADD COLUMN description_en TEXT,
ADD COLUMN description_es TEXT;

-- Add language columns to skills table
ALTER TABLE skills
ADD COLUMN name_en VARCHAR(255),
ADD COLUMN name_es VARCHAR(255),
ADD COLUMN description_en TEXT,
ADD COLUMN description_es TEXT,
ADD COLUMN quote_en TEXT,
ADD COLUMN quote_es TEXT,
ADD COLUMN cooldown_other_description_en TEXT,
ADD COLUMN cooldown_other_description_es TEXT;

-- Add language columns to conversations table
ALTER TABLE conversations
ADD COLUMN dialog_en TEXT,
ADD COLUMN dialog_es TEXT,
ADD COLUMN dialog_m_en TEXT,
ADD COLUMN dialog_m_es TEXT,
ADD COLUMN dialog_w_en TEXT,
ADD COLUMN dialog_w_es TEXT;

ALTER TABLE conversation_options
ADD COLUMN text_en VARCHAR(500) NOT NULL,
ADD COLUMN text_es VARCHAR(500) NOT NULL,
ADD COLUMN text_m_en VARCHAR(500) NOT NULL,
ADD COLUMN text_m_es VARCHAR(500) NOT NULL,
ADD COLUMN text_w_en VARCHAR(500) NOT NULL,
ADD COLUMN text_w_es VARCHAR(500) NOT NULL;

-- Add language columns to goals table
ALTER TABLE goals
ADD COLUMN action_en TEXT,
ADD COLUMN action_es TEXT;

-- Add language columns to nodes table
ALTER TABLE nodes
ADD COLUMN display_name_en VARCHAR(255),
ADD COLUMN display_name_es VARCHAR(255);

-- Add language preference column to users table
ALTER TABLE users
ADD COLUMN language_preference VARCHAR(10) DEFAULT 'en';

-- Create index on user language preference for better performance
CREATE INDEX idx_users_language_preference ON users(language_preference);

-- Add constraints to ensure language preference is valid (optional)
-- This constraint can be added if needed in the future:
-- ALTER TABLE users ADD CONSTRAINT chk_language_preference CHECK (language_preference IN ('en', 'es'));