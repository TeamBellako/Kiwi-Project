DROP PROCEDURE IF EXISTS migrate_v23;

DELIMITER $$

CREATE PROCEDURE migrate_v23()
BEGIN
    IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE TABLE_SCHEMA = DATABASE()
                     AND TABLE_NAME = 'conversation_options'
                     AND COLUMN_NAME = 'incidence_to_show') THEN
        ALTER TABLE conversation_options ADD COLUMN incidence_to_show VARCHAR(255);
    END IF;
END$$

DELIMITER ;

CALL migrate_v23();
DROP PROCEDURE migrate_v23;
