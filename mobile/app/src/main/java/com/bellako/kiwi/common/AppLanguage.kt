package com.bellako.kiwi.common

import java.util.Locale

enum class AppLanguage(
    val locale: Locale,
    val flag: String,
    val labelKey: String,
) {
    ENGLISH(Locale.forLanguageTag("en"), "🇬🇧", "language_english"),
    SPANISH(Locale.forLanguageTag("es-ES"), "🇪🇸", "language_spanish"),
    ;

    fun next(): AppLanguage =
        when (this) {
            ENGLISH -> SPANISH
            SPANISH -> ENGLISH
        }

    companion object {
        fun fromTag(tag: String?): AppLanguage? {
            val requestedLocale = Locale.forLanguageTag(tag.orEmpty())
            return when {
                requestedLocale.language.equals(ENGLISH.locale.language, ignoreCase = true) -> ENGLISH
                requestedLocale.language.equals(SPANISH.locale.language, ignoreCase = true) -> SPANISH
                else -> null
            }
        }

        /**
         * Detects language from device locale and maps it to AppLanguage.
         * Returns SPANISH for any Spanish variant (es-ES, es-MX, etc.) or ENGLISH if not supported.
         */
        fun detectFromLocale(locale: Locale): AppLanguage =
            when (locale.language) {
                "en" -> ENGLISH

                "es" -> SPANISH

                // Any Spanish variant maps to Spanish
                else -> ENGLISH
            }
    }
}
