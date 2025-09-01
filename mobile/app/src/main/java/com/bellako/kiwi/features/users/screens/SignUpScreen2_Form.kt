package com.bellako.kiwi.features.users.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.analytics.FirebaseEventLogger
import com.bellako.kiwi.analytics.FirebaseEventNames
import com.bellako.kiwi.common.data.ScreenRoutes
import com.bellako.kiwi.common.data.UIState
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_Button
import com.bellako.kiwi.common.screens.components.Kiwi_H2
import com.bellako.kiwi.common.screens.components.Kiwi_InfoBox
import com.bellako.kiwi.common.screens.components.Kiwi_InputField
import com.bellako.kiwi.common.screens.components.Kiwi_Label2
import com.bellako.kiwi.common.screens.components.Kiwi_P2
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer
import com.bellako.kiwi.common.screens.components.LoadingModal
import com.bellako.kiwi.common.screens.modals.ErrorModal
import com.bellako.kiwi.common.tests.CommonTestTags
import com.bellako.kiwi.features.personality.data.PersonalityState
import com.bellako.kiwi.features.personality.model.IPersonalityViewModel
import com.bellako.kiwi.features.personality.tests.PersonalityFakeViewModel
import com.bellako.kiwi.features.personality.tests.PersonalityTestFactory.validPersonalityDTO
import com.bellako.kiwi.features.users.data.UsersState
import com.bellako.kiwi.features.users.model.IUsersViewModel
import com.bellako.kiwi.features.users.tests.UsersFakeViewModel
import com.bellako.kiwi.features.users.tests.UsersTestFactory.validUsersDTO
import com.bellako.kiwi.features.users.tests.UsersTestTags
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SignUpScreen2_Form(
    usersViewModel: IUsersViewModel,
    personalityViewModel: IPersonalityViewModel,
    navController: NavController,
) {
    SignUpScreen {
        SignUp(
            usersViewModel,
            personalityViewModel,
            navController,
        )
    }
}

@Composable
private fun SignUp(
    usersViewModel: IUsersViewModel,
    personalityViewModel: IPersonalityViewModel,
    navController: NavController,
) {
    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current

    val usersState by usersViewModel.state.collectAsState()
    val usersUiState by usersViewModel.uiState.collectAsState()
    val usersIsLoading by usersViewModel.isLoading.collectAsState()

    val personalityState by personalityViewModel.state.collectAsState()
    val personalityUiState by personalityViewModel.uiState.collectAsState()
    val personalityIsLoading by personalityViewModel.isLoading.collectAsState()

    var localLoading by remember { mutableStateOf(false) }

    val isLoading by remember { derivedStateOf { localLoading || usersIsLoading || personalityIsLoading } }

    usersState?.let { currentUsersState ->
        personalityState?.let { currentPersonalityState ->

            if (usersUiState == UIState.GeneralError || personalityUiState == UIState.GeneralError) {
                ErrorModal(onButtonClick = {
                    usersViewModel.resetUiState()
                    personalityViewModel.resetUiState()
                })
            } else {
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
                    // TEXT WELCOME

                    Kiwi_P2(
                        KiwiTextArguments(
                            "Initial Setup Will Take\nApproximately 3 Minutes",
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.secondary,
                        ),
                    )

                    Kiwi_Spacer(Spacing.large)
                    Kiwi_Spacer(Spacing.large)

                    Kiwi_H2(
                        KiwiTextArguments(
                            "Let's Start With\nThe Basics",
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.secondary,
                            bold = true,
                        ),
                    )

                    Kiwi_Spacer(Spacing.large)

                    // INPUT

                    Kiwi_InputField(
                        enabled = !isLoading,
                        value = currentPersonalityState.realName,
                        onValueChange = { personalityViewModel.onRealNameChanged(it) },
                        label = {
                            Kiwi_Label2(
                                KiwiTextArguments(
                                    "Real Name",
                                    color = MaterialTheme.colorScheme.inversePrimary,
                                ),
                            )
                        },
                        shouldHideInput = false,
                        textColor = MaterialTheme.colorScheme.inversePrimary,
                        testTag = UsersTestTags.EMAIL_FIELD,
                    )

                    Kiwi_Spacer()

                    Kiwi_InputField(
                        enabled = !isLoading,
                        value = currentPersonalityState.knightName,
                        onValueChange = { personalityViewModel.onKnightNameChanged(it) },
                        label = {
                            Kiwi_Label2(
                                KiwiTextArguments(
                                    "Knight Name",
                                    color = MaterialTheme.colorScheme.inversePrimary,
                                ),
                            )
                        },
                        shouldHideInput = false,
                        textColor = MaterialTheme.colorScheme.inversePrimary,
                        testTag = UsersTestTags.EMAIL_FIELD,
                    )

                    Kiwi_Spacer()

                    Kiwi_InputField(
                        enabled = !isLoading,
                        value = currentUsersState.email,
                        onValueChange = { usersViewModel.onEmailChanged(it) },
                        label = {
                            Kiwi_Label2(
                                KiwiTextArguments(
                                    "Email",
                                    color = MaterialTheme.colorScheme.inversePrimary,
                                ),
                            )
                        },
                        shouldHideInput = false,
                        textColor = MaterialTheme.colorScheme.inversePrimary,
                        testTag = UsersTestTags.EMAIL_FIELD,
                    )

                    Kiwi_Spacer()

                    Kiwi_InputField(
                        enabled = !isLoading,
                        value = currentUsersState.password,
                        onValueChange = { usersViewModel.onPasswordChanged(it) },
                        label = {
                            Kiwi_Label2(
                                KiwiTextArguments(
                                    "Password",
                                    color = MaterialTheme.colorScheme.inversePrimary,
                                ),
                            )
                        },
                        shouldHideInput = true,
                        textColor = MaterialTheme.colorScheme.inversePrimary,
                        testTag = UsersTestTags.PASSWORD_FIELD,
                    )

                    Kiwi_Spacer(Spacing.xLarge)

                    // BUTTON

                    Kiwi_Button(
                        textArguments =
                            KiwiTextArguments(
                                "START JOURNEY",
                                color = MaterialTheme.colorScheme.secondary,
                                bold = true,
                            ),
                        color = MaterialTheme.colorScheme.tertiary,
                        onClick = {
                            CoroutineScope(Dispatchers.Main).launch {
                                if (personalityViewModel.checkRealNameValid() && personalityViewModel.checkKnightNameValid()) {
                                    personalityViewModel.resetUiState()

                                    if (usersViewModel.checkEmailValid()) {
                                        if (usersViewModel.checkPasswordValid()) {
                                            usersViewModel.resetUiState()

                                            if (usersViewModel.signup(context).isSuccess) {
                                                if (personalityViewModel.updateRealName().isSuccess &&
                                                    personalityViewModel.updateKnightName().isSuccess
                                                ) {
                                                    FirebaseEventLogger.logEvent(FirebaseEventNames.SIGNUP_2_FORM_COMPLETED)

                                                    navController.navigate(ScreenRoutes.SIGNUP3_TEST)
                                                    localLoading = true
                                                }
                                            }
                                        } else {
                                            FirebaseEventLogger.logEvent(FirebaseEventNames.SIGNUP_2_FORM_INVALID_PASSWORD)
                                        }
                                    }
                                }
                            }
                        },
                        enabled = !isLoading,
                        testTag = UsersTestTags.SIGNUP_BUTTON,
                    )

                    Kiwi_Spacer()

                    // SIGNUP ERROR MESSAGE

                    var errorMessage by remember { mutableStateOf("") }
                    errorMessage =
                        when (usersUiState) {
                            is UIState.Error -> {
                                (usersUiState as UIState.Error).message
                            }

                            else -> {
                                when (personalityUiState) {
                                    is UIState.Error -> {
                                        (personalityUiState as UIState.Error).message
                                    }

                                    else -> {
                                        ""
                                    }
                                }
                            }
                        }

                    Box(
                        modifier =
                            Modifier
                                .alpha(if (errorMessage.isEmpty()) 0f else 1f),
                    ) {
                        Kiwi_InfoBox(
                            message = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            testTag = UsersTestTags.ERROR_TEXT,
                        )
                    }
                }

                if (isLoading || isPreview) {
                    LoadingModal()
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
fun SignUpScreen2_Form_Preview() {
    Kiwi_Theme {
        SignUpScreen2_Form(
            UsersFakeViewModel(UsersState(validUsersDTO().email, validUsersDTO().password, validUsersDTO().registerDate)),
            personalityViewModel =
                PersonalityFakeViewModel(
                    PersonalityState(
                        validPersonalityDTO().realName,
                        validPersonalityDTO().knightName,
                        validPersonalityDTO().build,
                        validPersonalityDTO().goodApps,
                        validPersonalityDTO().badApps,
                    ),
                ),
            navController = rememberNavController(),
        )
    }
}
