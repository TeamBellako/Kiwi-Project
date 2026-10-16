package com.kiwi.features.localization;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class QueryBuildingUtils {

    private static final Pattern TABLE_PATTERN = Pattern.compile("(?i)\\b(?:from|join)\\s+`?([A-Za-z_][A-Za-z0-9_]*)");

    private final LocalizationService localizationService;

    public QueryBuildingUtils(LocalizationService localizationService) {
        this.localizationService = localizationService;
    }

    // Las tablas se infieren de los FROM/JOIN de la propia query.
    public String buildLocalizedQuery(String queryTemplate, String languagePreference) {
        Set<String> localizedColumns = new HashSet<>();
        Matcher matcher = TABLE_PATTERN.matcher(queryTemplate);
        while (matcher.find()) {
            localizedColumns.addAll(Arrays.asList(localizationService.getLocalizedColumnsForTable(matcher.group(1))));
        }
        return rewrite(queryTemplate, localizedColumns, languagePreference);
    }

    private String rewrite(String queryTemplate, Set<String> localizedColumns, String languagePreference) {
        String language = LocalizationService.normalizeLanguagePreference(languagePreference);
        if (language.equals("en")) {
            return queryTemplate;
        }

        StringBuilder query = new StringBuilder(queryTemplate.length());
        int index = 0;

        while (index < queryTemplate.length()) {
            char current = queryTemplate.charAt(index);
            if (current == '\'' || current == '"') {
                index = appendQuoted(queryTemplate, query, index, current);
            } else if (current == '-' && hasNext(queryTemplate, index, '-')) {
                index = appendLineComment(queryTemplate, query, index);
            } else if (current == '/' && hasNext(queryTemplate, index, '*')) {
                index = appendBlockComment(queryTemplate, query, index);
            } else if (current == ':' && index + 1 < queryTemplate.length()
                    && isIdentifierStart(queryTemplate.charAt(index + 1))) {
                index = appendParameter(queryTemplate, query, index);
            } else if (isIdentifierStart(current)) {
                int end = index + 1;
                while (end < queryTemplate.length() && isIdentifierPart(queryTemplate.charAt(end))) {
                    end++;
                }
                String identifier = queryTemplate.substring(index, end);
                query.append(localizeIdentifier(identifier, localizedColumns, language));
                index = end;
            } else if (current == '`') {
                index = appendQuotedIdentifier(queryTemplate, query, index, localizedColumns, language);
            } else {
                query.append(current);
                index++;
            }
        }
        return query.toString();
    }

    private String localizeIdentifier(String identifier, Set<String> localizedColumns, String language) {
        if (!identifier.endsWith("_en")) {
            return identifier;
        }
        String column = identifier.substring(0, identifier.length() - 3);
        return localizedColumns.contains(column) ? column + "_" + language : identifier;
    }

    private int appendQuotedIdentifier(
            String queryTemplate,
            StringBuilder query,
            int start,
            Set<String> localizedColumns,
            String language) {
        int end = queryTemplate.indexOf('`', start + 1);
        if (end < 0) {
            query.append(queryTemplate.substring(start));
            return queryTemplate.length();
        }
        String identifier = queryTemplate.substring(start + 1, end);
        query.append('`').append(localizeIdentifier(identifier, localizedColumns, language)).append('`');
        return end + 1;
    }

    private int appendQuoted(String source, StringBuilder target, int start, char quote) {
        int index = start;
        target.append(source.charAt(index++));
        while (index < source.length()) {
            char current = source.charAt(index++);
            target.append(current);
            if (current == '\\' && index < source.length()) {
                target.append(source.charAt(index++));
            } else if (current == quote) {
                if (index < source.length() && source.charAt(index) == quote) {
                    target.append(source.charAt(index++));
                } else {
                    break;
                }
            }
        }
        return index;
    }

    private int appendParameter(String source, StringBuilder target, int start) {
        int index = start;
        target.append(source.charAt(index++));
        while (index < source.length() && isIdentifierPart(source.charAt(index))) {
            target.append(source.charAt(index++));
        }
        return index;
    }

    private int appendLineComment(String source, StringBuilder target, int start) {
        int index = start;
        while (index < source.length()) {
            char current = source.charAt(index++);
            target.append(current);
            if (current == '\n') {
                break;
            }
        }
        return index;
    }

    private int appendBlockComment(String source, StringBuilder target, int start) {
        int end = source.indexOf("*/", start + 2);
        if (end < 0) {
            target.append(source.substring(start));
            return source.length();
        }
        target.append(source, start, end + 2);
        return end + 2;
    }

    private boolean hasNext(String source, int index, char expected) {
        return index + 1 < source.length() && source.charAt(index + 1) == expected;
    }

    private boolean isIdentifierStart(char value) {
        return value == '_' || Character.isLetter(value);
    }

    private boolean isIdentifierPart(char value) {
        return value == '_' || Character.isLetterOrDigit(value);
    }
}