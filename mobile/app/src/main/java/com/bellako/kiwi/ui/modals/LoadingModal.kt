package com.bellako.kiwi.ui.modals

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import com.bellako.kiwi.services.common.CommonTestTags
import com.bellako.kiwi.ui.theme.KiwiTheme

@Composable
fun LoadingModal(
    color: Color = MaterialTheme.colorScheme.secondary,
    trackColor: Color = Color.Transparent
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag(CommonTestTags.LOADING_MODAL),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = color,
            trackColor = trackColor
        )
    }
}

@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Composable
fun LoadingModalPreview() {
    KiwiTheme {
        LoadingModal()
    }
}