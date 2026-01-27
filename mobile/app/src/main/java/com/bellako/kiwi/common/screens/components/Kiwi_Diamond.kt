package com.bellako.kiwi.common.screens.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate

@Composable
fun Kiwi_Diamond(
    size: androidx.compose.ui.unit.Dp,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(
        modifier = modifier.size(size),
    ) {
        val diamondSize = this.size.minDimension
        val centerX = this.size.width / 2
        val centerY = this.size.height / 2
        kiwiDiamondShape(color, centerX, centerY, diamondSize)
    }
}

@Suppress("MagicNumber")
fun DrawScope.kiwiDiamondShape(
    color: Color,
    offsetX: Float,
    offsetY: Float,
    size: Float,
) {
    val squareSide = size / kotlin.math.sqrt(2f)

    rotate(
        45f,
        pivot = androidx.compose.ui.geometry.Offset(offsetX, offsetY),
    ) {
        drawRect(
            color = color,
            topLeft = androidx.compose.ui.geometry.Offset(
                offsetX - squareSide / 2,
                offsetY - squareSide / 2,
            ),
            size = androidx.compose.ui.geometry.Size(squareSide, squareSide),
        )
    }
}
