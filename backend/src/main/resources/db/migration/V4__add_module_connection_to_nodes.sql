DROP PROCEDURE IF EXISTS migrate_v4;

DELIMITER $$

CREATE PROCEDURE migrate_v4()
BEGIN
    IF EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
               WHERE TABLE_SCHEMA = DATABASE()
                 AND TABLE_NAME = 'nodes'
                 AND COLUMN_NAME = 'event_on_execution') THEN
        ALTER TABLE nodes DROP COLUMN event_on_execution;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE TABLE_SCHEMA = DATABASE()
                     AND TABLE_NAME = 'nodes'
                     AND COLUMN_NAME = 'on_execution_action') THEN
        ALTER TABLE nodes ADD COLUMN on_execution_action VARCHAR(255);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE TABLE_SCHEMA = DATABASE()
                     AND TABLE_NAME = 'nodes'
                     AND COLUMN_NAME = 'on_execution_entity') THEN
        ALTER TABLE nodes ADD COLUMN on_execution_entity VARCHAR(255);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE TABLE_SCHEMA = DATABASE()
                     AND TABLE_NAME = 'nodes'
                     AND COLUMN_NAME = 'on_execution_entity_id') THEN
        ALTER TABLE nodes ADD COLUMN on_execution_entity_id INT;
    END IF;
END$$

DELIMITER ;

CALL migrate_v4();
DROP PROCEDURE migrate_v4;
