package com.bellako.kiwi.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

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
    val modifier: Modifier = Modifier
)

@Composable
fun Kiwi_H1(arguments: Kiwi_TextArguments) {
    Text(
        text = arguments.text,
        textAlign = arguments.textAlign,
        color = arguments.color,
        modifier = arguments.modifier.padding(bottom = 24.dp),
        style = MaterialTheme.typography.headlineLarge.copy(
            fontWeight = if (arguments.bold) FontWeight.Bold else FontWeight.Normal,
            fontStyle = if (arguments.italic) FontStyle.Italic else FontStyle.Normal
        ).copy(
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
        modifier = arguments.modifier.padding(bottom = 16.dp),
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
        modifier = arguments.modifier.padding(bottom = 8.dp),
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
        modifier = arguments.modifier.padding(bottom = 4.dp),
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
        modifier = arguments.modifier.padding(bottom = 2.dp),
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
    Text(
        text = arguments.text,
        textAlign = arguments.textAlign,
        modifier = arguments.modifier.padding(bottom = 4.dp),
    )
}

@Composable
fun Kiwi_Label(arguments: Kiwi_TextArguments) {
    Text(
        text = arguments.text,
        textAlign = arguments.textAlign,
        color = arguments.color,
        style = MaterialTheme.typography.labelLarge.copy(
            fontWeight = if (arguments.bold) FontWeight.Bold else FontWeight.Normal,
            fontStyle = if (arguments.italic) FontStyle.Italic else FontStyle.Normal
        )
    )
}