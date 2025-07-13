package com.bellako.kiwi.features.users.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
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
import com.bellako.kiwi.ui.modals.ErrorModal
import com.bellako.kiwi.services.common.UIState
import com.bellako.kiwi.ui.screens.ScreenRoutes
import com.bellako.kiwi.ui.components.Kiwi_H1
import com.bellako.kiwi.ui.components.Kiwi_InfoBox
import com.bellako.kiwi.ui.components.Kiwi_Spacer
import com.bellako.kiwi.ui.components.Kiwi_TextArguments
import com.bellako.kiwi.ui.theme.KiwiTheme
import com.bellako.kiwi.ui.components.Kiwi_Image
import com.bellako.kiwi.ui.theme.Spacing
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.runtime.Composable
import com.bellako.kiwi.features.users.IUsersViewModel
import com.bellako.kiwi.features.users.UsersFakeViewModel
import com.bellako.kiwi.features.users.UsersState
import com.bellako.kiwi.features.users.UsersTestTags
import com.bellako.kiwi.ui.components.Kiwi_Button
import com.bellako.kiwi.ui.components.Kiwi_InputField
import com.bellako.kiwi.ui.components.Kiwi_P1


@Composable
fun SignUpScreen(
    viewModel: IUsersViewModel,
    navController: NavController
) {

    viewModel.onEmailChanged("");
    viewModel.onPasswordChanged("");

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
                .padding(Spacing.medium),
            contentAlignment = Alignment.Center
        ) {
            SignUp(
                viewModel,
                navController
            )
        }
    }
}

@Composable
private fun SignUp(
    viewModel: IUsersViewModel,
    navController: NavController
) {
    val state by viewModel.state.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    state?.let { currentState ->

        when (uiState) {
            is UIState.GeneralError -> {
                ErrorModal(onRetry = {
                    CoroutineScope(Dispatchers.Main).launch {
                        val result = viewModel.login(currentState)
                        if (result.isSuccess) {
                            navController.navigate(ScreenRoutes.HOME)
                        }
                    }
                })
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .testTag(CommonTestTags.USERS_SCREEN),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Kiwi_H1(Kiwi_TextArguments(
                        "Initial Setup Will Take\nApproximately 3 Minutes",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.secondary
                    ))

                    Kiwi_Spacer(Spacing.xLarge)

                    Kiwi_H1(Kiwi_TextArguments(
                        "Let's Start With\nThe Basics",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.secondary
                    ))

                    Kiwi_Spacer()

                    Kiwi_InputField(
                        enabled = !isLoading,
                        value = currentState.email,


                        onValueChange = { viewModel.onEmailChanged(it) },
                        label = {
                            Kiwi_P1(
                                Kiwi_TextArguments(
                                    "Email",
                                    color = MaterialTheme.colorScheme.inversePrimary
                                )
                            )
                        },
                        shouldHideInput = false,
                        textColor = MaterialTheme.colorScheme.inversePrimary,
                        testTag = UsersTestTags.EMAIL_FIELD
                    )

                    Kiwi_Spacer()

                    Kiwi_InputField(
                        enabled = !isLoading,
                        value = currentState.email,


                        onValueChange = { viewModel.onEmailChanged(it) },
                        label = {
                            Kiwi_P1(
                                Kiwi_TextArguments(
                                    "Email",
                                    color = MaterialTheme.colorScheme.inversePrimary
                                )
                            )
                        },
                        shouldHideInput = false,
                        textColor = MaterialTheme.colorScheme.inversePrimary,
                        testTag = UsersTestTags.EMAIL_FIELD
                    )

                    Kiwi_Spacer()

                    Kiwi_InputField(
                        enabled = !isLoading,
                        value = currentState.email,


                        onValueChange = { viewModel.onEmailChanged(it) },
                        label = {
                            Kiwi_P1(
                                Kiwi_TextArguments(
                                    "Email",
                                    color = MaterialTheme.colorScheme.inversePrimary
                                )
                            )
                        },
                        shouldHideInput = false,
                        textColor = MaterialTheme.colorScheme.inversePrimary,
                        testTag = UsersTestTags.EMAIL_FIELD
                    )

                    Kiwi_Spacer()

                    Kiwi_InputField(
                        enabled = !isLoading,
                        value = currentState.email,


                        onValueChange = { viewModel.onEmailChanged(it) },
                        label = {
                            Kiwi_P1(
                                Kiwi_TextArguments(
                                    "Email",
                                    color = MaterialTheme.colorScheme.inversePrimary
                                )
                            )
                        },
                        shouldHideInput = false,
                        textColor = MaterialTheme.colorScheme.inversePrimary,
                        testTag = UsersTestTags.EMAIL_FIELD
                    )

                    Kiwi_Spacer()

                    when (uiState) {
                        is UIState.Error -> {
                            Kiwi_Spacer()
                            Kiwi_InfoBox(
                                message = (uiState as UIState.Error).message,
                                color = MaterialTheme.colorScheme.error,
                                testTag = UsersTestTags.ERROR_TEXT
                            )
                        }

                        else -> {}
                    }

                    Kiwi_Button(
                        Kiwi_TextArguments(
                            "START JOURNEY",
                            color = MaterialTheme.colorScheme.secondary,
                            bold = true
                        ),
                        color = MaterialTheme.colorScheme.tertiary,
                        onClick = {
                            CoroutineScope(Dispatchers.Main).launch {
                                if (viewModel.login(currentState).isSuccess) {
                                    navController.navigate(ScreenRoutes.SIGNUP_TEST)
                                }
                            }
                        },
                        enabled = !isLoading,
                        testTag = UsersTestTags.LOGIN_BUTTON,
                    )
                }
            }
        }
    }
}

@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Composable
fun SignUpScreenPreview() {
    KiwiTheme {
        SignUpScreen(
            UsersFakeViewModel(
                UsersState("finn@thehuman.com", "Math3matical!"),
            ),
            navController = rememberNavController()
        )
    }
}
