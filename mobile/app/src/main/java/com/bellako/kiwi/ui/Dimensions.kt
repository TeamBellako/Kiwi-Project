package com.bellako.kiwi.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private const val DEFAULT_WIDTH = 392.0f
private const val DEFAULT_HEIGHT = 800.0f

@Composable
fun getScreenWidth(): Float {
    return LocalConfiguration.current.screenWidthDp.toFloat()
}

@Composable
fun getScreenHeight(): Float {
    return LocalConfiguration.current.screenHeightDp.toFloat()
}

/** Resize the passed size depending on the current window size. Medium screen is taken as default. */
@Composable
fun getResponsiveSizeWidth(size: Int): Int {
    return getResponsiveSizeWidth(size.dp).value.toInt()
}

/** Resize the passed size depending on the current window size. Medium screen is taken as default. */
@Composable
fun getResponsiveSizeHeight(size: Int): Int {
    return getResponsiveSizeHeight(size.dp).value.toInt()
}

/** Resize the passed size depending on the current window size. Medium screen is taken as default. */
@Composable
fun getResponsiveSizeWidth(size: Dp): Dp {
    return size * (getScreenWidth() / DEFAULT_WIDTH)
}

/** Resize the passed size depending on the current window size. Medium screen is taken as default. */
@Composable
fun getResponsiveSizeHeight(size: Dp): Dp {
    return size * (getScreenHeight() / DEFAULT_HEIGHT)
}

/** Returns the passed percentage (0f..1f) of the screen size width. */
@Composable
fun getResponsiveSizeRelativeWidth(percentage: Float): Dp {
    return (getScreenWidth() * percentage.coerceIn(0f, 1f)).dp
}

/** Returns the passed percentage (0f..1f) of the screen size height. */
@Composable
fun getResponsiveSizeRelativeHeight(percentage: Float): Dp {
    return (getScreenHeight() * percentage.coerceIn(0f, 1f)).dp
}
