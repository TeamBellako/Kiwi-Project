package com.bellako.kiwi.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import com.bellako.kiwi.ui.tags.KIWI_LOADING_INDICATOR

@Composable
fun Kiwi_LoadingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .testTag(KIWI_LOADING_INDICATOR),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}