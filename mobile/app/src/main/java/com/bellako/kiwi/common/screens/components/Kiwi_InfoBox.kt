package com.bellako.kiwi.common.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bellako.kiwi.features.users.tests.UsersTestTags
import com.bellako.kiwi.ui.KiwiTheme
import com.bellako.kiwi.ui.getResponsiveSizeHeight

@Composable
fun Kiwi_InfoBox(
    message: String,
    color: Color,
    testTag: String,
) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(color),
    ) {
        Kiwi_P2(
            Kiwi_TextArguments(
                message,
                modifier =
                    Modifier
                        .padding(getResponsiveSizeHeight(10.dp))
                        .testTag(testTag),
            ),
        )
    }
}

// -------------------------------------------------------------------------------------------------

@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Composable
fun Kiwi_InfoBoxPreview() {
    KiwiTheme {
        Column {
            Kiwi_InfoBox(
                message = "Invalid email or password",
                color = MaterialTheme.colorScheme.error,
                testTag = UsersTestTags.ERROR_TEXT,
            )
        }
    }
}
