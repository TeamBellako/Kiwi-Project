package com.bellako.kiwi.features.goals.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.bellako.kiwi.R
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_FixedSizeButton
import com.bellako.kiwi.common.screens.components.Kiwi_H1
import com.bellako.kiwi.common.screens.components.Kiwi_H2
import com.bellako.kiwi.common.screens.components.Kiwi_Image
import com.bellako.kiwi.common.screens.components.Kiwi_P1
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import com.bellako.kiwi.ui.getResponsiveSizeWidth

@Composable
fun GoalRewardModal(
    rewardPoints: Int,
    onDismiss: () -> Unit,
    title: String = "Goal Completed!",
) {
    val kiwiColor = LocalKiwiColors.current
    Dialog(
        onDismissRequest = onDismiss,
        properties =
            DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false,
            ),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .clickable(
                        onClick = onDismiss,
                        indication = null,
                        interactionSource = MutableInteractionSource(),
                    ),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier =
                    Modifier
                        .padding(horizontal = getResponsiveSizeWidth(Spacing.large))
                        .clickable(
                            onClick = {},
                            indication = null,
                            interactionSource = MutableInteractionSource(),
                        ),
            ) {
                Kiwi_Image(
                    R.drawable.dialogue_small_bg,
                    "Reward frame",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.fillMaxWidth(),
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement =
                        Arrangement.spacedBy(getResponsiveSizeHeight(Spacing.medium)),
                    modifier =
                        Modifier
                            .matchParentSize()
                            .padding(
                                horizontal = getResponsiveSizeWidth(Spacing.large),
                                vertical = getResponsiveSizeHeight(Spacing.large),
                            ),
                ) {
                    Kiwi_H1(
                        KiwiTextArguments(
                            title,
                            TextAlign.Center,
                            color = kiwiColor.color6,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth(),
                        ),
                    )
                    Kiwi_P1(
                        KiwiTextArguments(
                            "You earned",
                            TextAlign.Center,
                            color = kiwiColor.color6,
                            modifier = Modifier.fillMaxWidth(),
                        ),
                    )
                    Kiwi_H2(
                        KiwiTextArguments(
                            "+$rewardPoints points",
                            TextAlign.Center,
                            color = kiwiColor.color8A,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth(),
                        ),
                    )
                    Kiwi_FixedSizeButton(
                        horizontalMargin = Spacing.xLarge,
                        textArguments =
                            KiwiTextArguments(
                                "AWESOME!",
                                color = kiwiColor.color6,
                                fontWeight = FontWeight.Bold,
                            ),
                        color = kiwiColor.color5A,
                        onClick = onDismiss,
                    )
                }
            }
        }
    }
}

@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Composable
@Suppress("MagicNumber")
fun GoalRewardModal_Preview() {
    Kiwi_Theme {
        GoalRewardModal(
            rewardPoints = 150,
            onDismiss = {},
        )
    }
}
