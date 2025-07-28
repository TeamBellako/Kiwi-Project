package com.bellako.kiwi.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = KiwiPrimary,
    inversePrimary = KiwiInversePrimary,
    secondary = KiwiSecondary,
    tertiary = KiwiTertiary,
    background = KiwiBackground,
    primaryContainer = KiwiContainer,
    surface = KiwiSurface,
    outline = KiwiOutline,
    error = KiwiError
)

private val DarkColors = darkColorScheme(
    primary = KiwiPrimary,
    inversePrimary = KiwiInversePrimary,
    secondary = KiwiSecondary,
    tertiary = KiwiTertiary,
    background = KiwiBackground,
    primaryContainer = KiwiContainer,
    surface = KiwiSurface,
    outline = KiwiOutline,
    error = KiwiError
)

@Composable
fun KiwiTheme(
    useDarkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (useDarkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = kiwiTypography,
        content = content
    )
}
