package com.bellako.kiwi.features.users.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.common.tests.CommonTestTags
import com.bellako.kiwi.common.screens.modals.ErrorModal
import com.bellako.kiwi.common.data.UIState
import com.bellako.kiwi.common.screens.components.Kiwi_InfoBox
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.ui.KiwiTheme
import com.bellako.kiwi.ui.Spacing
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.runtime.Composable
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import com.bellako.kiwi.features.personality.model.IPersonalityViewModel
import com.bellako.kiwi.features.personality.tests.PersonalityFakeViewModel
import com.bellako.kiwi.features.personality.data.PersonalityState
import com.bellako.kiwi.features.personality.tests.PersonalityTestFactory.validPersonalityDTO
import com.bellako.kiwi.features.users.model.IUsersViewModel
import com.bellako.kiwi.features.users.tests.UsersFakeViewModel
import com.bellako.kiwi.features.users.data.UsersState
import com.bellako.kiwi.features.users.tests.UsersTestTags
import com.bellako.kiwi.analytics.FirebaseEventLogger
import com.bellako.kiwi.analytics.FirebaseEventNames
import com.bellako.kiwi.common.screens.ScreenRoutes
import com.bellako.kiwi.common.screens.components.Kiwi_Button
import com.bellako.kiwi.common.screens.components.KiwiH2
import com.bellako.kiwi.common.screens.components.Kiwi_InputField
import com.bellako.kiwi.common.screens.components.KiwiLabel2
import com.bellako.kiwi.common.screens.components.KiwiP2


@Composable
fun SignUpScreen2_Form(
    usersViewModel: IUsersViewModel,
    personalityViewModel: IPersonalityViewModel,
    navController: NavController
) {
    SignUpScreen() {
        SignUp(
            usersViewModel,
            personalityViewModel,
            navController
        )
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
                                navController.navigate(ScreenRoutes.SIGNUP3_TEST)
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

                        KiwiP2(KiwiTextArguments(
                            "Initial Setup Will Take\nApproximately 3 Minutes",
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.secondary
                        ))

                        Kiwi_Spacer(Spacing.large)
                        Kiwi_Spacer(Spacing.large)

                        KiwiH2(KiwiTextArguments(
                            "Let's Start With\nThe Basics",
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.secondary,
                            bold = true
                        ))

                        Kiwi_Spacer(Spacing.large)


                        // INPUT

                        Kiwi_InputField(
                            enabled = !usersIsLoading,
                            value = currentPersonalityState.realName,
                            onValueChange = { personalityViewModel.onRealNameChanged(it) },
                            label = {
                                KiwiLabel2(KiwiTextArguments(
                                    "Real Name",
                                    color = MaterialTheme.colorScheme.inversePrimary
                                ))
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
                                KiwiLabel2(KiwiTextArguments(
                                    "Knight Name",
                                    color = MaterialTheme.colorScheme.inversePrimary
                                ))
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
                                KiwiLabel2(KiwiTextArguments(
                                    "Email",
                                    color = MaterialTheme.colorScheme.inversePrimary
                                ))
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
                                KiwiLabel2(KiwiTextArguments(
                                    "Password",
                                    color = MaterialTheme.colorScheme.inversePrimary
                                ))
                            },
                            shouldHideInput = true,
                            textColor = MaterialTheme.colorScheme.inversePrimary,
                            testTag = UsersTestTags.PASSWORD_FIELD
                        )

                        Kiwi_Spacer(Spacing.large)
                        Kiwi_Spacer(Spacing.large)


                        // BUTTON

                        Kiwi_Button(
                            KiwiTextArguments(
                                "START JOURNEY",
                                color = MaterialTheme.colorScheme.secondary,
                                bold = true
                            ),
                            color = MaterialTheme.colorScheme.tertiary,
                            onClick = {
                                CoroutineScope(Dispatchers.Main).launch {
                                    if (personalityViewModel.checkValid().isSuccess && usersViewModel.signup(context).isSuccess) {
                                        FirebaseEventLogger.logEvent(FirebaseEventNames.ONBOARDING_COMPLETED)

                                        personalityViewModel.updateRealName()
                                        personalityViewModel.updateKnightName()
                                        navController.navigate(ScreenRoutes.SIGNUP3_TEST)
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

// -------------------------------------------------------------------------------------------------

@SuppressLint("ViewModelConstructorInComposable")
@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Composable
fun SignUpScreen2_FormPreview() {
    KiwiTheme {
        SignUpScreen2_Form(
            UsersFakeViewModel(UsersState("finn@thehuman.com", "Math3matical!")),
            personalityViewModel = PersonalityFakeViewModel(PersonalityState(
                validPersonalityDTO().realName,
                validPersonalityDTO().knightName,
                validPersonalityDTO().build,
                validPersonalityDTO().goodApps,
                validPersonalityDTO().badApps,
            )),
            navController = rememberNavController()
        )
    }
}
