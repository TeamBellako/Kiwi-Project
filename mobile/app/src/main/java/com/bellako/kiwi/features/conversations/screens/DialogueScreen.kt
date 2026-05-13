package com.bellako.kiwi.features.conversations.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bellako.kiwi.R
import com.bellako.kiwi.audio.AudioManager
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_Image
import com.bellako.kiwi.common.screens.components.Kiwi_P2
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer_Horizontal
import com.bellako.kiwi.common.utils.AssetResolver
import com.bellako.kiwi.features.conversations.components.CharacterName
import com.bellako.kiwi.features.conversations.data.ConversationDomain
import com.bellako.kiwi.features.conversations.data.ConversationType
import com.bellako.kiwi.features.conversations.data.NextEventType
import com.bellako.kiwi.features.conversations.model.ConversationViewModel
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import com.bellako.kiwi.ui.getResponsiveSizeWidth

@Composable
@Suppress("MagicNumber")
fun DialogueScreen(
    conversation: ConversationDomain,
    viewModel: ConversationViewModel? = null,
) {
    val kiwiColor = LocalKiwiColors.current

    val infiniteTransition = rememberInfiniteTransition(label = "arrow_bounce")

    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -10f, // altura del salto
        animationSpec =
            infiniteRepeatable(
                animation = tween(600, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse,
            ),
        label = "arrow_offset",
    )

    val context = LocalContext.current

    Column(
        verticalArrangement = Arrangement.Bottom,
        modifier =
            Modifier
                .fillMaxSize()
                .clickable {},
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier =
                Modifier
                    .background(
                        Brush.verticalGradient(
                            -0f to Color.Transparent,
                            0.3f to kiwiColor.color2.copy(alpha = 0.6f),
                            0.5f to kiwiColor.color2.copy(alpha = 0.8f),
                        ),
                    ).padding(Spacing.medium, Spacing.large),
        ) {
            Box(modifier = Modifier.weight(0.35f)) {
                Box(
                    modifier =
                        Modifier
                            .matchParentSize()
                            .clip(CircleShape),
                ) {
                    Kiwi_Image(
                        AssetResolver.drawableOr(context, conversation.sprite, R.drawable.character_liria_base),
                        "Character image",
                        contentScale = ContentScale.Crop,
                        modifier =
                            Modifier
                                .matchParentSize()
                                .scale(1.6f)
                                .background(kiwiColor.color0),
                    )
                }
                Kiwi_Image(
                    R.drawable.dialogue_small_frame,
                    "Character frame",
                )
                Box(
                    contentAlignment = Alignment.BottomCenter,
                    modifier = Modifier.matchParentSize().offset(x = 0.dp, y = getResponsiveSizeHeight(25.dp)),
                ) {
                    CharacterName("Liria", conversation.dark, conversation.type == ConversationType.SMALL)
                }
            }
            Kiwi_Spacer_Horizontal()
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.weight(0.65f),
            ) {
                Kiwi_Image(
                    R.drawable.dialogue_small_bg,
                    "Dialogue frame",
                    contentScale = ContentScale.FillWidth,
                )
                Kiwi_P2(
                    KiwiTextArguments(
                        conversation.dialog,
                        textAlign = TextAlign.Center,
                        color = kiwiColor.color6,
                        modifier = Modifier.padding(Spacing.medium, Spacing.medium),
                    ),
                )
                Kiwi_Image(
                    R.drawable.ic_dialogue_arrow,
                    "Arrow",
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .size(getResponsiveSizeWidth(10.dp), getResponsiveSizeHeight(10.dp))
                            .offset(y = getResponsiveSizeHeight(offsetY.dp))
                            .clickable {
                                AudioManager.playSFX(context, R.raw.snd_fx_03_page)
                                viewModel?.next()
                            },
                )
            }
        }
    }
}

// =================================================================================================
@RequiresApi(Build.VERSION_CODES.O)
@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Suppress("MagicNumber")
@Composable
fun DialogueScreen_Preview() {
    Kiwi_Theme {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(LocalKiwiColors.current.color6),
        ) {
            DialogueScreen(
                conversation =
                    ConversationDomain(
                        1L,
                        "Conversación de prueba",
                        ConversationType.SMALL,
                        "liria_neutral",
                        null,
                        null,
                        false,
                        0,
                        0,
                        "Esto es un texto de prueba, que tiene que ser largo de narices sin nada",
                        "Esto es un texto de prueba, que tiene que ser largo de narices pero con M",
                        "Esto es un texto de prueba, que tiene que ser largo de narices pero con W",
                        NextEventType.END,
                        2,
                        onCompletedEvent = "",
                        onCompletedEntityId = 0,
                    ),
            )
        }
    }
}
