package com.bellako.kiwi.common.screens.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.bellako.kiwi.common.tests.CommonTestTags
import com.bellako.kiwi.features.settings.tests.SettingsTestTags
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.getResponsiveSizeHeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Kiwi_Slider(
    textArguments: KiwiTextArguments,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    testTag: String,
    enabled: Boolean = true,
) {
    val kiwiColors = LocalKiwiColors.current

    Kiwi_H3(textArguments)

    Slider(
        value = value,
        onValueChange = onValueChange,
        valueRange = valueRange,
        steps = steps,
        modifier = Modifier.testTag(testTag).height(getResponsiveSizeHeight(40.dp)),
        enabled = enabled,
        thumb = {
            SliderDefaults.Thumb(
                interactionSource = remember { MutableInteractionSource() },
                thumbSize = DpSize(getResponsiveSizeHeight(20.dp), getResponsiveSizeHeight(20.dp)),
                colors =
                    SliderDefaults.colors().copy(
                        thumbColor = kiwiColors.color7D,
                    ),
                modifier = Modifier.padding(getResponsiveSizeHeight(0.dp)),
            )
        },
        track = { sliderState ->
            SliderDefaults.Track(
                modifier =
                    Modifier
                        .height(getResponsiveSizeHeight(4.dp)),
                sliderState = sliderState,
                colors =
                    SliderDefaults.colors().copy(
                        activeTickColor = Color.Transparent,
                        activeTrackColor = kiwiColors.color7D,
                        inactiveTickColor = Color.Transparent,
                        inactiveTrackColor = kiwiColors.color2,
                    ),
                thumbTrackGapSize = getResponsiveSizeHeight(0.dp),
            )
        },
    )
}

// -------------------------------------------------------------------------------------------------

@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Composable
fun Kiwi_Slider_Preview() {
    Kiwi_Theme {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .testTag(CommonTestTags.SETTINGS_SCREEN),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Kiwi_Slider(
                KiwiTextArguments("Sound Volume"),
                value = 2.0f,
                onValueChange = { },
                valueRange = 0f..3f,
                steps = 2,
                testTag = SettingsTestTags.SOUND_VOLUME_SLIDER,
            )

            Kiwi_Spacer()

            Kiwi_Slider(
                KiwiTextArguments("Sound Volume"),
                value = 2.0f,
                onValueChange = { },
                valueRange = 0f..3f,
                steps = 2,
                testTag = SettingsTestTags.SOUND_VOLUME_SLIDER,
                enabled = false,
            )
        }
    }
}
