package com.bellako.kiwi.audio

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.bellako.kiwi.R

@Composable
fun Kiwi_Music_Home() {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        AudioManager.playMusic(context, listOf(
            AudioLayer(R.raw.music_stepswithin, true),
            AudioLayer(R.raw.music_stepswithin_enigma, false)
        ))
    }
}

@Composable
fun Kiwi_Music_SignUp() {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        AudioManager.playMusic(context, listOf(
            AudioLayer(R.raw.music_stepswithin, true),
            AudioLayer(R.raw.music_stepswithin_enigma, false)
        ))
    }
}
