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

data class Kiwi_TextArguments(
    val text: String,
    val textAlign: TextAlign = TextAlign.Left,
    val color: Color = Color.White,
    val bold: Boolean = false,
    val italic: Boolean = false,
    val modifier: Modifier = Modifier,
)

data class Kiwi_AnnotatedStringArguments(
    val text: AnnotatedString,
    val textAlign: TextAlign = TextAlign.Left,
    val modifier: Modifier = Modifier,
)

// -------------------------------------------------------------------------------------------------

@Composable
private fun Kiwi_Text(
    arguments: Kiwi_TextArguments,
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
fun Kiwi_H1(arguments: Kiwi_TextArguments) {
    Kiwi_Text(arguments, MaterialTheme.typography.headlineLarge)
}

@Composable
fun Kiwi_H2(arguments: Kiwi_TextArguments) {
    Kiwi_Text(arguments, MaterialTheme.typography.headlineMedium)
}

@Composable
fun Kiwi_H3(arguments: Kiwi_TextArguments) {
    Kiwi_Text(arguments, MaterialTheme.typography.headlineSmall)
}

// -------------------------------------------------------------------------------------------------

@Composable
fun Kiwi_P1(arguments: Kiwi_TextArguments) {
    Kiwi_Text(arguments, MaterialTheme.typography.bodyLarge)
}

@Composable
fun Kiwi_P2(arguments: Kiwi_TextArguments) {
    Kiwi_Text(arguments, MaterialTheme.typography.bodyMedium)
}

@Composable
fun Kiwi_P3(arguments: Kiwi_TextArguments) {
    Kiwi_Text(arguments, MaterialTheme.typography.bodySmall)
}

// -------------------------------------------------------------------------------------------------

@Composable
fun Kiwi_Label1(arguments: Kiwi_TextArguments) {
    Kiwi_Text(arguments, MaterialTheme.typography.labelLarge)
}

@Composable
fun Kiwi_Label2(arguments: Kiwi_TextArguments) {
    Kiwi_Text(arguments, MaterialTheme.typography.labelMedium)
}

@Composable
fun Kiwi_Label3(arguments: Kiwi_TextArguments) {
    Kiwi_Text(arguments, MaterialTheme.typography.labelSmall)
}

// -------------------------------------------------------------------------------------------------

@Composable
private fun Kiwi_AnnotatedString_P(
    arguments: Kiwi_AnnotatedStringArguments,
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
fun Kiwi_AnnotatedString_P1(arguments: Kiwi_AnnotatedStringArguments) {
    Kiwi_AnnotatedString_P(arguments, MaterialTheme.typography.bodyLarge)
}

@Composable
fun Kiwi_AnnotatedString_P2(arguments: Kiwi_AnnotatedStringArguments) {
    Kiwi_AnnotatedString_P(arguments, MaterialTheme.typography.bodyMedium)
}

@Composable
fun Kiwi_AnnotatedString_P3(arguments: Kiwi_AnnotatedStringArguments) {
    Kiwi_AnnotatedString_P(arguments, MaterialTheme.typography.bodySmall)
}

// -------------------------------------------------------------------------------------------------
