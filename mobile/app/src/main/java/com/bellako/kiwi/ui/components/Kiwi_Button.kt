package com.bellako.kiwi.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

@Composable
fun Kiwi_Button(
    text: String,
    onClick: () -> Unit,
    isLoading: Boolean = false,
    testTag: String = "",
    rowModifier: Modifier = Modifier,
) {
    Box(modifier = rowModifier) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .testTag(testTag),
            enabled = !isLoading
        ) {
            Kiwi_P1(Kiwi_TextArguments(
                text
            ))
        }
        if (isLoading) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(MaterialTheme.colorScheme.tertiary)
            )
        }
    }
}