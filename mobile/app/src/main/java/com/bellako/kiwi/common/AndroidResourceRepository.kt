package com.bellako.kiwi.common

import android.content.Context
import android.content.res.Configuration
import android.os.LocaleList
import android.util.Log
import java.util.Locale

/**
 * Implementation of [ResourceRepository] that uses standard Android resources (strings.xml).
 */
class AndroidResourceRepository(private val context: Context) : ResourceRepository {
    private var localizedContext = context

    override fun setLocale(locale: Locale) {
        val configuration = Configuration(context.resources.configuration).apply {
            setLocale(locale)
            setLocales(LocaleList(locale))
        }
        localizedContext = context.createConfigurationContext(configuration)
    }

    override fun stringResource(key: String): String {
        val resId = localizedContext.resources.getIdentifier(key, "string", context.packageName)
        return if (resId != 0) {
            localizedContext.getString(resId)
        } else {
            Log.w("AndroidResourceRepository", "Resource not found for key: $key")
            key
        }
    }

    override fun formattedStringResource(
        key: String,
        vararg args: Any,
    ): String {
        val resId = localizedContext.resources.getIdentifier(key, "string", context.packageName)
        return if (resId != 0) {
            localizedContext.getString(resId, *args)
        } else {
            Log.w("AndroidResourceRepository", "Resource not found for key: $key")
            key
        }
    }

    override fun pluralResource(key: String, count: Int, vararg args: Any): String {
        val resId = localizedContext.resources.getIdentifier(key, "plurals", context.packageName)
        return if (resId != 0) {
            localizedContext.resources.getQuantityString(resId, count, *args)
        } else {
            Log.w("AndroidResourceRepository", "Plural resource not found for key: $key")
            key
        }
    }

    override fun arrayResource(key: String): List<String> {
        val resId = localizedContext.resources.getIdentifier(key, "array", context.packageName)
        return if (resId != 0) {
            localizedContext.resources.getStringArray(resId).toList()
        } else {
            Log.w("AndroidResourceRepository", "Array resource not found for key: $key")
            emptyList()
        }
    }
}
