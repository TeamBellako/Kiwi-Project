package com.bellako.kiwi.common.screens.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bellako.kiwi.common.tests.CommonTestTags
import com.bellako.kiwi.features.settings.tests.SettingsTestTags
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.getResponsiveSizeHeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Kiwi_Slider(
    textArguments: KiwiTextArguments? = null,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    testTag: String,
    enabled: Boolean = true,
) {
    val kiwiColors = LocalKiwiColors.current

    if (textArguments != null) {
        Kiwi_Label1(textArguments)
    }

    Slider(
        value = value,
        onValueChange = onValueChange,
        valueRange = valueRange,
        steps = steps,
        modifier = Modifier.fillMaxWidth().testTag(testTag).height(getResponsiveSizeHeight(40.dp)),
        enabled = enabled,
        thumb = {
            Kiwi_Diamond(
                size = getResponsiveSizeHeight(15.dp),
                color = kiwiColors.color7D,
            )
        },
        track = { sliderState ->
            val currentValue = sliderState.value
            val startDiamondColor = if (currentValue > valueRange.start) kiwiColors.color7D else kiwiColors.color2
            val endDiamondColor = if (currentValue >= valueRange.endInclusive) kiwiColors.color7D else kiwiColors.color2
            val density = androidx.compose.ui.platform.LocalDensity.current
            val diamondSizePx = with(density) { getResponsiveSizeHeight(10.dp).toPx() }

            SliderDefaults.Track(
                modifier =
                    Modifier
                        .height(getResponsiveSizeHeight(6.dp))
                        .drawWithContent {
                            // Dibujar el track primero
                            drawContent()

                            val trackHeight = size.height
                            val centerY = trackHeight / 2

                            // Dibujar rombo al inicio
                            val startX = 0f
                            kiwiDiamondShape(startDiamondColor, startX, centerY, diamondSizePx)

                            // Dibujar rombo al final
                            val endX = size.width
                            kiwiDiamondShape(endDiamondColor, endX, centerY, diamondSizePx)
                        },
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
