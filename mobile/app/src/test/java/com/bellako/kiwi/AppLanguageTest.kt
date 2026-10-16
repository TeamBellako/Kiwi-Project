package com.bellako.kiwi

import android.content.Context
import android.content.res.Configuration
import androidx.core.content.edit
import com.bellako.kiwi.common.AppLanguage
import com.bellako.kiwi.common.AppLanguageManager
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import java.util.Locale

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class AppLanguageTest {
    private val application = RuntimeEnvironment.getApplication()
    private val preferences = application.getSharedPreferences("kiwi_prefs", Context.MODE_PRIVATE)

    @Before
    fun clearLanguagePreference() {
        preferences.edit { remove("app_language") }
    }

    @Test
    fun initializePersistsDetectedSpanishVariantOnFirstOpen() {
        AppLanguageManager.initialize(contextWithLocale("es-MX"))

        assertEquals(AppLanguage.SPANISH, AppLanguageManager.language.value)
        assertEquals("es-ES", preferences.getString("app_language", null))
    }

    @Test
    fun initializePersistsDetectedGenericSpanishOnFirstOpen() {
        AppLanguageManager.initialize(contextWithLocale("es"))

        assertEquals(AppLanguage.SPANISH, AppLanguageManager.language.value)
        assertEquals("es-ES", preferences.getString("app_language", null))
    }

    @Test
    fun initializeUsesEnglishForUnsupportedDeviceLocale() {
        AppLanguageManager.initialize(contextWithLocale("fr-FR"))

        assertEquals(AppLanguage.ENGLISH, AppLanguageManager.language.value)
        assertEquals("en", preferences.getString("app_language", null))
    }

    @Test
    fun initializePrefersSavedLanguageOverDeviceLocale() {
        preferences.edit { putString("app_language", "en") }

        AppLanguageManager.initialize(contextWithLocale("es-ES"))

        assertEquals(AppLanguage.ENGLISH, AppLanguageManager.language.value)
    }

    private fun contextWithLocale(languageTag: String) =
        application.createConfigurationContext(
            Configuration(application.resources.configuration).apply {
                val locale = Locale.forLanguageTag(languageTag)
                setLocale(locale)
            },
        )
}
