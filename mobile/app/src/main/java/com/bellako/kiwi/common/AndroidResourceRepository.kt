package com.bellako.kiwi.common

import android.content.Context
import android.content.res.Configuration
import android.os.LocaleList

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
        if (key.toIntOrNull() != null) return key
        val resId = localizedContext.resources.getIdentifier(key, "string", context.packageName)
        return if (resId != 0) {
            localizedContext.getString(resId)
        } else {
            key
        }
    }

    override fun formattedStringResource(
        key: String,
        vararg args: Any,
    ): String {
        return formattedStringResource(key, args.toList())
    }

    override fun formattedStringResource(
        key: String,
        args: List<Any>,
    ): String {
        if (key.toIntOrNull() != null) return key
        val resId = localizedContext.resources.getIdentifier(key, "string", context.packageName)
        return if (resId != 0) {
            @Suppress("SpreadOperator")
            localizedContext.getString(resId, *args.toTypedArray())
        } else {
            key
        }
    }

    override fun pluralResource(key: String, count: Int, vararg args: Any): String {
        if (key.toIntOrNull() != null) return key
        val resId = localizedContext.resources.getIdentifier(key, "plurals", context.packageName)
        return if (resId != 0) {
            localizedContext.resources.getQuantityString(resId, count, *args)
        } else {
            key
        }
    }

    override fun arrayResource(key: String): List<String> {
        if (key.toIntOrNull() != null) return emptyList()
        val resId = localizedContext.resources.getIdentifier(key, "array", context.packageName)
        return if (resId != 0) {
            localizedContext.resources.getStringArray(resId).toList()
        } else {
            emptyList()
        }
    }
}
