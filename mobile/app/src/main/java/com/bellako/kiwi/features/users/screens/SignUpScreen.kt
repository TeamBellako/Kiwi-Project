package com.bellako.kiwi.features.users.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import com.bellako.kiwi.R
import com.bellako.kiwi.common.screens.components.Kiwi_Image
import com.bellako.kiwi.ui.Kiwi_Theme

@Composable
fun SignUpScreen(
    showSmoke: Boolean = true,
    content: @Composable () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        Kiwi_Image(
            R.drawable.ph_onboarding_bkg,
            "Sign Up Background",
            modifier =
                Modifier
                    .fillMaxSize()
                    .align(Alignment.TopCenter),
            contentScale = ContentScale.Crop,
        )

        // Ambient "smoke limbo" haze over the backdrop but under the UI. Opt-out
        // for the app-selection step (SignUpScreen4_Apps), which passes false.
        if (showSmoke) {
            SmokeLimboOverlay(modifier = Modifier.fillMaxSize())
        }

        Box(
            modifier =
                Modifier
                    .fillMaxSize(),
            contentAlignment = Alignment.Center,
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
fun SignUpScreen_Preview() {
    Kiwi_Theme {
        SignUpScreen {}
    }
}
