DROP PROCEDURE IF EXISTS migrate_v21;

DELIMITER $$

CREATE PROCEDURE migrate_v21()
BEGIN
    IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE TABLE_SCHEMA = DATABASE()
                     AND TABLE_NAME = 'conversations'
                     AND COLUMN_NAME = 'incidence_name_to_set') THEN
        ALTER TABLE conversations ADD COLUMN incidence_name_to_set VARCHAR(255);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE TABLE_SCHEMA = DATABASE()
                     AND TABLE_NAME = 'conversations'
                     AND COLUMN_NAME = 'incidence_value_to_set') THEN
        ALTER TABLE conversations ADD COLUMN incidence_value_to_set BOOLEAN NOT NULL DEFAULT TRUE;
    END IF;
END$$

DELIMITER ;

CALL migrate_v21();
DROP PROCEDURE migrate_v21;
