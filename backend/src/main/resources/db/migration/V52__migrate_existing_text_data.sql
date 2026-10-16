-- Migration script for migrating existing text data to localized columns

-- Migrate data from quests table
UPDATE quests
SET
    name_en = name,
    name_es = name,
    description_en = description,
    description_es = description;

ALTER TABLE quests
DROP COLUMN name,
DROP COLUMN description;


-- Migrate data from skills table
UPDATE skills
SET
    name_en = name,
    name_es = name,
    description_en = description,
    description_es = description,
    quote_en = quote,
    quote_es = quote,
    cooldown_other_description_en = cooldown_other_description,
    cooldown_other_description_es = cooldown_other_description;

ALTER TABLE skills
DROP COLUMN name,
DROP COLUMN description,
DROP COLUMN quote,
DROP COLUMN cooldown_other_description;

-- Migrate data from conversations table
UPDATE conversations
SET
    dialog_en = dialog,
    dialog_es = dialog,
    dialog_m_en = dialog_m,
    dialog_m_es = dialog_m,
    dialog_w_en = dialog_w,
    dialog_w_es = dialog_w;

ALTER TABLE conversations
DROP COLUMN dialog,
DROP COLUMN dialog_m,
DROP COLUMN dialog_w;

UPDATE conversation_options
SET
    text_en = text,
    text_es = text,
    text_m_en = text_m,
    text_m_es = text_m,
    text_w_en = text_w,
    text_w_es = text_w;

ALTER TABLE conversation_options
DROP COLUMN text,
DROP COLUMN text_m,
DROP COLUMN text_w;

-- Migrate data from goals table
UPDATE goals
SET
    action_en = action,
    action_es = action;

ALTER TABLE goals
DROP COLUMN action;

-- Migrate data from nodes table
UPDATE nodes
SET
    display_name_en = display_name,
    display_name_es = display_name;

ALTER TABLE nodes
DROP COLUMN display_name;

