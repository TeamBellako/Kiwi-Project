package com.bellako.kiwi.common.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.bellako.kiwi.common.screens.components.LoadingModal

const val LOGIN_LOADING_ANIM_DURATION_MS = 600

@Composable
fun LoginLoadingScreen(
    visible: Boolean,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        modifier = modifier,
        visible = visible,
        enter =
            slideInVertically(
                initialOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(durationMillis = LOGIN_LOADING_ANIM_DURATION_MS, easing = EaseInOut),
            ) +
                fadeIn(
                    animationSpec = tween(durationMillis = LOGIN_LOADING_ANIM_DURATION_MS, easing = EaseInOut),
                ),
        exit =
            slideOutVertically(
                targetOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(durationMillis = LOGIN_LOADING_ANIM_DURATION_MS, easing = EaseInOut),
            ) +
                fadeOut(
                    animationSpec = tween(durationMillis = LOGIN_LOADING_ANIM_DURATION_MS, easing = EaseInOut),
                ),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(Color.Black),
            contentAlignment = Alignment.Center,
        ) {
            LoadingModal(color = Color.White)
        }
    }
}
