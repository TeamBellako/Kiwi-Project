package com.bellako.kiwi.common.utils

import android.content.Context
import com.bellako.kiwi.R

object AssetResolver {
    fun drawable(
        context: Context,
        name: String?,
    ): Int? {
        if (name.isNullOrBlank()) return null
        return lookup(context, name, "drawable") ?: R.drawable.unresolved_asset
    }

    fun raw(
        context: Context,
        name: String?,
    ): Int? = lookup(context, name, "raw")

    fun drawableOr(
        context: Context,
        name: String?,
        fallback: Int,
    ): Int = drawable(context, name) ?: fallback

    fun rawOr(
        context: Context,
        name: String?,
        fallback: Int,
    ): Int = raw(context, name) ?: fallback

    private fun lookup(
        context: Context,
        name: String?,
        defType: String,
    ): Int? {
        if (name.isNullOrBlank()) return null
        val id = context.resources.getIdentifier(name, defType, context.packageName)
        return if (id != 0) id else null
    }
}
