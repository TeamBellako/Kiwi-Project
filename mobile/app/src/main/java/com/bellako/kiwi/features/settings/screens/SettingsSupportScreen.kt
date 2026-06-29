package com.bellako.kiwi.features.settings.screens

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.AndroidRuntimeException
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.BuildConfig
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_FixedSizeButton
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer
import com.bellako.kiwi.common.utils.Logger.warn
import com.bellako.kiwi.features.appbar.screens.AppBarScreen
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight

@Composable
fun SettingsSupportScreen(
    navController: NavController,
) {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(LocalKiwiColors.current.color2)
                .padding(getResponsiveSizeHeight(Spacing.medium)),
    ) {
        SettingsSupportScreenContent(navController = navController)
    }
}

@Composable
private fun SettingsSupportScreenContent(navController: NavController) {
    val context = LocalContext.current
    val kiwiColors = LocalKiwiColors.current

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SettingsSubScreenHeader(
            title = "Support",
            navController = navController,
        )

        Kiwi_Spacer(Spacing.medium)

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
    }
}

private fun openLinkInBrowser(
    context: Context,
    url: String,
) {
    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

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
fun SettingsSupportScreen_Preview() {
    Kiwi_Theme {
        Scaffold(
            bottomBar = {
                AppBarScreen(navController = rememberNavController())
            },
            content = { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues)) {
                    SettingsSupportScreen(navController = rememberNavController())
                }
            },
        )
    }
}


