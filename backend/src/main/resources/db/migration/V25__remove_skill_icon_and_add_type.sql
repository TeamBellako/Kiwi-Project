DROP PROCEDURE IF EXISTS migrate_v25;

DELIMITER $$

CREATE PROCEDURE migrate_v25()
BEGIN
    IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE TABLE_SCHEMA = DATABASE()
                     AND TABLE_NAME = 'skills'
                     AND COLUMN_NAME = 'type') THEN
        ALTER TABLE skills ADD COLUMN type VARCHAR(50);
        UPDATE skills SET type = 'ADAPTABILITY' WHERE type IS NULL;
        ALTER TABLE skills MODIFY COLUMN type VARCHAR(50) NOT NULL;
    END IF;

    IF EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
               WHERE TABLE_SCHEMA = DATABASE()
                 AND TABLE_NAME = 'skills'
                 AND COLUMN_NAME = 'icon') THEN
        ALTER TABLE skills DROP COLUMN icon;
    END IF;
END$$

DELIMITER ;

CALL migrate_v25();
DROP PROCEDURE migrate_v25;
