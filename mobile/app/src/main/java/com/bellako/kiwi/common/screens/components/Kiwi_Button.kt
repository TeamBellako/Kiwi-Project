package com.bellako.kiwi.common.screens.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bellako.kiwi.R
import com.bellako.kiwi.audio.AudioManager
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.getResponsiveSizeHeight

@Composable
fun Kiwi_Button(
    modifier: Modifier = Modifier,
    contentPaddingHorizontal: Dp = 8.dp,
    contentPaddingVertical: Dp = 8.dp,
    textArguments: KiwiTextArguments,
    onClick: () -> Unit,
    enabled: Boolean = true,
    color: Color,
    testTag: String = "",
) {
    val context = LocalContext.current

    Box(modifier = modifier) {
        Button(
            onClick = {
                AudioManager.playSFX(context, R.raw.snd_ui_button)
                onClick.invoke()
            },
            enabled = enabled,
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = color,
                    disabledContainerColor = color.copy(alpha = 0.15f),
                    contentColor = color,
                    disabledContentColor = color.copy(alpha = 0.3f),
                ),
            contentPadding =
                PaddingValues(
                    getResponsiveSizeHeight(contentPaddingHorizontal),
                    getResponsiveSizeHeight(contentPaddingVertical),
                ),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .testTag(testTag),
            shape = RoundedCornerShape(getResponsiveSizeHeight(10.dp)),
        ) {
            val actualTextArguments =
                if (enabled) {
                    textArguments
                } else {
                    textArguments.copy(color = textArguments.color.copy(alpha = 0.3f))
                }
            Kiwi_Label1(actualTextArguments)
        }
    }
}

// -------------------------------------------------------------------------------------------------

@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Composable
fun Kiwi_Button_Preview() {
    val kiwiColors = LocalKiwiColors.current

    Kiwi_Theme {
        Column {
            Kiwi_Button(
                textArguments =
                    KiwiTextArguments(
                        "BUTTON",
                        color = kiwiColors.colorF,
                        bold = true,
                    ),
                color = kiwiColors.color5,
                onClick = {},
            )

            Kiwi_Spacer()

            Kiwi_Button(
                textArguments =
                    KiwiTextArguments(
                        "BUTTON",
                        color = kiwiColors.colorF,
                        bold = true,
                    ),
                color = kiwiColors.color5,
                onClick = {},
                enabled = false,
            )
        }
    }
}
