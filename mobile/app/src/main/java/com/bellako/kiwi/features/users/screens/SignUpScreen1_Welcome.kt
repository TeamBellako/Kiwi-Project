package com.bellako.kiwi.features.users.screens

import android.annotation.SuppressLint
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.analytics.FirebaseEventNames
import com.bellako.kiwi.analytics.firebaseLogEvent
import com.bellako.kiwi.common.data.ScreenRoutes
import com.bellako.kiwi.common.screens.components.KiwiAnnotatedStringArguments
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_AnnotatedString_P2
import com.bellako.kiwi.common.screens.components.Kiwi_Button
import com.bellako.kiwi.common.screens.components.Kiwi_H2
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer
import com.bellako.kiwi.common.tests.CommonTestTags
import com.bellako.kiwi.features.users.data.UsersState
import com.bellako.kiwi.features.users.model.IUsersViewModel
import com.bellako.kiwi.features.users.tests.UsersFakeViewModel
import com.bellako.kiwi.features.users.tests.UsersTestFactory.validUsersDTO
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight

@Composable
fun SignUpScreen1_Welcome(
    viewModel: IUsersViewModel,
    navController: NavController,
) {
    SignUpScreen {
        Welcome(
            viewModel,
            navController,
        )

        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(bottom = getResponsiveSizeHeight(Spacing.medium)),
            contentAlignment = Alignment.BottomCenter,
        ) {
            GoToLogIn {
                navController.navigate(ScreenRoutes.LOGIN)
            }
        }
    }
}

@Composable
private fun Welcome(
    viewModel: IUsersViewModel,
    navController: NavController,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(getResponsiveSizeHeight(Spacing.medium))
                .testTag(CommonTestTags.USERS_SCREEN),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Kiwi_H2(
            KiwiTextArguments(
                "Your Legend is About\nTo Be Forged...",
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.secondary,
                bold = true,
            ),
        )

        Kiwi_Spacer(Spacing.xLarge)

        Kiwi_Button(
            textArguments =
                KiwiTextArguments(
                    "LET'S DO IT",
                    color = MaterialTheme.colorScheme.secondary,
                    bold = true,
                ),
            color = MaterialTheme.colorScheme.primary,
            onClick = {
                firebaseLogEvent(FirebaseEventNames.SIGNUP_1_STARTED)

                viewModel.onEmailChanged("")
                viewModel.onPasswordChanged("")
                viewModel.resetUiState()
                navController.navigate(ScreenRoutes.SIGNUP2_FORM)
            },
        )
    }
}

@Composable
private fun GoToLogIn(onSignUp: () -> Unit) {
    val annotatedString =
        buildAnnotatedString {
            withStyle(
                style =
                    SpanStyle(
                        color = MaterialTheme.colorScheme.secondary,
                    ),
            ) {
                append("Not Your First Time?\nContinue Your Adventure By\n")
            }

            withLink(
                link =
                    LinkAnnotation.Clickable(
                        tag = "HERE",
                        linkInteractionListener = {
                            onSignUp()
                        },
                    ),
            ) {
                withStyle(
                    style =
                        SpanStyle(
                            color = MaterialTheme.colorScheme.inversePrimary,
                            textDecoration = TextDecoration.Underline,
                        ),
                ) {
                    append("Logging In")
                }
            }
        }

    Kiwi_AnnotatedString_P2(
        KiwiAnnotatedStringArguments(
            annotatedString,
            TextAlign.Center,
        ),
    )
}

// -------------------------------------------------------------------------------------------------

@SuppressLint("ViewModelConstructorInComposable")
@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Composable
fun SignUpScreen1_Welcome_Preview() {
    Kiwi_Theme {
        SignUpScreen1_Welcome(
            UsersFakeViewModel(UsersState(validUsersDTO().email, validUsersDTO().password, validUsersDTO().registerDate)),
            navController = rememberNavController(),
        )
    }
}
