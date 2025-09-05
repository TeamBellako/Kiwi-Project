package com.bellako.kiwi

import android.app.Application
import com.bellako.kiwi.analytics.FirebaseEventNames
import com.bellako.kiwi.analytics.firebaseInit
import com.bellako.kiwi.analytics.firebaseLogEvent
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class KiwiApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        firebaseInit(this)
        firebaseLogEvent(FirebaseEventNames.APP_OPENED)
    }
}
