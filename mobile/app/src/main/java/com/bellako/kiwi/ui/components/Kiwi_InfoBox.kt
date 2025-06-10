package com.bellako.kiwi.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
fun Kiwi_InfoBox(
    message: String,
    color: Color,
    testTag: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color)
    ) {
        Kiwi_P1(Kiwi_TextArguments(
            message,
            modifier = Modifier
                .padding(10.dp)
                .testTag(testTag),
        ))
    }
}