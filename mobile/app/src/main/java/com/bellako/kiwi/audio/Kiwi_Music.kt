package com.bellako.kiwi.audio

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.bellako.kiwi.R

@Composable
fun Kiwi_Music_Login() {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        AudioManager.playMusic(
            context,
            listOf(
                AudioLayer(R.raw.growtale_stepswithin, true),
                AudioLayer(R.raw.growtale_stepswithin_enigma, false),
            ),
        )
    }
}

@Composable
fun Kiwi_Music_SignUp() {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        AudioManager.playMusic(
            context,
            listOf(
                AudioLayer(R.raw.growtale_stepswithin, false),
                AudioLayer(R.raw.growtale_stepswithin_enigma, true),
            ),
        )
    }
}

@Composable
fun Kiwi_Music_Home() {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        AudioManager.playMusic(
            context,
            listOf(
                AudioLayer(R.raw.growtale_maintheme, true),
                AudioLayer(R.raw.growtale_maintheme_chiptunesynth, false),
            ),
        )
    }
}

@Composable
fun Kiwi_Music_Settings() {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        AudioManager.playMusic(
            context,
            listOf(
                AudioLayer(R.raw.growtale_maintheme, false),
                AudioLayer(R.raw.growtale_maintheme_chiptunesynth, true),
            ),
        )
    }
}
