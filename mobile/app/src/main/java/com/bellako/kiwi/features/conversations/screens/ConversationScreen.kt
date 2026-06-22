package com.bellako.kiwi.features.conversations.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.bellako.kiwi.R
import com.bellako.kiwi.audio.AudioManager
import com.bellako.kiwi.common.screens.components.Kiwi_Image
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer
import com.bellako.kiwi.common.screens.components.rememberCharacterIdleModifier
import com.bellako.kiwi.common.utils.AssetResolver
import com.bellako.kiwi.features.conversations.components.CharacterName
import com.bellako.kiwi.features.conversations.components.ConversationOption
import com.bellako.kiwi.features.conversations.components.Kiwi_DialogueText
import com.bellako.kiwi.features.conversations.components.Kiwi_TypewriterText
import com.bellako.kiwi.features.conversations.components.TypewriterState
import com.bellako.kiwi.features.conversations.components.rememberTypewriter
import com.bellako.kiwi.features.conversations.data.ConversationDomain
import com.bellako.kiwi.features.conversations.data.ConversationOptionDomain
import com.bellako.kiwi.features.conversations.data.ConversationType
import com.bellako.kiwi.features.conversations.data.NextEventType
import com.bellako.kiwi.features.conversations.model.ConversationViewModel
import com.bellako.kiwi.features.nodes.screens.LocalNodeEntryTransition
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import com.bellako.kiwi.ui.getResponsiveSizeWidth

private const val BG_FADE_MS = 700
private const val CHARACTER_LERP_MS = 900
private const val DIALOGUE_FADE_MS = 600
private const val OPTION_POP_MS = 450
private const val OPTION_STAGGER_MS = 140
private const val STAGE_GAP_MS = 250L
private const val DIALOGUE_ADVANCE_MS = 450

// The MainScreen AnimatedVisibility slides the conversation up over 400ms.
// When we entered behind the veil we need to wait for that slide to fully
// settle before lifting the veil — otherwise the top of the screen still
// shows the map peeking around the sliding conversation as the veil clears.
private const val CONVERSATION_SLIDE_IN_MS = 400L

private const val PROTAGONIST_SPRITE_KEY = "liria"

@Composable
@Suppress("MagicNumber")
fun ConversationScreen(
    conversation: ConversationDomain,
    viewModel: ConversationViewModel? = null,
) {
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
    val backgroundRes = AssetResolver.drawable(context, conversation.background)

    // Liria enters from the bottom; everyone else slides in from the left.
    // Sprite identifiers vary in case and can embed "liria" anywhere in the
    // string (e.g. "character_liria_neutral"), so match case-insensitively
    // anywhere in the name.
    val isProtagonist = conversation.sprite.contains(PROTAGONIST_SPRITE_KEY, ignoreCase = true)

    val bgAlpha = remember { Animatable(0f) }
    val characterProgress = remember { Animatable(0f) }
    val dialogueAlpha = remember { Animatable(0f) }
    val optionsClockMs = remember { Animatable(0f) }

    // Held back until the dialogue stage of the intro sequence so the text
    // doesn't finish typing before the dialogue box has faded in.
    var dialogueStageStarted by remember { mutableStateOf(false) }
    val typewriter = rememberTypewriter(conversation.dialog, play = dialogueStageStarted)

    val idleModifier = rememberCharacterIdleModifier(conversation.sprite)

    val optionsTotalMs =
        OPTION_POP_MS + (conversation.options.size - 1).coerceAtLeast(0) * OPTION_STAGGER_MS

    val nodeEntry = LocalNodeEntryTransition.current

    val advance: () -> Unit = {
        AudioManager.playSFX(context, R.raw.snd_fx_03_page)
        viewModel?.next()
    }

    LaunchedEffect(Unit) {
        // When entered behind the node-entry veil, the bg can be snapped
        // straight to opaque — the veil already hides the swap. Then we
        // give the AnimatedVisibility slide-in time to settle so the
        // conversation is fully in place before we ask the veil to clear
        // (the user would otherwise see the map at the top of the screen
        // around the still-sliding conversation as the veil thins out).
        // Without a veil (e.g. the auto-emitted first conversation on a
        // brand-new account) we keep the original alpha ramp so the slide
        // doesn't read as a hard cut.
        val veilUp = (nodeEntry?.veilAlpha ?: 0f) > 0f
        if (veilUp) {
            bgAlpha.snapTo(1f)
            delay(CONVERSATION_SLIDE_IN_MS)
        } else {
            bgAlpha.animateTo(1f, tween(BG_FADE_MS, easing = LinearEasing))
        }
        // Launched in parallel so the character lerp doesn't wait for the
        // veil to lift.
        launch { nodeEntry?.fadeOut() }
        delay(STAGE_GAP_MS)
        characterProgress.animateTo(1f, tween(CHARACTER_LERP_MS, easing = FastOutSlowInEasing))
        delay(STAGE_GAP_MS)
        dialogueStageStarted = true
        dialogueAlpha.animateTo(1f, tween(DIALOGUE_FADE_MS, easing = LinearEasing))
        delay(STAGE_GAP_MS)
        optionsClockMs.animateTo(
            targetValue = optionsTotalMs.toFloat(),
            animationSpec = tween(optionsTotalMs, easing = LinearEasing),
        )
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight

        if (backgroundRes != null) {
            Kiwi_Image(
                painterResourceId = backgroundRes,
                alt = "Conversation background",
                contentScale = ContentScale.Crop,
                modifier =
                    Modifier
                        .fillMaxSize()
                        .alpha(bgAlpha.value),
            )
        }

        Column(
            verticalArrangement = Arrangement.Bottom,
            modifier =
                Modifier
                    .fillMaxSize()
                    .clickable {
                        when {
                            !typewriter.isComplete -> typewriter.skip()
                            conversation.options.isEmpty() -> advance()
                        }
                    },
        ) {
            val charExtraX =
                if (isProtagonist) 0.dp else -screenWidth * (1f - characterProgress.value)
            val charExtraY =
                if (isProtagonist) screenHeight * (1f - characterProgress.value) else 0.dp

            Box(
                modifier =
                    Modifier
                        .height(getResponsiveSizeHeight(400.dp))
                        .fillMaxWidth()
                        .offset(
                            x = getResponsiveSizeWidth(-50.dp) + charExtraX,
                            y = getResponsiveSizeHeight(100.dp) + charExtraY,
                        ),
            ) {
                // Sprite
                Kiwi_Image(
                    painterResourceId =
                        AssetResolver.drawableOr(context, conversation.sprite, R.drawable.character_liria_base),
                    alt = "Character Pose",
                    modifier = idleModifier,
                )
            }
            DialogueBox(
                conversation = conversation,
                typewriter = typewriter,
                alpha = dialogueAlpha.value,
            )
            ConversationOptionsPanel(
                options = conversation.options,
                alpha = dialogueAlpha.value,
                optionsClockMs = optionsClockMs.value,
                arrowBounceOffsetY = offsetY,
                arrowVisible = typewriter.isComplete,
                onOptionClick = { option ->
                    AudioManager.playSFX(context, R.raw.snd_fx_03_page)
                    viewModel?.next(option)
                },
                onAdvance = advance,
            )
        }
    }
}

@Composable
@Suppress("MagicNumber")
private fun DialogueBox(
    conversation: ConversationDomain,
    typewriter: TypewriterState,
    alpha: Float,
    modifier: Modifier = Modifier,
) {
    val kiwiColor = LocalKiwiColors.current
    val context = LocalContext.current
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .alpha(alpha)
                .background(
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
                painterResourceId = getAsset(conversation, R.drawable.dialogue_light_small, context),
                alt = "Conversation modal",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.fillMaxWidth(),
            )
            AdvancingDialogueText(
                conversation = conversation,
                typewriter = typewriter,
                modifier = Modifier.matchParentSize(),
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
}

@Composable
private fun AdvancingDialogueText(
    conversation: ConversationDomain,
    typewriter: TypewriterState,
    modifier: Modifier = Modifier,
) {
    val kiwiColor = LocalKiwiColors.current
    // Scroll-up advance: the current line slides up and out the top while the
    // next line rises in from the bottom. clipToBounds keeps both inside the
    // dialogue box so nothing shows over the background.
    AnimatedContent(
        targetState = conversation,
        transitionSpec = {
            slideInVertically(tween(DIALOGUE_ADVANCE_MS)) { it } togetherWith
                slideOutVertically(tween(DIALOGUE_ADVANCE_MS)) { -it }
        },
        contentKey = { it.id },
        contentAlignment = Alignment.Center,
        modifier = modifier.clipToBounds(),
        label = "dialogue_advance",
    ) { conv ->
        val textColor = if (conv.dark) kiwiColor.color6 else kiwiColor.color3
        val textModifier = Modifier.padding(Spacing.medium, Spacing.medium)
        // Fill the box so the slide travels the full box height and each line
        // clears the masked region completely.
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize(),
        ) {
            if (conv.id == conversation.id) {
                Kiwi_TypewriterText(
                    typewriter = typewriter,
                    textAlign = TextAlign.Center,
                    color = textColor,
                    modifier = textModifier,
                )
            } else {
                Kiwi_DialogueText(
                    text = conv.dialog,
                    textAlign = TextAlign.Center,
                    color = textColor,
                    modifier = textModifier,
                )
            }
        }
    }
}

@Composable
@Suppress("MagicNumber")
private fun ConversationOptionsPanel(
    options: List<ConversationOptionDomain>,
    alpha: Float,
    optionsClockMs: Float,
    arrowBounceOffsetY: Float,
    arrowVisible: Boolean,
    onOptionClick: (ConversationOptionDomain) -> Unit,
    onAdvance: () -> Unit,
) {
    val kiwiColor = LocalKiwiColors.current
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .alpha(alpha)
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
            itemsIndexed(options) { index, option ->
                val itemStart = index * OPTION_STAGGER_MS
                val rawP =
                    ((optionsClockMs - itemStart) / OPTION_POP_MS).coerceIn(0f, 1f)
                val popScale =
                    if (rawP <= 0f) 0f else EaseOutBack.transform(rawP).coerceAtLeast(0f)
                Box(modifier = Modifier.scale(popScale)) {
                    ConversationOption(option, onClick = { onOptionClick(option) })
                }
                Kiwi_Spacer(Spacing.small)
            }
        }
        if (options.isEmpty()) {
            // Space stays reserved so the panel doesn't jump when the arrow
            // fades in after the typewriter finishes.
            Kiwi_Spacer(Spacing.medium)
            Kiwi_Image(
                R.drawable.ic_dialogue_arrow,
                "Arrow",
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .size(getResponsiveSizeWidth(8.dp), getResponsiveSizeHeight(8.dp))
                        .offset(y = getResponsiveSizeHeight(arrowBounceOffsetY.dp))
                        .alpha(if (arrowVisible) 1f else 0f)
                        .then(if (arrowVisible) Modifier.clickable(onClick = onAdvance) else Modifier),
            )
            Kiwi_Spacer(Spacing.large)
        } else {
            Kiwi_Spacer(Spacing.xLarge)
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

private fun getAsset(conversation: ConversationDomain): Int {
    val lineCount = estimateLineCount(conversation.dialog)

    return when {
        conversation.dark && lineCount <= 1 -> R.drawable.dialogue_dark_small
        conversation.dark && lineCount <= 2 -> R.drawable.dialogue_dark_medium
        conversation.dark && lineCount > 2 -> R.drawable.dialogue_dark_big
        !conversation.dark && lineCount <= 1 -> R.drawable.dialogue_light_small
        !conversation.dark && lineCount <= 2 -> R.drawable.dialogue_light_medium
        else -> R.drawable.dialogue_light_big
    }
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
