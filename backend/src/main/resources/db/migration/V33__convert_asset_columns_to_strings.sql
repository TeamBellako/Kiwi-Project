-- Asset references (sprite/background/fx/sfx) are no longer FK ids into the
-- sprites/backgrounds/fx tables: callers now resolve assets by string key.
-- The conversations.expresion FK is removed entirely (expression is rolled
-- into the sprite key now). The four asset tables (sprites, expressions,
-- backgrounds, fx) are dropped at the end since nothing references them.
--
-- This migration drops the FK constraints, converts the surviving columns
-- from BIGINT to VARCHAR by backfilling each row with the asset name the
-- old FK was pointing at, and then drops the asset tables.
--
-- The whole body is idempotent: every column/constraint check looks at the
-- live schema, so a re-run after a successful migration becomes a no-op.

DROP PROCEDURE IF EXISTS migrate_v33;

DELIMITER $$

CREATE PROCEDURE migrate_v33()
BEGIN
    DECLARE fk_name VARCHAR(255);

    -- ============================================================
    -- conversations.sprite : BIGINT FK -> VARCHAR(255) NOT NULL
    -- ============================================================
    IF EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
               WHERE TABLE_SCHEMA = DATABASE()
                 AND TABLE_NAME = 'conversations'
                 AND COLUMN_NAME = 'sprite'
                 AND DATA_TYPE = 'bigint') THEN

        SELECT CONSTRAINT_NAME INTO fk_name
        FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'conversations'
          AND COLUMN_NAME = 'sprite'
          AND REFERENCED_TABLE_NAME = 'sprites'
        LIMIT 1;

        IF fk_name IS NOT NULL THEN
            SET @sql = CONCAT('ALTER TABLE conversations DROP FOREIGN KEY ', fk_name);
            PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
        END IF;

        ALTER TABLE conversations ADD COLUMN sprite_str VARCHAR(255) NULL;
        UPDATE conversations c
            LEFT JOIN sprites s ON c.sprite = s.id
            SET c.sprite_str = s.name;
        ALTER TABLE conversations DROP COLUMN sprite;
        ALTER TABLE conversations CHANGE COLUMN sprite_str sprite VARCHAR(255) NOT NULL;
    END IF;

    -- ============================================================
    -- conversations.background : BIGINT FK -> VARCHAR(255) NULL
    -- ============================================================
    SET fk_name = NULL;
    IF EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
               WHERE TABLE_SCHEMA = DATABASE()
                 AND TABLE_NAME = 'conversations'
                 AND COLUMN_NAME = 'background'
                 AND DATA_TYPE = 'bigint') THEN

        SELECT CONSTRAINT_NAME INTO fk_name
        FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'conversations'
          AND COLUMN_NAME = 'background'
          AND REFERENCED_TABLE_NAME = 'backgrounds'
        LIMIT 1;

        IF fk_name IS NOT NULL THEN
            SET @sql = CONCAT('ALTER TABLE conversations DROP FOREIGN KEY ', fk_name);
            PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
        END IF;

        ALTER TABLE conversations ADD COLUMN background_str VARCHAR(255) NULL;
        UPDATE conversations c
            LEFT JOIN backgrounds b ON c.background = b.id
            SET c.background_str = b.name;
        ALTER TABLE conversations DROP COLUMN background;
        ALTER TABLE conversations CHANGE COLUMN background_str background VARCHAR(255) NULL;
    END IF;

    -- ============================================================
    -- conversations.expresion : BIGINT FK -> dropped
    -- ============================================================
    SET fk_name = NULL;
    IF EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
               WHERE TABLE_SCHEMA = DATABASE()
                 AND TABLE_NAME = 'conversations'
                 AND COLUMN_NAME = 'expresion') THEN

        SELECT CONSTRAINT_NAME INTO fk_name
        FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'conversations'
          AND COLUMN_NAME = 'expresion'
          AND REFERENCED_TABLE_NAME = 'expressions'
        LIMIT 1;

        IF fk_name IS NOT NULL THEN
            SET @sql = CONCAT('ALTER TABLE conversations DROP FOREIGN KEY ', fk_name);
            PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
        END IF;

        ALTER TABLE conversations DROP COLUMN expresion;
    END IF;

    -- ============================================================
    -- conversations.fx : BIGINT FK -> VARCHAR(255) NULL
    -- ============================================================
    SET fk_name = NULL;
    IF EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
               WHERE TABLE_SCHEMA = DATABASE()
                 AND TABLE_NAME = 'conversations'
                 AND COLUMN_NAME = 'fx'
                 AND DATA_TYPE = 'bigint') THEN

        SELECT CONSTRAINT_NAME INTO fk_name
        FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'conversations'
          AND COLUMN_NAME = 'fx'
          AND REFERENCED_TABLE_NAME = 'fx'
        LIMIT 1;

        IF fk_name IS NOT NULL THEN
            SET @sql = CONCAT('ALTER TABLE conversations DROP FOREIGN KEY ', fk_name);
            PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
        END IF;

        ALTER TABLE conversations ADD COLUMN fx_str VARCHAR(255) NULL;
        UPDATE conversations c
            LEFT JOIN fx f ON c.fx = f.id
            SET c.fx_str = f.name;
        ALTER TABLE conversations DROP COLUMN fx;
        ALTER TABLE conversations CHANGE COLUMN fx_str fx VARCHAR(255) NULL;
    END IF;

    -- ============================================================
    -- combat_configs.background : BIGINT FK -> VARCHAR(255) NULL
    -- (FK was named fk_combat_configs_background in V28)
    -- ============================================================
    IF EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
               WHERE TABLE_SCHEMA = DATABASE()
                 AND TABLE_NAME = 'combat_configs'
                 AND COLUMN_NAME = 'background'
                 AND DATA_TYPE = 'bigint') THEN

        IF EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS
                   WHERE TABLE_SCHEMA = DATABASE()
                     AND TABLE_NAME = 'combat_configs'
                     AND CONSTRAINT_NAME = 'fk_combat_configs_background') THEN
            ALTER TABLE combat_configs DROP FOREIGN KEY fk_combat_configs_background;
        END IF;

        ALTER TABLE combat_configs ADD COLUMN background_str VARCHAR(255) NULL;
        UPDATE combat_configs cc
            LEFT JOIN backgrounds b ON cc.background = b.id
            SET cc.background_str = b.name;
        ALTER TABLE combat_configs DROP COLUMN background;
        ALTER TABLE combat_configs CHANGE COLUMN background_str background VARCHAR(255) NULL;
    END IF;

    -- ============================================================
    -- combat_configs.sfx : BIGINT FK -> VARCHAR(255) NULL
    -- (FK was named fk_combat_configs_sfx in V28)
    -- ============================================================
    IF EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
               WHERE TABLE_SCHEMA = DATABASE()
                 AND TABLE_NAME = 'combat_configs'
                 AND COLUMN_NAME = 'sfx'
                 AND DATA_TYPE = 'bigint') THEN

        IF EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS
                   WHERE TABLE_SCHEMA = DATABASE()
                     AND TABLE_NAME = 'combat_configs'
                     AND CONSTRAINT_NAME = 'fk_combat_configs_sfx') THEN
            ALTER TABLE combat_configs DROP FOREIGN KEY fk_combat_configs_sfx;
        END IF;

        ALTER TABLE combat_configs ADD COLUMN sfx_str VARCHAR(255) NULL;
        UPDATE combat_configs cc
            LEFT JOIN fx f ON cc.sfx = f.id
            SET cc.sfx_str = f.name;
        ALTER TABLE combat_configs DROP COLUMN sfx;
        ALTER TABLE combat_configs CHANGE COLUMN sfx_str sfx VARCHAR(255) NULL;
    END IF;

    -- ============================================================
    -- All FKs into the asset tables are gone, so the tables can go.
    -- ============================================================
    DROP TABLE IF EXISTS sprites;
    DROP TABLE IF EXISTS expressions;
    DROP TABLE IF EXISTS backgrounds;
    DROP TABLE IF EXISTS fx;
END$$

DELIMITER ;

CALL migrate_v33();
DROP PROCEDURE migrate_v33;
