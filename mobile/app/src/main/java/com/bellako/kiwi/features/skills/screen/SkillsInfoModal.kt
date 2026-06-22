package com.bellako.kiwi.features.skills.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_FixedSizeButton
import com.bellako.kiwi.common.screens.components.Kiwi_H2
import com.bellako.kiwi.common.screens.components.Kiwi_P2
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight

private const val SKILLS_INFO_MODAL_CORNER_DP = 16
private const val SKILLS_INFO_MODAL_BUTTON_WIDTH = 0.6f

@Composable
fun SkillsInfoModal(onDismiss: () -> Unit = {}) {
    val kiwiColors = LocalKiwiColors.current
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(
                        color = kiwiColors.color0,
                        shape = RoundedCornerShape(SKILLS_INFO_MODAL_CORNER_DP.dp),
                    ).padding(getResponsiveSizeHeight(Spacing.large)),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(getResponsiveSizeHeight(Spacing.medium)),
            ) {
                Kiwi_H2(
                    KiwiTextArguments(
                        "Managing Your Deck",
                        TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = kiwiColors.colorF,
                    ),
                )

                Kiwi_P2(
                    KiwiTextArguments(
                        "Hold a skill to equip it to your deck, or to un-equip it.",
                        TextAlign.Center,
                        color = kiwiColors.colorF1,
                    ),
                )

                Kiwi_P2(
                    KiwiTextArguments(
                        "Tap a skill to view its details.",
                        TextAlign.Center,
                        color = kiwiColors.colorF1,
                    ),
                )

                Kiwi_Spacer(Spacing.small)

                Kiwi_FixedSizeButton(
                    textArguments =
                        KiwiTextArguments(
                            "Got it",
                            color = kiwiColors.colorF,
                            fontWeight = FontWeight.Bold,
                        ),
                    color = kiwiColors.color8,
                    modifier = Modifier.fillMaxWidth(SKILLS_INFO_MODAL_BUTTON_WIDTH),
                    onClick = onDismiss,
                )
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------

@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Composable
fun SkillsInfoModal_Preview() {
    Kiwi_Theme {
        SkillsInfoModal()
    }
}
