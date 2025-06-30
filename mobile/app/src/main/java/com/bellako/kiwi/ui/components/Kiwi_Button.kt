package com.bellako.kiwi.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
fun Kiwi_Button(
    textArguments: Kiwi_TextArguments,
    onClick: () -> Unit,
    enabled: Boolean = true,
    color: Color = MaterialTheme.colorScheme.secondary,
    testTag: String = "",
    rowModifier: Modifier = Modifier,
) {
    Box(modifier = rowModifier) {
        Button(
            onClick = onClick,
            enabled = enabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = color,
                disabledContainerColor = color.copy(alpha = 0.1f),
                contentColor = MaterialTheme.colorScheme.onSecondary,
                disabledContentColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.3f)
            ),
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag(testTag)
        ) {
            Kiwi_Label(textArguments)
        }
    }
}
