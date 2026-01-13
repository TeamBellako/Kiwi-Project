package com.bellako.kiwi.common.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
        // Rombo izquierdo
        Kiwi_Diamond(getResponsiveSizeHeight(8.dp), color)
//        Box(
//            modifier =
//                Modifier
//                    .size(getResponsiveSizeHeight(8.dp))
//                    .rotate(45f)
//                    .background(color),
//        )

        // Línea central
        Box(
            modifier =
                Modifier
                    .weight(1f)
                    .height(getResponsiveSizeHeight(3.dp))
//                    .clip(RoundedCornerShape(getResponsiveSizeHeight(2.dp)))
                    .background(color),
        )

        // Rombo derecho
        Kiwi_Diamond(getResponsiveSizeHeight(8.dp), color)
//        Box(
//            modifier =
//                Modifier
//                    .size(getResponsiveSizeHeight(8.dp))
//                    .rotate(45f)
////                    .clip(RoundedCornerShape(getResponsiveSizeHeight(2.dp)))
//                    .background(color),
//        )
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
        Kiwi_Diamond(getResponsiveSizeHeight(8.dp), color)
//        Box(
//            modifier =
//                Modifier
//                    .size(getResponsiveSizeHeight(8.dp))
//                    .rotate(45f)
//                    .background(color),
//        )
        // Línea izquierda
        Box(
            modifier =
                Modifier
                    .weight(1f)
                    .height(getResponsiveSizeHeight(3.dp))
                    .clip(RoundedCornerShape(getResponsiveSizeHeight(2.dp)))
                    .background(color),
        )

        // Texto en el centro
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

        // Línea derecha
        Box(
            modifier =
                Modifier
                    .weight(1f)
                    .height(getResponsiveSizeHeight(3.dp))
                    .clip(RoundedCornerShape(getResponsiveSizeHeight(2.dp)))
                    .background(color),
        )
        Kiwi_Diamond(getResponsiveSizeHeight(8.dp), color)
//        Box(
//            modifier =
//                Modifier
//                    .size(getResponsiveSizeHeight(8.dp))
//                    .rotate(45f)
//                    .background(color),
//        )
    }
}
