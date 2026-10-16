package com.kiwi.features.skills.controllers;

import com.kiwi.features.localization.LocalizationQueryRewriter;
import com.kiwi.features.skills.data.persistence.SkillPersistence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillRepository extends JpaRepository<SkillPersistence, Long> {

    interface LocalizedSkill {
        Long getId();
        String getName();
        String getDescription();
        String getQuote();
        String getCooldownOtherDescription();
    }

        // Proyección para que la consulta localizada no reutilice entidades gestionadas.
    @Query(value = """
            SELECT id,
                 name_en AS name,
                 description_en AS description,
                 quote_en AS quote,
                 cooldown_other_description_en AS cooldownOtherDescription
            FROM skills
            WHERE id IN (:ids)
            """, nativeQuery = true, queryRewriter = LocalizationQueryRewriter.class)
    List<LocalizedSkill> findLocalizedByIds(@Param("ids") List<Long> ids);
}
