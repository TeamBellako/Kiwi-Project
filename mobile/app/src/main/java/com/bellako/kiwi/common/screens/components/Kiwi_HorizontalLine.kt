package com.bellako.kiwi.common.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bellako.kiwi.ui.getResponsiveSizeHeight

@Composable
fun Kiwi_HorizontalLine(
    width: Dp,
    height: Dp,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .width(getResponsiveSizeHeight(width))
                .height(getResponsiveSizeHeight(height))
                .clip(RoundedCornerShape(getResponsiveSizeHeight(2.dp)))
                .background(color),
    )
}
