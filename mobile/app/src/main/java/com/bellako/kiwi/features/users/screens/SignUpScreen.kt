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
import androidx.navigation.NavController
import com.bellako.kiwi.R
import com.bellako.kiwi.common.screens.components.Kiwi_Image
import com.bellako.kiwi.features.nodes.screens.NodeEntryTransitionController
import com.bellako.kiwi.ui.Kiwi_Theme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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

// Plays the fullscreen veil (fade-to-black + brief hold) and only then
// navigates to [route], so the step change happens entirely behind the veil.
// The destination screen lifts the veil on mount (nodeEntry.fadeOut()).
//
// [scope] MUST be a Compose-aware scope (rememberCoroutineScope) — the veil's
// Animatable needs a MonotonicFrameClock, which a bare CoroutineScope on the
// main dispatcher doesn't have. When [nodeEntry] is null (previews / tests with
// no controller provided) we just navigate, so the flow still works uncovered.
fun signupVeilNavigate(
    nodeEntry: NodeEntryTransitionController?,
    scope: CoroutineScope,
    navController: NavController,
    route: String,
) {
    if (nodeEntry == null) {
        navController.navigate(route)
        return
    }
    scope.launch {
        nodeEntry.enter()
        navController.navigate(route)
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
