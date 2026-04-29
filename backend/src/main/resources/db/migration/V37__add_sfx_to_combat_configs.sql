ALTER TABLE combat_configs
    ADD COLUMN sfx BIGINT NULL,
    ADD CONSTRAINT fk_combat_configs_sfx
        FOREIGN KEY (sfx)
        REFERENCES fx(id)
        ON DELETE SET NULL
        ON UPDATE CASCADE;
