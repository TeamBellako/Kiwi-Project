package com.bellako.kiwi.common.utils

import android.app.AppOpsManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.Q)
fun hasUsageStatsPermission(context: Context): Boolean =
    try {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode =
            appOps.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                context.packageName,
            )
        mode == AppOpsManager.MODE_ALLOWED
    } catch (_: NullPointerException) {
        false
    }
