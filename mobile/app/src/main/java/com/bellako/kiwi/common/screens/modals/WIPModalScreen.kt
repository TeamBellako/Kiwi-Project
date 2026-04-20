package com.bellako.kiwi.common.screens.modals

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.util.AndroidRuntimeException
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.BuildConfig
import com.bellako.kiwi.R
import com.bellako.kiwi.audio.AudioManager
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_FixedSizeButton
import com.bellako.kiwi.common.screens.components.Kiwi_H2
import com.bellako.kiwi.common.screens.components.Kiwi_P2
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer
import com.bellako.kiwi.common.tests.CommonTestTags
import com.bellako.kiwi.common.utils.Logger.warn
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight

@Composable
fun WIPModalScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    message: String =
        "Coming soon!",
    subMessage: String =
        "This feature isn’t ready just yet, but we’re building it for you! Stay tuned for updates.",
    buttonMessage: String = "BACK",
    onButtonClick: (() -> Unit)? = null,
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        AudioManager.playSFX(context, R.raw.snd_ui_error)
    }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(LocalKiwiColors.current.color2),
        contentAlignment = Alignment.Center,
    ) {
        WIPModalLayout(
            navController,
            modifier,
            message,
            subMessage,
            buttonMessage,
            onButtonClick,
        )
    }
}

@Composable
private fun WIPModalLayout(
    navController: NavController,
    modifier: Modifier = Modifier,
    message: String,
    subMessage: String,
    buttonMessage: String,
    onButtonClick: (() -> Unit)? = null,
) {
    val context = LocalContext.current
    val kiwiColors = LocalKiwiColors.current

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(getResponsiveSizeHeight(Spacing.medium)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Filled.Handyman,
            contentDescription = "WIP icon",
            tint = kiwiColors.color0A,
            modifier =
                Modifier
                    .size(getResponsiveSizeHeight(50.dp)),
        )

        Kiwi_Spacer(Spacing.xLarge)

        Kiwi_H2(
            KiwiTextArguments(
                message,
                color = kiwiColors.colorF,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
            ),
        )

        Kiwi_Spacer(Spacing.xLarge)

        Kiwi_P2(
            KiwiTextArguments(
                subMessage,
                TextAlign.Center,
                color = kiwiColors.colorF1,
                modifier =
                    Modifier
                        .testTag(CommonTestTags.ERROR_MODAL),
            ),
        )

        Kiwi_Spacer(Spacing.xLarge)

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .testTag(CommonTestTags.SETTINGS_SCREEN),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Kiwi_FixedSizeButton(
                horizontalMargin = Spacing.xLarge,
                textArguments =
                    KiwiTextArguments(
                        "CONTACT US",
                        color = kiwiColors.color6,
                        fontWeight = FontWeight.Bold,
                    ),
                color = kiwiColors.color5A,
                onClick = { openLinkInBrowser(context, BuildConfig.CONCIERGE_FORM_LINK) },
            )

            Kiwi_Spacer()

            Kiwi_FixedSizeButton(
                horizontalMargin = Spacing.xLarge,
                textArguments =
                    KiwiTextArguments(
                        "REPORT A BUG",
                        color = kiwiColors.color6,
                        fontWeight = FontWeight.Bold,
                    ),
                color = kiwiColors.color5A,
                onClick = { openLinkInBrowser(context, BuildConfig.BUG_FORM_LINK) },
            )

            Kiwi_Spacer()

            Kiwi_FixedSizeButton(
                horizontalMargin = Spacing.xLarge,
                textArguments =
                    KiwiTextArguments(
                        buttonMessage,
                        color = kiwiColors.color6,
                        fontWeight = FontWeight.Bold,
                    ),
                color = kiwiColors.color5A,
                onClick = {
                    if (onButtonClick != null) {
                        onButtonClick
                    } else {
                        navController.navigateUp()
                    }
                },
            )
        }
    }
}

private fun openLinkInBrowser(
    context: Context,
    url: String,
) {
    val browserIntent = Intent(Intent.ACTION_VIEW, url.toUri())

    try {
        context.startActivity(browserIntent)
    } catch (e: ActivityNotFoundException) {
        warn("No browser app found: ${e.message}")
    } catch (e: AndroidRuntimeException) {
        warn("Runtime error: ${e.message}")
    }
}

// -------------------------------------------------------------------------------------------------

@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Composable
fun WIPModal_Preview() {
    Kiwi_Theme {
        WIPModalScreen(navController = rememberNavController()) {}
    }
}
