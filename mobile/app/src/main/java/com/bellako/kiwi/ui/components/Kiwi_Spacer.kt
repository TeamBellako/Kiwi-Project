package com.bellako.kiwi.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.times
import com.bellako.kiwi.ui.theme.SEPARATOR_HEIGHT

@Composable
fun Kiwi_Spacer(
    heightMultiplier: Float = 1.0F
) {
    Spacer(Modifier.height(heightMultiplier * SEPARATOR_HEIGHT))
}