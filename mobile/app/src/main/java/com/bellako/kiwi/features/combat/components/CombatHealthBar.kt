package com.bellako.kiwi.features.combat.components

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import com.bellako.kiwi.R
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_Image
import com.bellako.kiwi.common.screens.components.Kiwi_Label3
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.LocalKiwiColors

private const val HP_LERP_DURATION_MS = 600

@Composable
@Suppress("LongParameterList")
fun CombatHealthBar(
    currentHp: Int,
    maxHp: Int,
    modifier: Modifier = Modifier,
    fillTint: Color? = null,
    label: String = "HP bar",
    // 0f = bar hidden, 1f = fully revealed. The reveal grows from the centre
    // outward toward both edges, used by the combat intro to introduce the bar.
    barRevealProgress: Float = 1f,
    // Independent alpha for the HP numbers so they can fade in after the bar
    // graphic has finished its reveal.
    numbersAlpha: Float = 1f,
) {
    val colors = LocalKiwiColors.current
    val animatedHp by animateIntAsState(
        targetValue = currentHp,
        animationSpec = tween(durationMillis = HP_LERP_DURATION_MS),
        label = "combat_hp_lerp",
    )
    val percentage =
        if (maxHp <= 0) {
            0f
        } else {
            (animatedHp.toFloat() / maxHp.toFloat()).coerceIn(0f, 1f)
        }

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        clip = true
                        shape = centerExpandShape(barRevealProgress)
                    },
            contentAlignment = Alignment.Center,
        ) {
            Kiwi_Image(
                painterResourceId = R.drawable.health_bar_bg,
                alt = "$label background",
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.FillWidth,
            )

            Kiwi_Image(
                painterResourceId = R.drawable.health_bar_fill,
                alt = "$label fill",
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            clip = true
                            shape =
                                object : Shape {
                                    override fun createOutline(
                                        size: Size,
                                        layoutDirection: LayoutDirection,
                                        density: Density,
                                    ): Outline =
                                        Outline.Rectangle(
                                            Rect(0f, 0f, size.width * percentage, size.height),
                                        )
                                }
                        },
                contentScale = ContentScale.FillWidth,
                colorFilter = fillTint?.let { ColorFilter.tint(it) },
            )
        }

        Kiwi_Label3(
            KiwiTextArguments(
                text = "$animatedHp/$maxHp",
                color = colors.colorF,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.alpha(numbersAlpha),
            ),
        )
    }
}

/**
 * Outline that exposes a horizontally centred slice of the bar whose width
 * grows from 0 at [progress] = 0 to the full bar width at [progress] = 1. Used
 * to reveal the health bar from the centre outward during the combat intro.
 */
private fun centerExpandShape(progress: Float): Shape =
    object : Shape {
        override fun createOutline(
            size: Size,
            layoutDirection: LayoutDirection,
            density: Density,
        ): Outline {
            val visibleWidth = size.width * progress.coerceIn(0f, 1f)
            val left = (size.width - visibleWidth) / 2f
            val right = left + visibleWidth
            return Outline.Rectangle(Rect(left, 0f, right, size.height))
        }
    }

@Preview(name = "Medium Phone", widthDp = 392, heightDp = 100)
@Composable
@Suppress("MagicNumber")
fun CombatHealthBar_Preview() {
    Kiwi_Theme {
        CombatHealthBar(currentHp = 12000, maxHp = 18000)
    }
}
