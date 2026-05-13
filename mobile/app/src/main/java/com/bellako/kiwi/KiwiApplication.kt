package com.bellako.kiwi

import android.app.Application
import androidx.core.content.edit
import com.bellako.kiwi.analytics.FirebaseEventNames
import com.bellako.kiwi.analytics.firebaseInit
import com.bellako.kiwi.analytics.firebaseLogEvent
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class KiwiApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        firebaseInit(this)

        val prefs = getSharedPreferences("kiwi_prefs", MODE_PRIVATE)
        val isFirstOpen = prefs.getBoolean("is_first_open", true)

        if (isFirstOpen) {
            firebaseLogEvent(FirebaseEventNames.USER_ACQUIRED)
            prefs.edit { putBoolean("is_first_open", false) }
        }

        firebaseLogEvent(FirebaseEventNames.APP_OPENED)
    }
}
