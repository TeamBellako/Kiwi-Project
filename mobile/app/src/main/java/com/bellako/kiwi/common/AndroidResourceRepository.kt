package com.bellako.kiwi.common

import android.content.Context
import android.util.Log

/**
 * Implementation of [ResourceRepository] that uses standard Android resources (strings.xml).
 */
class AndroidResourceRepository(private val context: Context) : ResourceRepository {

    override fun stringResource(key: String): String {
        val resId = context.resources.getIdentifier(key, "string", context.packageName)
        return if (resId != 0) {
            context.getString(resId)
        } else {
            Log.w("AndroidResourceRepository", "Resource not found for key: $key")
            key
        }
    }

    override fun pluralResource(key: String, count: Int, vararg args: Any): String {
        val resId = context.resources.getIdentifier(key, "plurals", context.packageName)
        return if (resId != 0) {
            context.resources.getQuantityString(resId, count, *args)
        } else {
            Log.w("AndroidResourceRepository", "Plural resource not found for key: $key")
            key
        }
    }

    override fun arrayResource(key: String): List<String> {
        val resId = context.resources.getIdentifier(key, "array", context.packageName)
        return if (resId != 0) {
            context.resources.getStringArray(resId).toList()
        } else {
            Log.w("AndroidResourceRepository", "Array resource not found for key: $key")
            emptyList()
        }
    }
}
