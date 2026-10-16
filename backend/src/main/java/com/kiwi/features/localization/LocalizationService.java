package com.kiwi.features.localization;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
public class LocalizationService {
    private static final Set<String> SUPPORTED_LANGUAGES = Set.of("en", "es");

    // Define which columns are localized for each table
    private static final Map<String, String[]> LOCALIZED_COLUMNS = new HashMap<>();

    static {
        // Quests table columns that need localization
        LOCALIZED_COLUMNS.put("quests", new String[]{"name", "description"});

        // Subquests table columns that need localization
        LOCALIZED_COLUMNS.put("subquests", new String[]{"name"});

        // Skills table columns that need localization
        LOCALIZED_COLUMNS.put("skills", new String[]{"name", "description", "quote", "cooldown_other_description"});

        // Conversations table columns that need localization
        LOCALIZED_COLUMNS.put("conversations", new String[]{"dialog", "dialog_m", "dialog_w"});

        // Conversation options table columns that need localization
        LOCALIZED_COLUMNS.put("conversation_options", new String[]{"text", "text_m", "text_w"});

        // Nodes table columns that need localization
        LOCALIZED_COLUMNS.put("nodes", new String[]{"display_name"});

        // Goals table columns that need localization
        LOCALIZED_COLUMNS.put("goals", new String[]{"action"});

        // Tips table columns that need localization
        LOCALIZED_COLUMNS.put("tips", new String[]{"title", "text"});
    }

    public static String normalizeLanguagePreference(String languagePreference) {
        if (languagePreference == null) {
            return "en";
        }

        String normalized = languagePreference.trim().toLowerCase(Locale.ROOT);
        return SUPPORTED_LANGUAGES.contains(normalized) ? normalized : "en";
    }

    public String[] getLocalizedColumnsForTable(String tableName) {
        return tableName == null
                ? new String[0]
                : LOCALIZED_COLUMNS.getOrDefault(tableName.toLowerCase(Locale.ROOT), new String[0]);
    }
}