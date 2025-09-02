package com.bellako.kiwi.analytics

import android.os.Bundle
import com.bellako.kiwi.BuildConfig
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics

object FirebaseEventLogger {
    private val analytics = Firebase.analytics

    /**
     * Generic logging method for all Firebase events.
     *
     * @param eventName Name of the event (use constants!)
     * @param params Optional map of parameters to include with the event
     */
    fun logEvent(
        eventName: String,
        params: Map<String, Any>? = null,
    ) {
        val bundle = Bundle()

        params?.forEach { (key, value) ->
            when (value) {
                is String -> bundle.putString(key, value)
                is Int -> bundle.putInt(key, value)
                is Long -> bundle.putLong(key, value)
                is Float -> bundle.putFloat(key, value)
                is Double -> bundle.putDouble(key, value)
                is Boolean -> bundle.putBoolean(key, value)
                else -> throw IllegalArgumentException("Unsupported type for Firebase param: $key")
            }
        }

        bundle.putString("env", if (BuildConfig.DEBUG) "dev" else "prod")

        analytics.logEvent(eventName, bundle)
    }
}
