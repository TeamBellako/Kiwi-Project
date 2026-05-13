DROP PROCEDURE IF EXISTS migrate_v18;

DELIMITER $$

CREATE PROCEDURE migrate_v18()
BEGIN
    IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE TABLE_SCHEMA = DATABASE()
                     AND TABLE_NAME = 'conversations'
                     AND COLUMN_NAME = 'incidence_for_next_event') THEN
        ALTER TABLE conversations ADD COLUMN incidence_for_next_event VARCHAR(255);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE TABLE_SCHEMA = DATABASE()
                     AND TABLE_NAME = 'conversations'
                     AND COLUMN_NAME = 'fallback_event_id') THEN
        ALTER TABLE conversations ADD COLUMN fallback_event_id BIGINT;
    END IF;
END$$

DELIMITER ;

CALL migrate_v18();
DROP PROCEDURE migrate_v18;
