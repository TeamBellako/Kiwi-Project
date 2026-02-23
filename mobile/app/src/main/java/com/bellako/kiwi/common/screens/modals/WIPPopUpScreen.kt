package com.bellako.kiwi.common.screens.modals

import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_FixedSizeButton
import com.bellako.kiwi.common.screens.components.Kiwi_H1
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight

@Composable
fun WIPPopUpScreen(onDismiss: () -> Unit = {}) {
    val kiwiColor = LocalKiwiColors.current
    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(
                        color = kiwiColor.color0,
                        shape = RoundedCornerShape(16.dp),
                    ).padding(getResponsiveSizeHeight(Spacing.large)),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement =
                    androidx.compose.foundation.layout.Arrangement
                        .spacedBy(getResponsiveSizeHeight(Spacing.medium)),
            ) {
                Kiwi_H1(
                    KiwiTextArguments(
                        "Work in progress",
                        TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                    ),
                )
                @Suppress("MagicNumber")
                Kiwi_FixedSizeButton(
                    textArguments =
                        KiwiTextArguments(
                            "Close",
                            color = kiwiColor.colorF,
                        ),
                    color = kiwiColor.color8,
                    modifier = Modifier.fillMaxWidth(0.6f),
                    onClick = onDismiss,
                )
            }
        }
    }
}
