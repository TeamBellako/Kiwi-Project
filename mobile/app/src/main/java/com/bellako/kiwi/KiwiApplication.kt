package com.bellako.kiwi

import android.app.Application
import android.content.Context
import com.bellako.kiwi.analytics.FirebaseEventNames
import com.bellako.kiwi.analytics.firebaseLogEvent
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.analytics
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class KiwiApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initFirebase(this)
        firebaseLogEvent(FirebaseEventNames.APP_OPENED)
    }
}

private fun initFirebase(context: Context) {
    FirebaseApp.initializeApp(context)
    Firebase.analytics.setAnalyticsCollectionEnabled(true)
}
