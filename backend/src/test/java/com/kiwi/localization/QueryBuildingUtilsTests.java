package com.kiwi.localization;

import com.kiwi.features.localization.LocalizationService;
import com.kiwi.features.localization.QueryBuildingUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class QueryBuildingUtilsTests {
    private final QueryBuildingUtils queryBuildingUtils = new QueryBuildingUtils(new LocalizationService());

    @Test
    public void buildLocalizedQuery_rewritesOnlyLocalizedIdentifiers() {
        String template = """
                SELECT q.name_en AS name, q.description_en AS description, q.action_en
                FROM quests q
                WHERE q.id = :id AND q.name = :name_en AND q.note = 'name_en' -- description_en
                """;

        String localized = queryBuildingUtils.buildLocalizedQuery(template, "es");

        assertEquals("""
                SELECT q.name_es AS name, q.description_es AS description, q.action_en
                FROM quests q
                WHERE q.id = :id AND q.name = :name_en AND q.note = 'name_en' -- description_en
                """, localized);
    }

    @Test
    public void buildLocalizedQuery_infersTablesFromQuery() {
        String template = "SELECT COALESCE(dialog_en, dialog) AS dialog, name_en FROM conversations WHERE id = :id";

        assertEquals("SELECT COALESCE(dialog_es, dialog) AS dialog, name_en FROM conversations WHERE id = :id",
                queryBuildingUtils.buildLocalizedQuery(template, "es"));
    }

    @Test
    public void buildLocalizedQuery_localizesSubquestName() {
        String template = "SELECT name_en AS name FROM subquests WHERE id = :id";

        assertEquals("SELECT name_es AS name FROM subquests WHERE id = :id",
                queryBuildingUtils.buildLocalizedQuery(template, "es"));
    }

    @Test
    public void buildLocalizedQuery_usesBaseTemplateForUnsupportedLanguage() {
        String template = "SELECT action_en AS action FROM goals";

        assertEquals(template, queryBuildingUtils.buildLocalizedQuery(template, "fr"));
    }
}