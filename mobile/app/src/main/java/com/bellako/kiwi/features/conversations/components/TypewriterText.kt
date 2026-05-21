package com.bellako.kiwi.features.conversations.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.bellako.kiwi.common.screens.components.rememberTextWidthScale
import kotlinx.coroutines.delay

private const val DEFAULT_CHAR_DELAY_MS = 14L
private const val LINE_HEIGHT = 1.2f

/**
 * Drives a character-by-character reveal of [fullText]. The whole string is
 * always laid out so wrapping never shifts mid-reveal; only the colored prefix
 * grows. Tapping can cut the reveal short via [skip].
 */
@Stable
class TypewriterState(
    val fullText: String,
) {
    var visibleCharCount by mutableIntStateOf(0)
        internal set

    val isComplete: Boolean
        get() = visibleCharCount >= fullText.length

    fun skip() {
        visibleCharCount = fullText.length
    }
}

@Composable
fun rememberTypewriter(
    fullText: String,
    charDelayMs: Long = DEFAULT_CHAR_DELAY_MS,
    play: Boolean = true,
): TypewriterState {
    val state = remember(fullText) { TypewriterState(fullText) }

    LaunchedEffect(state, play) {
        if (!play) return@LaunchedEffect
        while (state.visibleCharCount < fullText.length) {
            delay(charDelayMs)
            // skip() may have completed the reveal while we were delayed.
            if (state.visibleCharCount < fullText.length) {
                state.visibleCharCount += 1
            }
        }
    }

    return state
}

@Composable
fun Kiwi_TypewriterText(
    typewriter: TypewriterState,
    color: Color,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Left,
) {
    DialogueText(
        fullText = typewriter.fullText,
        revealedChars = typewriter.visibleCharCount,
        color = color,
        modifier = modifier,
        textAlign = textAlign,
    )
}

/** Fully revealed dialogue line — used for the outgoing line while it slides away. */
@Composable
fun Kiwi_DialogueText(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Left,
) {
    DialogueText(
        fullText = text,
        revealedChars = text.length,
        color = color,
        modifier = modifier,
        textAlign = textAlign,
    )
}

@Composable
private fun DialogueText(
    fullText: String,
    revealedChars: Int,
    color: Color,
    modifier: Modifier,
    textAlign: TextAlign,
) {
    val scale = rememberTextWidthScale()
    val bodyStyle = MaterialTheme.typography.bodyMedium

    val annotated =
        remember(fullText, revealedChars, color) {
            buildAnnotatedString {
                append(fullText)
                addStyle(SpanStyle(color = color), 0, revealedChars)
                addStyle(SpanStyle(color = Color.Transparent), revealedChars, fullText.length)
            }
        }

    Text(
        text = annotated,
        textAlign = textAlign,
        modifier = modifier,
        style =
            bodyStyle.copy(
                fontSize = (bodyStyle.fontSize.value * scale).sp,
                lineHeight = (bodyStyle.fontSize.value * scale * LINE_HEIGHT).sp,
            ),
    )
}
