package com.bellako.kiwi.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
fun Kiwi_Button(
    textArguments: Kiwi_TextArguments,
    onClick: () -> Unit,
    isLoading: Boolean = false,
    color: Color = MaterialTheme.colorScheme.secondary,
    testTag: String = "",
    rowModifier: Modifier = Modifier,
) {
    var buttonColor = color
    if(isLoading) {
        buttonColor = buttonColor.copy(alpha = 0.3F)
    }

    Box(modifier = rowModifier) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag(testTag),
            enabled = !isLoading
        ) {
            Kiwi_Label(textArguments)
        }
    }
}