package com.bellako.kiwi.common.data

import androidx.compose.ui.graphics.Color

fun multiplyColorRgb(
    color: Color,
    factor: Float,
): Color =
    color.copy(
        red = color.red * factor,
        green = color.green * factor,
        blue = color.blue * factor,
    )
