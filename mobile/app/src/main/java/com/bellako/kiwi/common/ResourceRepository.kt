package com.bellako.kiwi.common

/**
 * Interface for resource retrieval.
 * This allows the app to switch between local Android resources
 * and potentially remote/dynamic resources.
 */
interface ResourceRepository {
    fun stringResource(key: String): String
    fun pluralResource(key: String, count: Int, vararg args: Any): String
    fun arrayResource(key: String): List<String>
}
