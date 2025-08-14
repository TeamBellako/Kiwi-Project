package com.bellako.kiwi.features.users.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import com.bellako.kiwi.R
import com.bellako.kiwi.ui.KiwiTheme
import com.bellako.kiwi.common.screens.components.Kiwi_Image
import com.bellako.kiwi.ui.Spacing
import androidx.compose.runtime.Composable
import com.bellako.kiwi.ui.getResponsiveSizeHeight


@Composable
fun SignUpScreen(
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Kiwi_Image(
            R.drawable.ph_onboarding_bkg,
            "Sign Up Background",
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(getResponsiveSizeHeight(Spacing.medium)),
            contentAlignment = Alignment.Center
        ) {

            content()

        }
    }
}

// -------------------------------------------------------------------------------------------------

@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Composable
fun SignUpScreenPreview() {
    KiwiTheme {
        SignUpScreen() {}
    }
}
