package com.bellako.kiwi.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class DeviceSize { SMALL, MEDIUM, LARGE }

object Dimensions {
    val smallFontSize = 12.sp
    val mediumFontSize = 16.sp
    val largeFontSize = 20.sp
}

fun getDeviceSize(maxWidth: Dp): DeviceSize = when {
    maxWidth < 360.dp -> DeviceSize.SMALL
    maxWidth < 480.dp -> DeviceSize.MEDIUM
    else -> DeviceSize.LARGE
}
