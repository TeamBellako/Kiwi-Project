package com.bellako.kiwi.audio

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.bellako.kiwi.R
import com.bellako.kiwi.common.utils.AssetResolver

@Suppress("MagicNumber")
@Composable
fun Kiwi_Music_Login() {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        AudioManager.playMusic(
            context,
            listOf(
                AudioLayer(R.raw.growtale_stepswithin, 0.35f, true),
                AudioLayer(R.raw.growtale_stepswithin_enigma, 0.35f, false),
            ),
        )
    }
}

@Suppress("MagicNumber")
@Composable
fun Kiwi_Music_SignUp() {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        AudioManager.playMusic(
            context,
            listOf(
                AudioLayer(R.raw.growtale_stepswithin, 0.35f, false),
                AudioLayer(R.raw.growtale_stepswithin_enigma, 0.35f, true),
            ),
        )
    }
}

@Suppress("MagicNumber")
@Composable
fun Kiwi_Music_Home() {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        AudioManager.playMusic(
            context,
            listOf(
                AudioLayer(R.raw.growtale_maintheme, 0.35f, true),
                AudioLayer(R.raw.growtale_maintheme_chiptunesynth, 0.35f, false),
            ),
        )
    }
}

@Suppress("MagicNumber")
@Composable
fun Kiwi_Music_Settings() {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        AudioManager.playMusic(
            context,
            listOf(
                AudioLayer(R.raw.growtale_maintheme, 0.35f, false),
                AudioLayer(R.raw.growtale_maintheme_chiptunesynth, 0.35f, true),
            ),
        )
    }
}

@Suppress("MagicNumber")
@Composable
fun Kiwi_Music_Combat(music: String?) {
    val context = LocalContext.current
    DisposableEffect(music) {
        val resolved = AssetResolver.rawOr(context, music, R.raw.growtale_battleofhabits)
        AudioManager.playMusic(
            context,
            listOf(AudioLayer(resolved, 0.35f, true)),
            fadeOutFirst = true,
        )
        onDispose {
            AudioManager.playMusic(
                context,
                listOf(
                    AudioLayer(R.raw.growtale_maintheme, 0.35f, true),
                    AudioLayer(R.raw.growtale_maintheme_chiptunesynth, 0.35f, false),
                ),
                fadeOutFirst = true,
            )
        }
    }
}

@Composable
fun Kiwi_Music_Conversation() {
    LaunchedEffect(Unit) {
        AudioManager.fadeOutMusic()
    }
}
