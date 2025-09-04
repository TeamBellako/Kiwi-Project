package com.bellako.kiwi.analytics

import android.content.Context
import android.os.Bundle
import com.bellako.kiwi.BuildConfig
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.analytics

fun firebaseFunctionWrapper(function: (() -> Unit)) {
    try {
        function.invoke()
    } catch (_: ExceptionInInitializerError) {
    } catch (_: NoClassDefFoundError) {
    }
}

fun firebaseInit(context: Context) {
    firebaseFunctionWrapper {
        FirebaseApp.initializeApp(context)
        Firebase.analytics.setAnalyticsCollectionEnabled(true)
    }
}

fun firebaseSetUserId(id: String) {
    firebaseFunctionWrapper {
        Firebase.analytics.setUserId(id)
    }
}

/**
 * Generic logging method for all Firebase events.
 * @param eventName Name of the event (use constants!)
 * @param params Optional map of parameters to include with the event
 */
fun firebaseLogEvent(
    eventName: String,
    params: Map<String, Any>? = null,
) {
    firebaseFunctionWrapper {
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

        Firebase.analytics.logEvent(eventName, bundle)
    }
}
