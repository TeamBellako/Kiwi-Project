package com.bellako.kiwi

import android.app.Application
import androidx.compose.ui.platform.LocalContext
import com.bellako.kiwi.analytics.FirebaseEventLogger
import com.bellako.kiwi.analytics.FirebaseEventNames
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.analytics
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class KiwiApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)

        FirebaseEventLogger.logEvent(FirebaseEventNames.APP_OPENED)

        if (!BuildConfig.DEBUG) {
            Firebase.analytics.setAnalyticsCollectionEnabled(true)
        }
    }
}