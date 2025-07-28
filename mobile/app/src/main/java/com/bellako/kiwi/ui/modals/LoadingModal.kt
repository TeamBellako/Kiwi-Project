package com.bellako.kiwi.ui.modals

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bellako.kiwi.services.common.CommonTestTags
import com.bellako.kiwi.ui.theme.KiwiTheme
import com.bellako.kiwi.ui.theme.getResponsiveRelativeSize

@Composable
fun LoadingModal(
    color: Color = MaterialTheme.colorScheme.secondary,
    trackColor: Color = Color.Transparent
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(modifier = Modifier.size(getResponsiveRelativeSize(50.dp))) {
            LoadingIcon(color, trackColor)
        }
    }
}

@Composable
fun LoadingIcon(
    color: Color = MaterialTheme.colorScheme.secondary,
    trackColor: Color = Color.Transparent
) {
    val isPreview = LocalInspectionMode.current
    if (isPreview) {
        // Indicators not shown in preview, show as static circle instead
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = color,
                center = Offset(size.width / 2f, size.height / 2f),
                radius = (size.minDimension - 11f) / 2f,
                style = Stroke(width = 11f)
            )
        }
    } else {
        // Actual indicator in runtime
        CircularProgressIndicator(
            color = color,
            trackColor = trackColor,
            modifier = Modifier
                .fillMaxSize()
                .testTag(CommonTestTags.LOADING_MODAL)
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
