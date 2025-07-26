package com.bellako.kiwi.features.users.signup

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.R
import com.bellako.kiwi.services.common.CommonTestTags
import com.bellako.kiwi.ui.screens.ScreenRoutes
import com.bellako.kiwi.ui.components.Kiwi_TextArguments
import com.bellako.kiwi.ui.theme.KiwiTheme
import com.bellako.kiwi.ui.components.Kiwi_Image
import com.bellako.kiwi.ui.theme.Spacing
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.withLink
import com.bellako.kiwi.audio.AudioLayer
import com.bellako.kiwi.audio.AudioManager
import com.bellako.kiwi.features.users.IUsersViewModel
import com.bellako.kiwi.features.users.UsersFakeViewModel
import com.bellako.kiwi.features.users.UsersState
import com.bellako.kiwi.services.analytics.FirebaseEventLogger
import com.bellako.kiwi.services.analytics.FirebaseEventNames
import com.bellako.kiwi.ui.components.Kiwi_AnnotatedStringArguments
import com.bellako.kiwi.ui.components.Kiwi_AnnotatedString_P2
import com.bellako.kiwi.ui.components.Kiwi_Button
import com.bellako.kiwi.ui.components.Kiwi_H2
import com.bellako.kiwi.ui.components.Kiwi_Spacer
import com.bellako.kiwi.ui.theme.getResponsiveRelativeSize
import com.google.firebase.analytics.FirebaseAnalytics


@Composable
fun SignUpWelcomeScreen(
    viewModel: IUsersViewModel,
    navController: NavController
) {
    val context = LocalContext.current

    BackHandler(enabled = true) { } // Do nothing, ignore native back

    LaunchedEffect(Unit) {
        AudioManager.playMusic(context, listOf(
            AudioLayer(R.raw.music_stepswithin, false),
            AudioLayer(R.raw.music_stepswithin_enigma, true)
        ))
    }


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
                .padding(getResponsiveRelativeSize(Spacing.medium)),
            contentAlignment = Alignment.Center
        ) {
            Welcome(
                viewModel,
                navController
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = getResponsiveRelativeSize(Spacing.medium)),
                contentAlignment = Alignment.BottomCenter
            ) {

                GoToLogIn() {
                    navController.navigate(ScreenRoutes.LOGIN)
                }

            }
        }
    }
}

@Composable
private fun Welcome(
    viewModel: IUsersViewModel,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .testTag(CommonTestTags.USERS_SCREEN),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Kiwi_H2(Kiwi_TextArguments(
            "Your Legend is About\nTo Be Forged...",
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.secondary,
            bold = true
        ))

        Kiwi_Spacer(Spacing.xLarge)

        Kiwi_Button(
            Kiwi_TextArguments(
                "LET'S DO IT",
                color = MaterialTheme.colorScheme.secondary,
                bold = true
            ),
            color = MaterialTheme.colorScheme.primary,
            onClick = {
                FirebaseEventLogger.logEvent(FirebaseEventNames.ONBOARDING_STARTED)

                viewModel.onEmailChanged("")
                viewModel.onPasswordChanged("")
                viewModel.resetUiState()
                navController.navigate(ScreenRoutes.SIGNUP)
            },
        )
    }
}

@Composable
private fun GoToLogIn(
    onSignUp: () -> Unit
) {
    val annotatedString = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                color = MaterialTheme.colorScheme.secondary,
            )
        ) {
            append("Not Your First Time?\nContinue Your Adventure By\n")
        }

        withLink(link = LinkAnnotation.Clickable(
            tag = "HERE",
            linkInteractionListener = {
                onSignUp()
            },
        )) {
            withStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.inversePrimary,
                    textDecoration = TextDecoration.Underline
                )
            ) {
                append("Logging In")
            }
        }
    }

    Kiwi_AnnotatedString_P2(Kiwi_AnnotatedStringArguments(
        annotatedString,
        TextAlign.Center,
    ))
}

@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Composable
fun SignUpWelcomeScreenPreview() {
    KiwiTheme {
        SignUpWelcomeScreen(
            UsersFakeViewModel(
                UsersState("finn@thehuman.com", "Math3matical!"),
            ),
            navController = rememberNavController()
        )
    }
}
