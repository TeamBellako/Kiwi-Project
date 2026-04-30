DROP PROCEDURE IF EXISTS migrate_v31;

DELIMITER $$

CREATE PROCEDURE migrate_v31()
BEGIN
    IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE TABLE_SCHEMA = DATABASE()
                     AND TABLE_NAME = 'goals'
                     AND COLUMN_NAME = 'on_completed_action') THEN
        ALTER TABLE goals ADD COLUMN on_completed_action VARCHAR(255);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE TABLE_SCHEMA = DATABASE()
                     AND TABLE_NAME = 'goals'
                     AND COLUMN_NAME = 'on_completed_entity') THEN
        ALTER TABLE goals ADD COLUMN on_completed_entity VARCHAR(255);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE TABLE_SCHEMA = DATABASE()
                     AND TABLE_NAME = 'goals'
                     AND COLUMN_NAME = 'on_completed_entity_id') THEN
        ALTER TABLE goals ADD COLUMN on_completed_entity_id INT;
    END IF;
END$$

DELIMITER ;

CALL migrate_v31();
DROP PROCEDURE migrate_v31;

UPDATE goals
SET on_completed_action = '',
    on_completed_entity = '',
    on_completed_entity_id = 0
WHERE on_completed_action IS NULL;
