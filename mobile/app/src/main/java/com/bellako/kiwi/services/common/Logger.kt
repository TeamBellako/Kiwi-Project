package com.bellako.kiwi.services.common

import android.util.Log
import com.bellako.kiwi.BuildConfig

object Logger {
    private const val TAG = "Kiwi-Logs"
    private val isProd = BuildConfig.DEBUG.not()

    // Enum for log levels
    enum class LogLevel { DEBUG, INFO, WARN, ERROR }

    private fun log(level: LogLevel, message: String, throwable: Throwable? = null) {
        if (isProd && level == LogLevel.DEBUG) return

        val timestamp = System.currentTimeMillis()
        val logMessage = "[$timestamp] [$level] $message"
        when (level) {
            LogLevel.DEBUG -> Log.d(TAG, logMessage, throwable)
            LogLevel.INFO -> Log.i(TAG, logMessage, throwable)
            LogLevel.WARN -> Log.w(TAG, logMessage, throwable)
            LogLevel.ERROR -> Log.e(TAG, logMessage, throwable)
        }
    }

    fun debug(message: String, throwable: Throwable? = null) = log(LogLevel.DEBUG, message, throwable)
    fun info(message: String, throwable: Throwable? = null) = log(LogLevel.INFO, message, throwable)
    fun warn(message: String, throwable: Throwable? = null) = log(LogLevel.WARN, message, throwable)
    fun error(message: String, throwable: Throwable? = null) = log(LogLevel.ERROR, message, throwable)
}
