package com.bellako.kiwi.features.combat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_FixedSizeButton
import com.bellako.kiwi.common.screens.components.Kiwi_H3
import com.bellako.kiwi.common.screens.components.Kiwi_P2
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer_Horizontal
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import com.bellako.kiwi.ui.getResponsiveSizeWidth

private val MODAL_RADIUS = 16.dp
private val MODAL_MAX_WIDTH = 320.dp
private const val SCRIM_ALPHA = 0.7f
private const val BUTTON_WEIGHT = 0.5f

@Composable
fun CombatAbandonConfirmModal(
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    Dialog(
        onDismissRequest = onCancel,
        properties =
            DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false,
            ),
    ) {
        val colors = LocalKiwiColors.current
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = SCRIM_ALPHA))
                    .clickable(
                        onClick = onCancel,
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                    ),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier =
                    Modifier
                        .widthIn(max = getResponsiveSizeWidth(MODAL_MAX_WIDTH))
                        .padding(horizontal = getResponsiveSizeWidth(Spacing.large))
                        .background(
                            color = colors.color3A,
                            shape = RoundedCornerShape(getResponsiveSizeHeight(MODAL_RADIUS)),
                        ).padding(
                            horizontal = getResponsiveSizeWidth(Spacing.large),
                            vertical = getResponsiveSizeHeight(Spacing.large),
                        ).clickable(
                            onClick = {},
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                        ),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(getResponsiveSizeHeight(Spacing.small)),
                ) {
                    Kiwi_H3(
                        KiwiTextArguments(
                            text = "Leave combat?",
                            color = colors.colorF,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        ),
                    )
                    Kiwi_P2(
                        KiwiTextArguments(
                            text = "Leaving now counts as an automatic defeat. Are you sure?",
                            color = colors.color7A,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        ),
                    )
                    Kiwi_Spacer(Spacing.small)
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Kiwi_FixedSizeButton(
                            textArguments =
                                KiwiTextArguments(
                                    text = "Cancel",
                                    color = colors.colorF,
                                ),
                            onClick = onCancel,
                            color = colors.color5,
                            modifier = Modifier.weight(BUTTON_WEIGHT),
                        )
                        Kiwi_Spacer_Horizontal(Spacing.small)
                        Kiwi_FixedSizeButton(
                            textArguments =
                                KiwiTextArguments(
                                    text = "Leave",
                                    color = colors.colorF,
                                ),
                            onClick = onConfirm,
                            color = colors.colorR,
                            modifier = Modifier.weight(BUTTON_WEIGHT),
                        )
                    }
                }
            }
        }
    }
}

@Preview(name = "Abandon modal", widthDp = 392, heightDp = 600)
@Composable
fun CombatAbandonConfirmModal_Preview() {
    Kiwi_Theme {
        CombatAbandonConfirmModal(onConfirm = {}, onCancel = {})
    }
}
