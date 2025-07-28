package com.bellako.kiwi.common.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.BuildConfig
import com.bellako.kiwi.common.tests.CommonTestTags
import com.bellako.kiwi.common.screens.components.Kiwi_AnnotatedStringArguments
import com.bellako.kiwi.common.screens.components.Kiwi_AnnotatedString_P2
import com.bellako.kiwi.common.screens.components.Kiwi_Button
import com.bellako.kiwi.common.screens.components.Kiwi_H2
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer
import com.bellako.kiwi.common.screens.components.Kiwi_TextArguments
import com.bellako.kiwi.ui.KiwiTheme
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveRelativeSize


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
            .padding(getResponsiveRelativeSize(Spacing.large)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Kiwi_H2(
            Kiwi_TextArguments(
            "SUPPORT",
            color = MaterialTheme.colorScheme.secondary,
            bold = true
        )
        )

        Kiwi_Spacer(Spacing.xLarge)

        val annotatedString = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.secondary,
                )
            ) {
                append("If you have any questions or need support, feel free to reach out to us at ")
            }

            withLink(link = LinkAnnotation.Clickable(
                tag = "EMAIL",
                linkInteractionListener = {
                    openEmailClient(context)
                },
            )) {
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.inversePrimary,
                        textDecoration = TextDecoration.Underline
                    )
                ) {
                    append(BuildConfig.MOBILE_COMPANY_EMAIL)
                }
            }
        }

        Kiwi_AnnotatedString_P2(
            Kiwi_AnnotatedStringArguments(
            annotatedString,
            TextAlign.Center
        )
        )

        Kiwi_Spacer(Spacing.xLarge)

        Kiwi_Button(Kiwi_TextArguments(
            "BACK",
            color = MaterialTheme.colorScheme.secondary,
            bold = true,
            modifier = Modifier
                .testTag(CommonTestTags.HELP_SCREEN)
        ), {
            navController.navigate(ScreenRoutes.SETTINGS)
        })
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
