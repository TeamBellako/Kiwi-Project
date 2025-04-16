package com.bellako.kiwi.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.ui.platform.LocalInspectionMode

private val LightColors = lightColorScheme(
    primary = KiwiPrimary,
    secondary = KiwiSecondary,
    tertiary = KiwiTertiary
)

private val DarkColors = darkColorScheme(
    primary = KiwiPrimary,
    secondary = KiwiSecondary,
    tertiary = KiwiTertiary
)

@Composable
fun KiwiTheme(
    useDarkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (useDarkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
