package com.bellako.kiwi.common.screens.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import com.bellako.kiwi.ui.getResponsiveSizeWidth

// -------------------------------------------------------------------------------------------------

@Suppress("MagicNumber")
@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun rememberTextWidthScale(): Float {
    val screenWidthDp = LocalConfiguration.current.screenWidthDp.toFloat()
    val designWidth = 360f
    val scale = screenWidthDp / designWidth
    return scale.coerceIn(0.8f, 1.25f)
}

// -------------------------------------------------------------------------------------------------

data class KiwiTextArguments(
    val text: String,
    val textAlign: TextAlign = TextAlign.Left,
    val color: Color = Color.White,
    val fontWeight: FontWeight = FontWeight.Normal,
    val italic: Boolean = false,
    val modifier: Modifier = Modifier,
)

data class KiwiAnnotatedStringArguments(
    val text: AnnotatedString,
    val textAlign: TextAlign = TextAlign.Left,
    val modifier: Modifier = Modifier,
)

// -------------------------------------------------------------------------------------------------

private const val LINE_HEIGHT = 1.2f

@Composable
private fun Kiwi_Text(
    arguments: KiwiTextArguments,
    bodyStyle: TextStyle,
) {
    val scale = rememberTextWidthScale()

    Text(
        text = arguments.text,
        textAlign = arguments.textAlign,
        color = arguments.color,
        modifier = arguments.modifier,
        style =
            bodyStyle.copy(
                fontWeight = arguments.fontWeight,
                fontStyle = if (arguments.italic) FontStyle.Italic else FontStyle.Normal,
                fontSize = (bodyStyle.fontSize.value * scale).sp,
                lineHeight = (bodyStyle.fontSize.value * scale * LINE_HEIGHT).sp,
            ),
    )
}

// -------------------------------------------------------------------------------------------------

@Composable
fun Kiwi_H1(arguments: KiwiTextArguments) {
    Kiwi_Text(arguments, MaterialTheme.typography.headlineLarge)
}

@Composable
fun Kiwi_H2(arguments: KiwiTextArguments) {
    Kiwi_Text(arguments, MaterialTheme.typography.headlineMedium)
}

@Composable
fun Kiwi_H3(arguments: KiwiTextArguments) {
    Kiwi_Text(arguments, MaterialTheme.typography.headlineSmall)
}

// -------------------------------------------------------------------------------------------------

@Composable
fun Kiwi_P1(arguments: KiwiTextArguments) {
    Kiwi_Text(arguments, MaterialTheme.typography.bodyLarge)
}

@Composable
fun Kiwi_P2(arguments: KiwiTextArguments) {
    Kiwi_Text(arguments, MaterialTheme.typography.bodyMedium)
}

@Composable
fun Kiwi_P3(arguments: KiwiTextArguments) {
    Kiwi_Text(arguments, MaterialTheme.typography.bodySmall)
}

// -------------------------------------------------------------------------------------------------

@Composable
fun Kiwi_Label1(arguments: KiwiTextArguments) {
    Kiwi_Text(arguments, MaterialTheme.typography.labelLarge)
}

@Composable
fun Kiwi_Label2(arguments: KiwiTextArguments) {
    Kiwi_Text(arguments, MaterialTheme.typography.labelMedium)
}

@Composable
fun Kiwi_Label3(arguments: KiwiTextArguments) {
    Kiwi_Text(arguments, MaterialTheme.typography.labelSmall)
}

// -------------------------------------------------------------------------------------------------

@Composable
fun Kiwi_Display1(arguments: KiwiTextArguments) {
    Kiwi_Text(arguments, MaterialTheme.typography.displayLarge)
}

@Composable
fun Kiwi_Display2(arguments: KiwiTextArguments) {
    Kiwi_Text(arguments, MaterialTheme.typography.displayMedium)
}

// -------------------------------------------------------------------------------------------------

@Composable
private fun Kiwi_AnnotatedString_P(
    arguments: KiwiAnnotatedStringArguments,
    bodyStyle: TextStyle,
) {
    val scale = rememberTextWidthScale()

    Text(
        text = arguments.text,
        textAlign = arguments.textAlign,
        modifier = arguments.modifier,
        style = bodyStyle.copy(fontSize = bodyStyle.fontSize * scale),
    )
}

@Composable
fun Kiwi_AnnotatedString_P1(arguments: KiwiAnnotatedStringArguments) {
    Kiwi_AnnotatedString_P(arguments, MaterialTheme.typography.bodyLarge)
}

@Composable
fun Kiwi_AnnotatedString_P2(arguments: KiwiAnnotatedStringArguments) {
    Kiwi_AnnotatedString_P(arguments, MaterialTheme.typography.bodyMedium)
}

@Composable
fun Kiwi_AnnotatedString_P3(arguments: KiwiAnnotatedStringArguments) {
    Kiwi_AnnotatedString_P(arguments, MaterialTheme.typography.bodySmall)
}

// -------------------------------------------------------------------------------------------------
