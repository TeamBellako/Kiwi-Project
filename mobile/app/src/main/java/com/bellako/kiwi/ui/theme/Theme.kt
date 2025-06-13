package com.bellako.kiwi.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = kiwiPrimaryColor,
    inversePrimary = kiwiInversePrimaryColor,
    secondary = kiwiSecondaryColor,
    tertiary = kiwiTertiaryColor,
    background = kiwiBackgroundColor,
    surface = kiwiSurfaceColor,
    outline = kiwiOutlineColor,
    error = kiwiErrorColor
)

private val DarkColors = darkColorScheme(
    primary = kiwiPrimaryColor,
    inversePrimary = kiwiInversePrimaryColor,
    secondary = kiwiSecondaryColor,
    tertiary = kiwiTertiaryColor,
    background = kiwiBackgroundColor,
    surface = kiwiSurfaceColor,
    outline = kiwiOutlineColor,
    error = kiwiErrorColor
)

@Composable
fun KiwiTheme(
    useDarkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (useDarkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = kiwiTypography,
        content = content
    )
}
