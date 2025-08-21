package com.bellako.kiwi.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private const val DEFAULT_WIDTH = 392.0f
private const val DEFAULT_HEIGHT = 800.0f

@Composable
fun getScreenWidth(): Float = LocalConfiguration.current.screenWidthDp.toFloat()

@Composable
fun getScreenHeight(
    withoutInsetTop: Boolean = false,
    withoutInsetBottom: Boolean = false,
): Float {
    val insets = WindowInsets.systemBars.asPaddingValues()
    val insetTop = if (withoutInsetTop) insets.calculateTopPadding() else 0.dp
    val insetBottom = if (withoutInsetBottom) insets.calculateBottomPadding() else 0.dp
    return (LocalConfiguration.current.screenHeightDp.dp - insetTop - insetBottom).value
}

/** Resize the passed size depending on the current window size. Medium screen is taken as default. */
@Composable
fun getResponsiveSizeWidth(size: Int): Int = getResponsiveSizeWidth(size.dp).value.toInt()

/** Resize the passed size depending on the current window size. Medium screen is taken as default. */
@Composable
fun getResponsiveSizeHeight(size: Int): Int = getResponsiveSizeHeight(size.dp).value.toInt()

/** Resize the passed size depending on the current window size. Medium screen is taken as default. */
@Composable
fun getResponsiveSizeWidth(size: Dp): Dp = size * (getScreenWidth() / DEFAULT_WIDTH)

/** Resize the passed size depending on the current window size. Medium screen is taken as default. */
@Composable
fun getResponsiveSizeHeight(size: Dp): Dp = size * (getScreenHeight() / DEFAULT_HEIGHT)

/** Returns the passed percentage (0f..1f) of the screen size width. */
@Composable
fun getResponsiveSizeRelativeWidth(percentage: Float): Dp = (getScreenWidth() * percentage.coerceIn(0f, 1f)).dp

/** Returns the passed percentage (0f..1f) of the screen size height. */
@Composable
fun getResponsiveSizeRelativeHeight(percentage: Float): Dp = (getScreenHeight() * percentage.coerceIn(0f, 1f)).dp
