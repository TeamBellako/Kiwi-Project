package com.bellako.kiwi.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bellako.kiwi.ui.components.Kiwi_Button
import com.bellako.kiwi.ui.components.Kiwi_H1
import com.bellako.kiwi.ui.components.Kiwi_P1
import com.bellako.kiwi.ui.components.Kiwi_Spacer
import com.bellako.kiwi.ui.components.Kiwi_TextArguments
import com.bellako.kiwi.ui.theme.KiwiTheme

@Composable
fun ErrorScreen(
    modifier: Modifier = Modifier,
    errorMessage: String = "Something went wrong. Please try again later.",
    onRetry: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,

    ) {
        Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = "Error icon",
            tint = Color.Red,
            modifier = Modifier.size(64.dp)
        )

        Kiwi_Spacer()

        Kiwi_H1(Kiwi_TextArguments(
            "Oops!",
            color = MaterialTheme.colorScheme.error
        ))

        Kiwi_Spacer(0.5F)

        Kiwi_P1(Kiwi_TextArguments(
            errorMessage,
            TextAlign.Center,
            modifier = Modifier.testTag(CommonTestTags.ERROR_SCREEN)
        ))

        if (onRetry != null) {
            Kiwi_Spacer()

            Box(
                modifier = Modifier.padding(24.dp)
            ) {
                Kiwi_Button(
                    Kiwi_TextArguments(
                        "Retry",
                        color = Color.White
                    ),
                    onRetry
                )
            }
        }
    }
}


@Preview
@Composable
fun UsersScreenPreview() {
    KiwiTheme {
        ErrorScreen(onRetry = {})
    }
}