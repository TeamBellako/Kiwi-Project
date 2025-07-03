package com.bellako.kiwi.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class DeviceSize { SMALL, MEDIUM, LARGE }

fun getDeviceSize(maxWidth: Dp): DeviceSize = when {
    maxWidth < 360.dp -> DeviceSize.SMALL
    maxWidth < 480.dp -> DeviceSize.MEDIUM
    else -> DeviceSize.LARGE
}
