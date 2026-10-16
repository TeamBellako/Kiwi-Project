ALTER TABLE subquests
ADD COLUMN name_en VARCHAR(255),
ADD COLUMN name_es VARCHAR(255);

UPDATE subquests
SET name_en = name,
    name_es = name;

ALTER TABLE subquests
    MODIFY COLUMN name_en VARCHAR(255) NOT NULL,
    MODIFY COLUMN name_es VARCHAR(255) NOT NULL;

ALTER TABLE subquests
DROP COLUMN name;

DELETE FROM user_subquest_status
WHERE subquest_id IN (2, 3);

DELETE FROM subquests
WHERE id IN (2, 3);