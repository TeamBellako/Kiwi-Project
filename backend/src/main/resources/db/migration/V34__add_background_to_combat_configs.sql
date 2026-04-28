ALTER TABLE combat_configs
    ADD COLUMN background BIGINT NULL,
    ADD CONSTRAINT fk_combat_configs_background
        FOREIGN KEY (background)
        REFERENCES backgrounds(id)
        ON DELETE SET NULL
        ON UPDATE CASCADE;

UPDATE backgrounds SET name = 'background_mindveil' WHERE id = 1;

UPDATE combat_configs SET background = 1 WHERE id = 1;
