package com.bellako.kiwi.common.screens.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.bellako.kiwi.ui.getResponsiveSizeHeight

// -------------------------------------------------------------------------------------------------

data class KiwiTextArguments(
    val text: String,
    val textAlign: TextAlign = TextAlign.Left,
    val color: Color = Color.White,
    val bold: Boolean = false,
    val italic: Boolean = false,
    val modifier: Modifier = Modifier,
)

data class KiwiAnnotatedStringArguments(
    val text: AnnotatedString,
    val textAlign: TextAlign = TextAlign.Left,
    val modifier: Modifier = Modifier,
)

// -------------------------------------------------------------------------------------------------

@Composable
private fun KiwiText(
    arguments: KiwiTextArguments,
    bodyStyle: TextStyle,
) {
    Text(
        text = arguments.text,
        textAlign = arguments.textAlign,
        color = arguments.color,
        modifier = arguments.modifier,
        style =
            bodyStyle.copy(
                fontWeight = if (arguments.bold) FontWeight.Bold else FontWeight.Normal,
                fontStyle = if (arguments.italic) FontStyle.Italic else FontStyle.Normal,
                fontSize = getResponsiveSizeHeight(bodyStyle.fontSize.value.toInt()).sp,
            ),
    )
}

// -------------------------------------------------------------------------------------------------

@Composable
fun KiwiH1(arguments: KiwiTextArguments) {
    KiwiText(arguments, MaterialTheme.typography.headlineLarge)
}

@Composable
fun KiwiH2(arguments: KiwiTextArguments) {
    KiwiText(arguments, MaterialTheme.typography.headlineMedium)
}

@Composable
fun KiwiH3(arguments: KiwiTextArguments) {
    KiwiText(arguments, MaterialTheme.typography.headlineSmall)
}

// -------------------------------------------------------------------------------------------------

@Composable
fun KiwiP1(arguments: KiwiTextArguments) {
    KiwiText(arguments, MaterialTheme.typography.bodyLarge)
}

@Composable
fun KiwiP2(arguments: KiwiTextArguments) {
    KiwiText(arguments, MaterialTheme.typography.bodyMedium)
}

@Composable
fun KiwiP3(arguments: KiwiTextArguments) {
    KiwiText(arguments, MaterialTheme.typography.bodySmall)
}

// -------------------------------------------------------------------------------------------------

@Composable
fun KiwiLabel1(arguments: KiwiTextArguments) {
    KiwiText(arguments, MaterialTheme.typography.labelLarge)
}

@Composable
fun KiwiLabel2(arguments: KiwiTextArguments) {
    KiwiText(arguments, MaterialTheme.typography.labelMedium)
}

@Composable
fun KiwiLabel3(arguments: KiwiTextArguments) {
    KiwiText(arguments, MaterialTheme.typography.labelSmall)
}

// -------------------------------------------------------------------------------------------------

@Composable
private fun KiwiAnnotatedStringP(
    arguments: KiwiAnnotatedStringArguments,
    bodyStyle: TextStyle,
) {
    Text(
        text = arguments.text,
        textAlign = arguments.textAlign,
        modifier = arguments.modifier,
        style = bodyStyle.copy(fontSize = getResponsiveSizeHeight(bodyStyle.fontSize.value.toInt()).sp),
    )
}

@Composable
fun KiwiAnnotatedStringP1(arguments: KiwiAnnotatedStringArguments) {
    KiwiAnnotatedStringP(arguments, MaterialTheme.typography.bodyLarge)
}

@Composable
fun KiwiAnnotatedStringP2(arguments: KiwiAnnotatedStringArguments) {
    KiwiAnnotatedStringP(arguments, MaterialTheme.typography.bodyMedium)
}

@Composable
fun KiwiAnnotatedStringP3(arguments: KiwiAnnotatedStringArguments) {
    KiwiAnnotatedStringP(arguments, MaterialTheme.typography.bodySmall)
}

// -------------------------------------------------------------------------------------------------
