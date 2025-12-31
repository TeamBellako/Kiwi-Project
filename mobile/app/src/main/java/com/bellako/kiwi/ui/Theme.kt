package com.bellako.kiwi.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Composable
fun Kiwi_Theme(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalKiwiColors provides KiwiColors,
    ) {
        MaterialTheme(
            typography = kiwiTypography,
            content = content,
        )
    }
}

@Immutable
data class KiwiColorsData(
    val color1: Color,
    val color1A: Color,
    val color1B: Color,
    val color2: Color,
    val color2A: Color,
    val color2B: Color,
    val color2C: Color,
    val color3: Color,
    val color3A: Color,
    val color4: Color,
    val color4A: Color,
    val color4B: Color,
    val color4C: Color,
    val color5: Color,
    val color5A: Color,
    val color5B: Color,
    val color5C: Color,
    val color6: Color,
    val color6A: Color,
    val color6B: Color,
    val color7: Color,
    val color7A: Color,
    val color7B: Color,
    val color7C: Color,
    val color7D: Color,
    val color8: Color,
    val color8A: Color,
    val color8B: Color,
    val color8C: Color,
    val color9: Color,
    val color9A: Color,
    val color9B: Color,
    val color9C: Color,
    val colorF: Color,
    val colorF1: Color,
    val color0: Color,
    val color0A: Color,
    val color0B: Color,
    val color0C: Color,
    val colorR: Color,
    val colorR1: Color,
    val colorOcean: Color,
)

val KiwiColors =
    KiwiColorsData(
        color1 = KiwiColor1,
        color1A = KiwiColor1A,
        color1B = KiwiColor1B,
        color2 = KiwiColor2,
        color2A = KiwiColor2A,
        color2B = KiwiColor2B,
        color2C = KiwiColor2C,
        color3 = KiwiColor3,
        color3A = KiwiColor3A,
        color4 = KiwiColor4,
        color4A = KiwiColor4A,
        color4B = KiwiColor4B,
        color4C = KiwiColor4C,
        color5 = KiwiColor5,
        color5A = KiwiColor5A,
        color5B = KiwiColor5B,
        color5C = KiwiColor5C,
        color6 = KiwiColor6,
        color6A = KiwiColor6A,
        color6B = KiwiColor6B,
        color7 = KiwiColor7,
        color7A = KiwiColor7A,
        color7B = KiwiColor7B,
        color7C = KiwiColor7C,
        color7D = KiwiColor7D,
        color8 = KiwiColor8,
        color8A = KiwiColor8A,
        color8B = KiwiColor8B,
        color8C = KiwiColor8C,
        color9 = KiwiColor9,
        color9A = KiwiColor9A,
        color9B = KiwiColor9B,
        color9C = KiwiColor9C,
        colorF = KiwiColorF,
        colorF1 = KiwiColorF1,
        color0 = KiwiColor0,
        color0A = KiwiColor0A,
        color0B = KiwiColor0B,
        color0C = KiwiColor0C,
        colorR = KiwiColorR,
        colorR1 = KiwiColorR1,
        colorOcean = KiwiColorOcean,
    )

val LocalKiwiColors = staticCompositionLocalOf { KiwiColors }
