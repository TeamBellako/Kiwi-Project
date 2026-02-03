package com.bellako.kiwi.common.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight

@Composable
fun Kiwi_HorizontalLine(
    color: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Kiwi_Diamond(getResponsiveSizeHeight(18.dp), color, Modifier.offset(x = getResponsiveSizeHeight(4.dp)))

        Box(
            modifier =
                Modifier
                    .weight(1f)
                    .height(getResponsiveSizeHeight(4.dp))
                    .background(color),
        )

        Kiwi_Diamond(getResponsiveSizeHeight(18.dp), color, Modifier.offset(x = getResponsiveSizeHeight(-4.dp)))
    }
}

@Composable
fun Kiwi_HorizontalLine_Text(
    text: String,
    color: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Kiwi_Diamond(getResponsiveSizeHeight(14.dp), color, Modifier.offset(x = getResponsiveSizeHeight(4.dp)))

        Box(
            modifier =
                Modifier
                    .weight(1f)
                    .height(getResponsiveSizeHeight(3.dp))
                    .background(color),
        )

        Kiwi_P1(
            KiwiTextArguments(
                text = text,
                color = textColor,
                modifier =
                    Modifier.padding(
                        horizontal = getResponsiveSizeHeight(Spacing.medium),
                    ),
            ),
        )


        Box(
            modifier =
                Modifier
                    .weight(1f)
                    .height(getResponsiveSizeHeight(3.dp))
                    .background(color),
        )
        Kiwi_Diamond(getResponsiveSizeHeight(14.dp), color, Modifier.offset(x = getResponsiveSizeHeight(-4.dp)))
    }
}
