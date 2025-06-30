package com.bellako.kiwi.ui.modals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bellako.kiwi.services.common.CommonTestTags
import com.bellako.kiwi.ui.components.Kiwi_Button
import com.bellako.kiwi.ui.components.Kiwi_H1
import com.bellako.kiwi.ui.components.Kiwi_P1
import com.bellako.kiwi.ui.components.Kiwi_Spacer
import com.bellako.kiwi.ui.components.Kiwi_TextArguments
import com.bellako.kiwi.ui.theme.KiwiTheme

@Composable
fun ErrorModal(
    modifier: Modifier = Modifier,
    errorMessage: String = "Uh-oh! It seems a careless scribe forgot to write this part of the story. \n\n Let's get back on track!",
    onRetry: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,

    ) {
        Icon(
            imageVector = Icons.Filled.Warning,
            contentDescription = "Error icon",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(64.dp)
        )

        Kiwi_Spacer()

        Kiwi_H1(Kiwi_TextArguments(
            "Wild Error Appeared!",
            color = MaterialTheme.colorScheme.inversePrimary
        ))

        Kiwi_Spacer(0.5F)

        Kiwi_P1(Kiwi_TextArguments(
            errorMessage,
            TextAlign.Center,
            modifier = Modifier.testTag(CommonTestTags.ERROR_MODAL)
        ))

        if (onRetry != null) {
            Box(
                modifier = Modifier.padding(24.dp)
            ) {
                Kiwi_Button(
                    Kiwi_TextArguments(
                        "RETRY",
                        color = Color.White,
                        bold = true
                    ),
                    onRetry
                )
            }
        }
    }
}

@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Composable
fun ErrorModalPreview() {
    KiwiTheme {
        ErrorModal(onRetry = {})
    }
}