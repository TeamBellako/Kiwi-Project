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
	
END$$

DELIMITER ;

CALL migrate_v33();
DROP PROCEDURE migrate_v33;