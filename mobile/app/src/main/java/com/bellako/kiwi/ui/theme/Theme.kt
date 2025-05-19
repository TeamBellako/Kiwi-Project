package com.bellako.kiwi.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

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
