package com.bellako.kiwi.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
private fun getWindowScale(): Float {
    val defaultWidthDp = 392.0f
    val defaultHeightDp = 800.0f
    val configuration = LocalConfiguration.current
    return (configuration.screenWidthDp / defaultWidthDp + configuration.screenHeightDp / defaultHeightDp) / 2.0f
}

@Composable
fun getResponsiveRelativeSize(size: Int): Int {
    return (size * getWindowScale()).toInt()
}

@Composable
fun getResponsiveRelativeSize(size: Dp): Dp {
    return getResponsiveRelativeSize(size.value.toInt()).dp
}
