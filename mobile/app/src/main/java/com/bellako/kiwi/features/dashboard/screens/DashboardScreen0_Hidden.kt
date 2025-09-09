package com.bellako.kiwi.features.dashboard.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun DashboardScreen0_Hidden() {
    ComposableEngagementMeasuring("hidden")
}


// -------------------------------------------------------------------------------------------------

@RequiresApi(Build.VERSION_CODES.O)
@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Composable
fun DashboardModal0_Hidden_Preview() {
    DashboardModal_Preview(false, 0)
}
