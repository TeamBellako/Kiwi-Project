ALTER TABLE conversations
ADD COLUMN incidence_name_to_set VARCHAR(255),
ADD COLUMN incidence_value_to_set BOOLEAN NOT NULL DEFAULT TRUE;