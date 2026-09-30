package com.bellako.kiwi.common

import android.content.Context

/**
 * ResourceProvider provides a centralized way to access localized strings,
 * plurals, and arrays. It is designed to be pluggable, allowing for
 * different resource sources (e.g., local Android resources or remote updates).
 */
object ResourceProvider {
    private var repository: ResourceRepository? = null

    /**
     * Initializes the ResourceProvider with a context.
     * Should be called in Application.onCreate().
     */
    fun init(context: Context) {
        // Default implementation using Android's standard resources
        repository = AndroidResourceRepository(context)
    }

    /**
     * Returns the string associated with the given [key].
     * Returns the [key] itself as a fallback if the resource is not found.
     */
    fun stringResource(key: String): String {
        return repository?.stringResource(key) ?: key
    }

    /**
     * Returns a plural string associated with the given [key] and [count].
     */
    fun pluralResource(key: String, count: Int, vararg args: Any): String {
        return repository?.pluralResource(key, count, *args) ?: key
    }

    /**
     * Returns an array of strings associated with the given [key].
     */
    fun arrayResource(key: String): List<String> {
        return repository?.arrayResource(key) ?: emptyList()
    }

    /**
     * Allows changing the resource repository at runtime (e.g., after downloading new resources).
     */
    fun setRepository(newRepository: ResourceRepository) {
        this.repository = newRepository
    }
}
