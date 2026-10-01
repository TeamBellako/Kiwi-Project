package com.bellako.kiwi.common

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.os.LocaleList
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object AppLanguageManager {
    private const val PREFERENCES_NAME = "kiwi_prefs"
    private const val LANGUAGE_KEY = "app_language"

    private val _language = MutableStateFlow(AppLanguage.ENGLISH)
    val language: StateFlow<AppLanguage> = _language

    fun initialize(context: Context) {
        val language =
            savedLanguage(context)
                ?: deviceLanguage(context).also { persistLanguage(context, it) }
        _language.value = language
        ResourceProvider.setLocale(language.locale)
    }

    fun setLanguage(
        context: Context,
        language: AppLanguage,
    ) {
        if (_language.value == language) return

        persistLanguage(context, language)
        _language.value = language
        ResourceProvider.setLocale(language.locale)
        context.findActivity()?.recreate()
    }

    fun cycle(context: Context) {
        setLanguage(context, _language.value.next())
    }

    fun wrapContext(context: Context): Context {
        val locale = (savedLanguage(context) ?: deviceLanguage(context)).locale
        val configuration =
            Configuration(context.resources.configuration).apply {
                setLocale(locale)
                setLocales(LocaleList(locale))
            }
        return context.createConfigurationContext(configuration)
    }

    private fun savedLanguage(context: Context): AppLanguage? =
        AppLanguage.fromTag(
            context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
                .getString(LANGUAGE_KEY, null),
        )

    private fun persistLanguage(
        context: Context,
        language: AppLanguage,
    ) {
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE).edit {
            putString(LANGUAGE_KEY, language.locale.toLanguageTag())
        }
    }

    private fun deviceLanguage(context: Context): AppLanguage {
        val deviceLocale = context.resources.configuration.locales[0]
        return AppLanguage.detectFromLocale(deviceLocale)
    }

    private tailrec fun Context.findActivity(): Activity? =
        when (this) {
            is Activity -> this
            is ContextWrapper -> baseContext.findActivity()
            else -> null
        }
}
