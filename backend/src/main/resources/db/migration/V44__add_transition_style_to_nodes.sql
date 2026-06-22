DROP PROCEDURE IF EXISTS migrate_v44;

DELIMITER $$

CREATE PROCEDURE migrate_v44()
BEGIN
    IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE TABLE_SCHEMA = DATABASE()
                     AND TABLE_NAME = 'nodes'
                     AND COLUMN_NAME = 'transition_style') THEN
        ALTER TABLE nodes
            ADD COLUMN transition_style ENUM('VEIL', 'IMMEDIATE') NOT NULL DEFAULT 'VEIL';
    END IF;
END$$

DELIMITER ;

CALL migrate_v44();
DROP PROCEDURE migrate_v44;

UPDATE nodes SET transition_style = 'VEIL';

UPDATE nodes SET transition_style = 'IMMEDIATE' WHERE id = 1;
