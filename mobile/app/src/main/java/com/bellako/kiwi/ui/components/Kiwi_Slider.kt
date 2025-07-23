package com.bellako.kiwi.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.bellako.kiwi.features.settings.SettingsTestTags
import com.bellako.kiwi.services.common.CommonTestTags
import com.bellako.kiwi.ui.theme.KiwiTheme
import com.bellako.kiwi.ui.theme.getResponsiveRelativeSize

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Kiwi_Slider(
    textArguments: Kiwi_TextArguments,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    testTag: String,
    enabled: Boolean = true
) {
    Kiwi_H3(textArguments)

    val trackColor = if (enabled) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)

    Slider(
        value = value,
        onValueChange = onValueChange,
        valueRange = valueRange,
        steps = steps,
        modifier = Modifier.testTag(testTag).height(getResponsiveRelativeSize(30.dp)),
        enabled = enabled,
        thumb = {
            SliderDefaults.Thumb(
                interactionSource = remember { MutableInteractionSource() },
                thumbSize = DpSize(getResponsiveRelativeSize(20.dp), getResponsiveRelativeSize(20.dp)),
                colors = SliderDefaults.colors().copy(
                    thumbColor = trackColor,
                ),
                modifier = Modifier.padding(0.dp)
            )
        },
        track = { sliderState ->
            SliderDefaults.Track(
                modifier = Modifier
                    .height(getResponsiveRelativeSize(16.dp)),
                sliderState = sliderState,
                colors = SliderDefaults.colors().copy(
                    activeTickColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.0f),
                    activeTrackColor = trackColor,
                    inactiveTickColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.0f),
                    inactiveTrackColor = MaterialTheme.colorScheme.primary,
                ),
            )
        },
    )
}

// -------------------------------------------------------------------------------------------------

@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Composable
fun Kiwi_SliderPreview() {
    KiwiTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .testTag(CommonTestTags.SETTINGS_SCREEN),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Kiwi_Slider(
                Kiwi_TextArguments("Sound Volume"),
                value = 2.0f,
                onValueChange = { },
                valueRange = 0f..3f,
                steps = 2,
                testTag = SettingsTestTags.SOUND_VOLUME_SLIDER
            )

            Kiwi_Spacer()

            Kiwi_Slider(
                Kiwi_TextArguments("Sound Volume"),
                value = 2.0f,
                onValueChange = { },
                valueRange = 0f..3f,
                steps = 2,
                testTag = SettingsTestTags.SOUND_VOLUME_SLIDER,
                enabled = false
            )
        }
    }
}
