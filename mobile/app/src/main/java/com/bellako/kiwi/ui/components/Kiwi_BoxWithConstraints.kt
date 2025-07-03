package com.bellako.kiwi.ui.components

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bellako.kiwi.ui.theme.DeviceSize
import com.bellako.kiwi.ui.theme.getDeviceSize

@Suppress("UnusedBoxWithConstraintsScope")
@Composable
fun Kiwi_BoxWithConstraints(
    smallLayout: @Composable () -> Unit,
    mediumLayout: @Composable () -> Unit,
    bigLayout: @Composable () -> Unit,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val deviceSize = getDeviceSize(maxWidth)

        when (deviceSize) {
            DeviceSize.SMALL -> smallLayout()
            DeviceSize.MEDIUM -> mediumLayout()
            DeviceSize.LARGE -> bigLayout()
        }
    }
}