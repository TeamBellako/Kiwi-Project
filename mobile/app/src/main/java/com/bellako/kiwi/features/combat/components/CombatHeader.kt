package com.bellako.kiwi.features.combat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_H3
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import com.bellako.kiwi.ui.getResponsiveSizeWidth

private val HEADER_BUTTON_SIZE = 36.dp
private val HEADER_ICON_SIZE = 18.dp

@Composable
fun CombatHeader(
    title: String,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalKiwiColors.current

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(
                    horizontal = getResponsiveSizeWidth(Spacing.medium),
                    vertical = getResponsiveSizeHeight(Spacing.small),
                ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier
                    .size(getResponsiveSizeHeight(HEADER_BUTTON_SIZE))
                    .background(color = colors.color3, shape = CircleShape)
                    .clickable(onClick = onClose),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close combat",
                tint = colors.colorF,
                modifier = Modifier.size(getResponsiveSizeHeight(HEADER_ICON_SIZE)),
            )
        }

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            Kiwi_H3(
                KiwiTextArguments(
                    text = title,
                    color = colors.colorF,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                ),
            )
        }

        Spacer(modifier = Modifier.size(getResponsiveSizeHeight(HEADER_BUTTON_SIZE)))
    }
}

@Preview(name = "Medium Phone", widthDp = 392, heightDp = 100)
@Composable
fun CombatHeader_Preview() {
    Kiwi_Theme {
        CombatHeader(
            title = "Ongoing Combat",
            onClose = {},
        )
    }
}
