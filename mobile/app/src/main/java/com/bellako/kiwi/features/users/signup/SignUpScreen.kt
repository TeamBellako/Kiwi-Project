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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import com.bellako.kiwi.features.personality.IPersonalityViewModel
import com.bellako.kiwi.features.personality.PersonalityFakeViewModel
import com.bellako.kiwi.features.personality.PersonalityState
import com.bellako.kiwi.features.personality.PersonalityTestFactory.validPersonalityDTO
import com.bellako.kiwi.features.users.IUsersViewModel
import com.bellako.kiwi.features.users.UsersFakeViewModel
import com.bellako.kiwi.features.users.UsersState
import com.bellako.kiwi.features.users.UsersTestTags
import com.bellako.kiwi.ui.components.Kiwi_Button
import com.bellako.kiwi.ui.components.Kiwi_H2
import com.bellako.kiwi.ui.components.Kiwi_InputField
import com.bellako.kiwi.ui.components.Kiwi_P1
import com.bellako.kiwi.ui.theme.getResponsiveRelativeSize


@Composable
fun SignUpScreen(
    usersViewModel: IUsersViewModel,
    personalityViewModel: IPersonalityViewModel,
    navController: NavController
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
                .padding(getResponsiveRelativeSize(Spacing.medium)),
            contentAlignment = Alignment.Center
        ) {
            SignUp(
                usersViewModel,
                personalityViewModel,
                navController
            )
        }
    }
}

@Composable
private fun SignUp(
    usersViewModel: IUsersViewModel,
    personalityViewModel: IPersonalityViewModel,
    navController: NavController
) {
    val context = LocalContext.current

    val usersState by usersViewModel.state.collectAsState()
    val usersUiState by usersViewModel.uiState.collectAsState()
    val usersIsLoading by usersViewModel.isLoading.collectAsState()

    val personalityState by personalityViewModel.state.collectAsState()
    val personalityUiState by personalityViewModel.uiState.collectAsState()

    usersState?.let { currentUsersState ->
        personalityState?.let { currentPersonalityState ->

            when (usersUiState) {
                is UIState.GeneralError -> {
                    ErrorModal(onRetry = {
                        CoroutineScope(Dispatchers.Main).launch {
                            if (personalityViewModel.checkValid().isSuccess && usersViewModel.signup(context).isSuccess) {
                                personalityViewModel.updateRealName()
                                personalityViewModel.updateKnightName()
                                navController.navigate(ScreenRoutes.SIGNUP_TEST)
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

                        // TEXT WELCOME

                        Kiwi_P1(
                            Kiwi_TextArguments(
                                "Initial Setup Will Take\nApproximately 3 Minutes",
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        )

                        Kiwi_Spacer()

                        Kiwi_H2(
                            Kiwi_TextArguments(
                                "Let's Start With\nThe Basics",
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.secondary,
                                bold = true
                            )
                        )

                        Kiwi_Spacer()


                        // INPUT

                        Kiwi_InputField(
                            enabled = !usersIsLoading,
                            value = currentPersonalityState.realName,
                            onValueChange = { personalityViewModel.onRealNameChanged(it) },
                            label = {
                                Kiwi_P1(
                                    Kiwi_TextArguments(
                                        "Real Name",
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
                            enabled = !usersIsLoading,
                            value = currentPersonalityState.knightName,
                            onValueChange = { personalityViewModel.onKnightNameChanged(it) },
                            label = {
                                Kiwi_P1(
                                    Kiwi_TextArguments(
                                        "Knight Name",
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
                            enabled = !usersIsLoading,
                            value = currentUsersState.email,
                            onValueChange = { usersViewModel.onEmailChanged(it) },
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
                            enabled = !usersIsLoading,
                            value = currentUsersState.password,
                            onValueChange = { usersViewModel.onPasswordChanged(it) },
                            label = {
                                Kiwi_P1(
                                    Kiwi_TextArguments(
                                        "Password",
                                        color = MaterialTheme.colorScheme.inversePrimary
                                    )
                                )
                            },
                            shouldHideInput = true,
                            textColor = MaterialTheme.colorScheme.inversePrimary,
                            testTag = UsersTestTags.PASSWORD_FIELD
                        )

                        Kiwi_Spacer()


                        // BUTTON

                        Kiwi_Button(
                            Kiwi_TextArguments(
                                "START JOURNEY",
                                color = MaterialTheme.colorScheme.secondary,
                                bold = true
                            ),
                            color = MaterialTheme.colorScheme.tertiary,
                            onClick = {
                                CoroutineScope(Dispatchers.Main).launch {
                                    if (personalityViewModel.checkValid().isSuccess && usersViewModel.signup(context).isSuccess) {
                                        personalityViewModel.updateRealName()
                                        personalityViewModel.updateKnightName()
                                        navController.navigate(ScreenRoutes.SIGNUP_TEST)
                                    }
                                }
                            },
                            enabled = !usersIsLoading,
                            testTag = UsersTestTags.SIGNUP_BUTTON,
                        )

                        Kiwi_Spacer()


                        // SIGNUP ERROR MESSAGE

                        var errorMessage by remember { mutableStateOf("") }
                        errorMessage = when (usersUiState) {
                            is UIState.Error -> { (usersUiState as UIState.Error).message }
                            else -> {
                                when (personalityUiState) {
                                    is UIState.Error -> { (personalityUiState as UIState.Error).message }
                                    else -> { "" }
                                }
                            }
                        }

                        Box(
                            modifier = Modifier
                                .alpha(if (errorMessage.isEmpty()) 0f else 1f)
                        ) {
                            Kiwi_InfoBox(
                                message = errorMessage,
                                color = MaterialTheme.colorScheme.error,
                                testTag = UsersTestTags.ERROR_TEXT
                            )
                        }

                    }
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
            PersonalityFakeViewModel(
                PersonalityState(validPersonalityDTO().realName, validPersonalityDTO().knightName, validPersonalityDTO().build),
            ),
            navController = rememberNavController()
        )
    }
}
