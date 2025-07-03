package com.bellako.kiwi.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.BuildConfig
import com.bellako.kiwi.services.common.CommonTestTags
import com.bellako.kiwi.ui.components.Kiwi_Button
import com.bellako.kiwi.ui.components.Kiwi_H1
import com.bellako.kiwi.ui.components.Kiwi_P1
import com.bellako.kiwi.ui.components.Kiwi_P2
import com.bellako.kiwi.ui.components.Kiwi_Spacer
import com.bellako.kiwi.ui.components.Kiwi_TextArguments
import com.bellako.kiwi.ui.theme.KiwiTheme
import com.bellako.kiwi.ui.theme.Spacing


@Composable
fun HelpScreen(
    navController: NavController
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        HelpScreenLayout(navController)
    }
}

@Composable
private fun HelpScreenLayout(
    navController: NavController
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(Spacing.large),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Kiwi_H1(Kiwi_TextArguments(
            "SUPPORT",
            color = MaterialTheme.colorScheme.secondary,
            bold = true
        ))

        Kiwi_P1(Kiwi_TextArguments(
            "If you have any questions or need support, feel free to reach out to us at:",
            TextAlign.Center,
            color = MaterialTheme.colorScheme.outline
        ))

        Kiwi_P2(Kiwi_TextArguments(
            BuildConfig.MOBILE_COMPANY_EMAIL,
            TextAlign.Center,
            MaterialTheme.colorScheme.inversePrimary,
            bold = true,
            modifier = Modifier
                .clickable { openEmailClient(context) }
                .testTag(CommonTestTags.HELP_SCREEN)
        ))

        Kiwi_Spacer(Spacing.xLarge)

        Kiwi_Button(
            Kiwi_TextArguments(
                "BACK",
                color = MaterialTheme.colorScheme.secondary,
                bold = true
            ),
            { navController.navigate(ScreenRoutes.SETTINGS) }
        )
    }
}

private fun openEmailClient(context: Context) {
    val emailIntent = Intent(
        Intent.ACTION_SENDTO,
        "mailto:${BuildConfig.MOBILE_COMPANY_EMAIL}".toUri()
    )

    try {
        context.startActivity(emailIntent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Composable
fun HelpScreenPreview() {
    KiwiTheme {
        HelpScreen(
            navController = rememberNavController()
        )
    }
}
