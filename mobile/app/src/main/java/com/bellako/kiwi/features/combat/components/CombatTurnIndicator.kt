package com.bellako.kiwi.features.combat.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bellako.kiwi.R
import com.bellako.kiwi.common.screens.components.KiwiAnnotatedStringArguments
import com.bellako.kiwi.common.screens.components.Kiwi_AnnotatedString_P2
import com.bellako.kiwi.common.screens.components.Kiwi_Image
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import com.bellako.kiwi.ui.getResponsiveSizeWidth

private val INDICATOR_HEIGHT = 56.dp
private val INDICATOR_RADIUS = 12.dp
private val CHEVRON_SIZE = 14.dp
private const val CHEVRON_OPEN_ROTATION = 180f
private const val CHEVRON_CLOSED_ROTATION = 0f

@Composable
fun CombatTurnIndicator(
    message: AnnotatedString,
    isLogOpen: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalKiwiColors.current
    val rotation by animateFloatAsState(
        targetValue = if (isLogOpen) CHEVRON_CLOSED_ROTATION else CHEVRON_OPEN_ROTATION,
        label = "combat_turn_chevron",
    )

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .background(
                    color = colors.color3A,
                    shape = RoundedCornerShape(getResponsiveSizeHeight(INDICATOR_RADIUS)),
                ).border(
                    width = getResponsiveSizeHeight(1.dp),
                    color = colors.color5C,
                    shape = RoundedCornerShape(getResponsiveSizeHeight(INDICATOR_RADIUS)),
                ).clickable(onClick = onClick)
                .padding(
                    horizontal = getResponsiveSizeWidth(Spacing.medium),
                    vertical = getResponsiveSizeHeight(Spacing.medium),
                ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            Kiwi_AnnotatedString_P2(
                KiwiAnnotatedStringArguments(
                    text = message,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                ),
            )
        }

        Kiwi_Image(
            painterResourceId = R.drawable.ic_dialogue_arrow,
            alt = if (isLogOpen) "Close combat log" else "Open combat log",
            modifier =
                Modifier
                    .size(getResponsiveSizeHeight(CHEVRON_SIZE))
                    .rotate(rotation),
        )
    }
}

@Composable
fun userTurnMessage(): AnnotatedString {
    val colors = LocalKiwiColors.current
    return buildAnnotatedString {
        withStyle(SpanStyle(color = colors.color7A, fontStyle = FontStyle.Italic)) {
            append("IT'S YOUR TURN!")
        }
    }
}

@Preview(name = "Medium Phone", widthDp = 392, heightDp = 120)
@Composable
fun CombatTurnIndicator_Preview() {
    Kiwi_Theme {
        CombatTurnIndicator(
            message = userTurnMessage(),
            isLogOpen = false,
            onClick = {},
            modifier = Modifier.padding(Spacing.medium),
        )
    }
}
