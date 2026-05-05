DROP PROCEDURE IF EXISTS migrate_v33;

DELIMITER $$

CREATE PROCEDURE migrate_v33()
BEGIN
	ALTER TABLE skills
	DROP COLUMN type,
	ADD COLUMN element_id BIGINT NOT NULL DEFAULT 1 AFTER levelup_skill_id,
	ADD INDEX fk_skills_elements_idx (element_id ASC) VISIBLE;
	
	ALTER TABLE skills
	ADD CONSTRAINT fk_skills_elements
	  FOREIGN KEY element_id
	  REFERENCES combat_elements(id)
	  ON DELETE CASCADE
	  ON UPDATE CASCADE;

	ALTER TABLE skill_effects
	DROP FOREIGN KEY fk_skill_effects_elements;
	ALTER TABLE skill_effects 
	DROP COLUMN element_id,
	DROP INDEX fk_skill_effects_elements;
	
	ALTER TABLE combat_active_status
	ADD COLUMN stat_affected ENUM('CURRENT_HP', 'MAX_HP', 'PATK', 'MATK', 'PDEF', 'MDEF', 'ACC', 'EVA', 'LCK') NULL DEFAULT NULL AFTER remaining_turns;

	ALTER TABLE combat_log
	CHANGE COLUMN effect_type effect_type ENUM('DAMAGE', 'MISS_DAMAGE', 'HEAL', 'MODIFY_STAT', 'STATUS_APPLIED', 'STATUS_REMOVED', 'MISS_STATUS', 'IMMUNE') NULL DEFAULT NULL;
	
	ALTER TABLE combat_log
	CHANGE COLUMN stat_affected stat_affected ENUM('CURRENT_HP', 'MAX_HP', 'PATK', 'MATK', 'PDEF', 'MDEF', 'ACC', 'EVA', 'LCK', 'SHIELD') NULL DEFAULT NULL;

	ALTER TABLE user_stats
	ADD COLUMN shield INT NOT NULL AFTER lck;
	
	ALTER TABLE enemies 
	ADD COLUMN shield INT NOT NULL AFTER lck;
	
	ALTER TABLE combat_active_status
	CHANGE COLUMN stat_affected stat_affected ENUM('CURRENT_HP', 'MAX_HP', 'PATK', 'MATK', 'PDEF', 'MDEF', 'ACC', 'EVA', 'LCK', 'SHIELD') NULL DEFAULT NULL;

END$$

DELIMITER ;

CALL migrate_v33();
DROP PROCEDURE migrate_v33;