package com.bellako.kiwi.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bellako.kiwi.ui.theme.DeviceSize
import com.bellako.kiwi.ui.theme.Dimensions
import com.bellako.kiwi.ui.theme.Spacing
import com.bellako.kiwi.ui.theme.getDeviceSize

data class Kiwi_TextArguments(
    val text: String,
    val textAlign: TextAlign = TextAlign.Left,
    val color: Color = Color.White,
    val bold: Boolean = false,
    val italic: Boolean = false,
    val modifier: Modifier = Modifier
)

data class Kiwi_AnnotatedStringArguments(
    val text: AnnotatedString,
    val textAlign: TextAlign = TextAlign.Left,
    val modifier: Modifier = Modifier,
    val shouldBeResponsive: Boolean = false
)

@Composable
fun Kiwi_H1(arguments: Kiwi_TextArguments) {
    Text(
        text = arguments.text,
        textAlign = arguments.textAlign,
        color = arguments.color,
        modifier = arguments.modifier.then(Modifier.padding(bottom = Spacing.large)),
        style = MaterialTheme.typography.headlineLarge.copy(
            fontWeight = if (arguments.bold) FontWeight.Bold else FontWeight.Normal,
            fontStyle = if (arguments.italic) FontStyle.Italic else FontStyle.Normal
        )
    )
}

@Composable
fun Kiwi_H2(arguments: Kiwi_TextArguments) {
    Text(
        text = arguments.text,
        textAlign = arguments.textAlign,
        color = arguments.color,
        modifier = arguments.modifier.then(Modifier.padding(bottom = Spacing.medium)),
        style = MaterialTheme.typography.headlineMedium.copy(
            fontWeight = if (arguments.bold) FontWeight.Bold else FontWeight.Normal,
            fontStyle = if (arguments.italic) FontStyle.Italic else FontStyle.Normal
        )
    )
}

@Composable
fun Kiwi_H3(arguments: Kiwi_TextArguments) {
    Text(
        text = arguments.text,
        textAlign = arguments.textAlign,
        color = arguments.color,
        modifier = arguments.modifier.then(Modifier.padding(bottom = Spacing.small)),
        style = MaterialTheme.typography.headlineSmall.copy(
            fontWeight = if (arguments.bold) FontWeight.Bold else FontWeight.Normal,
            fontStyle = if (arguments.italic) FontStyle.Italic else FontStyle.Normal
        )
    )
}

@Composable
fun Kiwi_P1(arguments: Kiwi_TextArguments) {
    Text(
        text = arguments.text,
        textAlign = arguments.textAlign,
        color = arguments.color,
        modifier = arguments.modifier.then(Modifier.padding(bottom = Spacing.small)),
        style = MaterialTheme.typography.bodyLarge.copy(
            fontWeight = if (arguments.bold) FontWeight.Bold else FontWeight.Normal,
            fontStyle = if (arguments.italic) FontStyle.Italic else FontStyle.Normal
        )
    )
}

@Composable
fun Kiwi_P2(arguments: Kiwi_TextArguments) {
    Text(
        text = arguments.text,
        textAlign = arguments.textAlign,
        color = arguments.color,
        modifier = arguments.modifier.then(Modifier.padding(bottom = Spacing.xSmall)),
        style = MaterialTheme.typography.bodyMedium.copy(
            fontWeight = if (arguments.bold) FontWeight.Bold else FontWeight.Normal,
            fontStyle = if (arguments.italic) FontStyle.Italic else FontStyle.Normal
        )
    )
}

@Composable
fun Kiwi_P3(arguments: Kiwi_TextArguments) {
    Text(
        text = arguments.text,
        textAlign = arguments.textAlign,
        color = arguments.color,
        modifier = arguments.modifier,
        style = MaterialTheme.typography.bodySmall.copy(
            fontWeight = if (arguments.bold) FontWeight.Bold else FontWeight.Normal,
            fontStyle = if (arguments.italic) FontStyle.Italic else FontStyle.Normal
        )
    )
}

@Composable
fun Kiwi_AnnotatedString(arguments: Kiwi_AnnotatedStringArguments) {
    if (arguments.shouldBeResponsive) {
        Kiwi_ResponsiveAnnotatedString(
            text = arguments.text,
            textAlign = arguments.textAlign,
        )
    } else {
        Text(
            text = arguments.text,
            textAlign = arguments.textAlign,
            style = MaterialTheme.typography.bodyMedium,
        )
    }

}

@Composable
fun Kiwi_ResponsiveAnnotatedString(
    text: AnnotatedString,
    textAlign: TextAlign
) {
    val textWidth = remember { mutableStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned { coordinates ->
                textWidth.value = coordinates.size.width.toFloat()
            }
    ) {
        val deviceSize = getDeviceSize(textWidth.value.dp)

        val fontSize = when (deviceSize) {
            DeviceSize.SMALL -> Dimensions.smallFontSize
            DeviceSize.MEDIUM -> Dimensions.mediumFontSize
            DeviceSize.LARGE -> Dimensions.largeFontSize
        }

        Text(
            text = text,
            textAlign = textAlign,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = fontSize)
        )
    }
}


@Composable
fun Kiwi_Label(arguments: Kiwi_TextArguments) {
    Text(
        text = arguments.text,
        textAlign = arguments.textAlign,
        color = arguments.color,
        modifier = arguments.modifier,
        style = MaterialTheme.typography.labelLarge.copy(
            fontWeight = if (arguments.bold) FontWeight.Bold else FontWeight.Normal,
            fontStyle = if (arguments.italic) FontStyle.Italic else FontStyle.Normal
        )
    )
}
