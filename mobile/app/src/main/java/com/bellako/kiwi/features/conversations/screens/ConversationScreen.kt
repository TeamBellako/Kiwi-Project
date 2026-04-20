package com.bellako.kiwi.features.conversations.screens

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.EaseInOut
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer
import com.bellako.kiwi.features.conversations.components.CharacterName
import com.bellako.kiwi.features.conversations.components.ConversationOption
import com.bellako.kiwi.features.conversations.data.ConversationDomain
import com.bellako.kiwi.features.conversations.data.ConversationOptionDomain
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
fun ConversationScreen(
    conversation: ConversationDomain,
    viewModel: ConversationViewModel? = null,
) {
    val kiwiColor = LocalKiwiColors.current
    val infiniteTransition = rememberInfiniteTransition(label = "arrow_bounce")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -8f, // altura del salto
        animationSpec =
            infiniteRepeatable(
                animation = tween(1000, easing = EaseInOut),
                repeatMode = RepeatMode.Reverse,
            ),
        label = "arrow_offset",
    )
    val context = LocalContext.current

    // Background image
    Column(
        verticalArrangement = Arrangement.Bottom,
        modifier =
            Modifier
                .fillMaxSize()
                .clickable {},
    ) {
        Box(
            modifier =
                Modifier
                    .height(getResponsiveSizeHeight(400.dp))
                    .fillMaxWidth()
                    .offset(
                        x = getResponsiveSizeWidth(-50.dp),
                        y = getResponsiveSizeHeight(100.dp),
                    ),
        ) {
            // Sprite
            Kiwi_Image(
                painterResourceId = R.drawable.liria_neutral,
                alt = "Character Pose",
            )
        }
        // Dialogue
        Box(
            modifier =
                Modifier.fillMaxWidth().background(
                    Brush.verticalGradient(
                        -0.2f to Color.Transparent,
                        0.5f to kiwiColor.color2,
                        1f to kiwiColor.color2,
                    ),
                ),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(horizontal = Spacing.medium),
            ) {
                Kiwi_Image(
                    painterResourceId = getAsset(conversation, R.drawable.dialogue_light_small, LocalContext.current),
                    alt = "Conversation modal",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.fillMaxWidth(),
                )
                Kiwi_P2(
                    KiwiTextArguments(
                        conversation.dialog,
                        textAlign = TextAlign.Center,
                        color = if (conversation.dark) kiwiColor.color6 else kiwiColor.color3,
                        modifier =
                            Modifier.padding(Spacing.medium, Spacing.medium),
                    ),
                )
                Box(
                    modifier =
                        Modifier
                            .matchParentSize()
                            .offset(x = getResponsiveSizeWidth(25.dp)),
                ) {
                    CharacterName("Liria", conversation.dark, false)
                }
            }
        }
        // Options
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(kiwiColor.color2),
        ) {
            val optionHeight = getResponsiveSizeHeight(50.dp)
            val maxVisible = 3
            Kiwi_Spacer(Spacing.medium)
            LazyColumn(
                modifier =
                    Modifier
                        .padding(horizontal = Spacing.medium)
                        .heightIn(max = optionHeight * maxVisible + Spacing.small * 2),
            ) {
                items(conversation.options) { option ->
                    ConversationOption(option, onClick = {
                        AudioManager.playSFX(context, R.raw.snd_fx_03_page)
                        viewModel?.next(option)
                    })
                    Kiwi_Spacer(Spacing.small)
                }
            }
            if (conversation.options.size == 0) {
                Kiwi_Spacer(Spacing.medium)
                Kiwi_Image(
                    R.drawable.ic_dialogue_arrow,
                    "Arrow",
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .size(getResponsiveSizeWidth(8.dp), getResponsiveSizeHeight(8.dp))
                            .offset(y = getResponsiveSizeHeight(offsetY.dp))
                            .clickable {
                                AudioManager.playSFX(context, R.raw.snd_fx_03_page)
                                viewModel?.next()
                            },
                )
                Kiwi_Spacer(Spacing.large)
            } else {
                Kiwi_Spacer(Spacing.xLarge)
            }
        }
    }
}

private fun estimateLineCount(
    text: String,
    charsPerLine: Int = 35,
): Int {
    if (text.isBlank()) return 1
    return (text.length + charsPerLine - 1) / charsPerLine
}

private fun getAsset(
    conversation: ConversationDomain,
    painterResourceId: Int,
    context: Context,
): Int {
    val baseName = context.resources.getResourceEntryName(painterResourceId)
    var resourceName = baseName
    val lineCount = estimateLineCount(conversation.dialog)
    val size =
        when {
            lineCount <= 1 -> "small"
            lineCount <= 2 -> "medium"
            else -> "big"
        }
    val color = if (conversation.dark) "dark" else "light"
    resourceName = "dialogue_${color}_$size"
    return context.resources.getIdentifier(resourceName, "drawable", context.packageName)
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Suppress("MagicNumber")
@Composable
fun ConversationScreen_Preview() {
    Kiwi_Theme {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(LocalKiwiColors.current.color6),
        ) {
            ConversationScreen(
                conversation =
                    ConversationDomain(
                        1L,
                        "Conversación de prueba",
                        ConversationType.FULL,
                        1,
                        1,
                        1,
                        1,
                        false,
                        0,
                        0,
                        "Esto es un texto de prueba, que tiene que ser largo de narices sin nada",
                        "Esto es un texto de prueba, que tiene que ser largo de narices pero con M",
                        "Esto es un texto de prueba, que tiene que ser largo de narices pero con W",
                        NextEventType.END,
                        2,
                        options =
                            listOf(
                                ConversationOptionDomain(
                                    1L,
                                    "Option 1",
                                    "Option 1",
                                    "Option 1",
                                    2,
                                    null,
                                ),
                                ConversationOptionDomain(2L, "Option 2", "Option 2", "Option 2", 2, null),
                                ConversationOptionDomain(2L, "Option 2", "Option 2", "Option 2", 2, null),
                                ConversationOptionDomain(2L, "Option 2", "Option 2", "Option 2", 2, null),
                                ConversationOptionDomain(2L, "Option 2", "Option 2", "Option 2", 2, null),
                            ),
                        onCompletedEvent = "",
                        onCompletedEntityId = 0,
                    ),
            )
        }
    }
}
