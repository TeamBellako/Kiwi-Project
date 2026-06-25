package com.bellako.kiwi.features.users.screens

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.analytics.FirebaseEventNames
import com.bellako.kiwi.analytics.firebaseLogEvent
import com.bellako.kiwi.common.data.ScreenRoutes
import com.bellako.kiwi.common.data.UIState
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_FixedSizeButton
import com.bellako.kiwi.common.screens.components.Kiwi_H2
import com.bellako.kiwi.common.screens.components.Kiwi_InfoBox
import com.bellako.kiwi.common.screens.components.Kiwi_InputField
import com.bellako.kiwi.common.screens.components.Kiwi_Label2
import com.bellako.kiwi.common.screens.components.Kiwi_P2
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer
import com.bellako.kiwi.common.screens.components.LoadingModal
import com.bellako.kiwi.common.screens.modals.ErrorModalScreen
import com.bellako.kiwi.common.tests.CommonTestTags
import com.bellako.kiwi.features.personality.data.PersonalityState
import com.bellako.kiwi.features.personality.model.IPersonalityViewModel
import com.bellako.kiwi.features.personality.tests.PersonalityFakeViewModel
import com.bellako.kiwi.features.nodes.screens.LocalNodeEntryTransition
import com.bellako.kiwi.features.personality.tests.PersonalityTestFactory.validPersonalityDTO
import com.bellako.kiwi.features.users.data.Password
import com.bellako.kiwi.features.users.data.UsersState
import com.bellako.kiwi.features.users.model.IUsersViewModel
import com.bellako.kiwi.features.users.tests.UsersFakeViewModel
import com.bellako.kiwi.features.users.tests.UsersTestFactory.validUsersDTO
import com.bellako.kiwi.features.users.tests.UsersTestTags
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.LocalKiwiColors
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
        SignUpFormLayoutContainer(
            usersViewModel,
            personalityViewModel,
            navController,
        )

        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(bottom = getResponsiveSizeHeight(Spacing.large)),
            contentAlignment = Alignment.BottomCenter,
        ) {
            GoToLogIn {
                navController.navigate(ScreenRoutes.LOGIN)
            }
        }
    }
}

@Composable
private fun SignUpFormLayoutContainer(
    usersViewModel: IUsersViewModel,
    personalityViewModel: IPersonalityViewModel,
    navController: NavController,
) {
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
                ErrorModalScreen(onButtonClick = {
                    usersViewModel.resetUiState()
                    personalityViewModel.resetUiState()
                })
            } else {
                SignUpFormLayout(
                    isLoading = isLoading,
                    usersViewModel = usersViewModel,
                    usersState = currentUsersState,
                    usersUiState = usersUiState,
                    personalityViewModel = personalityViewModel,
                    personalityState = currentPersonalityState,
                    personalityUiState = personalityUiState,
                    navController = navController,
                    onSignUpSuccess = {
                        localLoading = true
                    },
                )
            }
        }
    }
}

@Composable
private fun SignUpFormLayout(
    isLoading: Boolean,
    usersViewModel: IUsersViewModel,
    usersState: UsersState,
    usersUiState: UIState<Unit>,
    personalityViewModel: IPersonalityViewModel,
    personalityState: PersonalityState,
    personalityUiState: UIState<Unit>,
    navController: NavController,
    onSignUpSuccess: (() -> Unit),
) {
    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current
    val kiwiColors = LocalKiwiColors.current

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
        Kiwi_P2(
            KiwiTextArguments(
                "Initial Setup Will Take\nApproximately 3 Minutes",
                textAlign = TextAlign.Center,
                color = kiwiColors.color6,
            ),
        )

        Kiwi_Spacer(Spacing.large)
        Kiwi_Spacer(Spacing.large)

        Kiwi_H2(
            KiwiTextArguments(
                "Let's Start With\nThe Basics",
                textAlign = TextAlign.Center,
                color = kiwiColors.color6,
                fontWeight = FontWeight.Bold,
            ),
        )

        Kiwi_Spacer(Spacing.large)

        SignUpForm(
            context = context,
            isLoading = isLoading,
            usersViewModel = usersViewModel,
            usersState = usersState,
            personalityViewModel = personalityViewModel,
            personalityState = personalityState,
            navController = navController,
            onSignUpSuccess = onSignUpSuccess,
        )

        Kiwi_Spacer()

        SignUpErrorMessage(
            usersUiState = usersUiState,
            personalityUiState = personalityUiState,
        )
    }

    if (isLoading || isPreview) {
        LoadingModal()
    }
}

@Composable
private fun SignUpForm(
    context: Context,
    isLoading: Boolean,
    usersViewModel: IUsersViewModel,
    usersState: UsersState,
    personalityViewModel: IPersonalityViewModel,
    personalityState: PersonalityState,
    navController: NavController,
    onSignUpSuccess: (() -> Unit),
) {
    val kiwiColors = LocalKiwiColors.current

    // Live typing feedback surfaces password problems on its own; this flag
    // additionally reveals them after a submit attempt so an empty password
    // (nothing typed yet) is still explained instead of silently blocking.
    var attemptedSubmit by remember { mutableStateOf(false) }

    // Veil the step change into the questionnaire. rememberCoroutineScope (not
    // the detached CoroutineScope used for the async signup below) so the veil
    // Animatable has a frame clock.
    val nodeEntry = LocalNodeEntryTransition.current
    val veilScope = rememberCoroutineScope()

    SignUpForm_Personality(
        isLoading = isLoading,
        personalityViewModel = personalityViewModel,
        personalityState = personalityState,
    )

    Kiwi_Spacer()

    SignUpForm_Users(
        isLoading = isLoading,
        usersViewModel = usersViewModel,
        usersState = usersState,
        showErrorsWhenEmpty = attemptedSubmit,
    )

    Kiwi_Spacer(Spacing.xLarge)

    Kiwi_FixedSizeButton(
        textArguments =
            KiwiTextArguments(
                "START JOURNEY",
                color = kiwiColors.colorF,
                fontWeight = FontWeight.Bold,
            ),
        color = kiwiColors.color8,
        onClick = {
            attemptedSubmit = true
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
                                    firebaseLogEvent(FirebaseEventNames.SIGNUP_2_FORM_COMPLETED)

                                    signupVeilNavigate(nodeEntry, veilScope, navController, ScreenRoutes.SIGNUP3_TEST)
                                    onSignUpSuccess()
                                }
                            }
                        } else {
                            firebaseLogEvent(FirebaseEventNames.SIGNUP_2_FORM_INVALID_PASSWORD)
                        }
                    }
                }
            }
        },
        enabled = !isLoading,
        testTag = UsersTestTags.SIGNUP_BUTTON,
    )
}

@Composable
private fun SignUpForm_Personality(
    isLoading: Boolean,
    personalityViewModel: IPersonalityViewModel,
    personalityState: PersonalityState,
) {
    val kiwiColors = LocalKiwiColors.current

    Kiwi_InputField(
        enabled = !isLoading,
        value = personalityState.realName,
        onValueChange = { personalityViewModel.onRealNameChanged(it) },
        label = {
            Kiwi_Label2(
                KiwiTextArguments(
                    "Real Name",
                    color = kiwiColors.color7B,
                ),
            )
        },
        textColor = kiwiColors.color7B,
        color = kiwiColors.color3A,
        testTag = UsersTestTags.EMAIL_FIELD,
    )

    Kiwi_Spacer()

    Kiwi_InputField(
        enabled = !isLoading,
        value = personalityState.knightName,
        onValueChange = { personalityViewModel.onKnightNameChanged(it) },
        label = {
            Kiwi_Label2(
                KiwiTextArguments(
                    "Knight Name",
                    color = kiwiColors.color7B,
                ),
            )
        },
        textColor = kiwiColors.color7B,
        color = kiwiColors.color3A,
        testTag = UsersTestTags.EMAIL_FIELD,
    )
}

@Composable
private fun SignUpForm_Users(
    isLoading: Boolean,
    usersViewModel: IUsersViewModel,
    usersState: UsersState,
    showErrorsWhenEmpty: Boolean,
) {
    val kiwiColors = LocalKiwiColors.current

    // Explain why a password is rejected: the rules it fails, shown live while
    // the user types and — once a submit has been attempted — even for an empty
    // field. A valid password shows nothing.
    val unmetRequirements = Password.unmetRequirements(usersState.password)
    val showPasswordErrors =
        unmetRequirements.isNotEmpty() && (usersState.password.isNotEmpty() || showErrorsWhenEmpty)

    Kiwi_InputField(
        enabled = !isLoading,
        value = usersState.email,
        onValueChange = { usersViewModel.onEmailChanged(it) },
        label = {
            Kiwi_Label2(
                KiwiTextArguments(
                    "Email",
                    color = kiwiColors.color7B,
                ),
            )
        },
        textColor = kiwiColors.color7B,
        color = kiwiColors.color3A,
        testTag = UsersTestTags.EMAIL_FIELD,
    )

    Kiwi_Spacer()

    Kiwi_InputField(
        enabled = !isLoading,
        value = usersState.password,
        onValueChange = { usersViewModel.onPasswordChanged(it) },
        label = {
            Kiwi_Label2(
                KiwiTextArguments(
                    "Password",
                    color = kiwiColors.color7B,
                ),
            )
        },
        keyboardType = KeyboardType.Password,
        textColor = kiwiColors.color7B,
        color = kiwiColors.color3A,
        testTag = UsersTestTags.PASSWORD_FIELD,
    )

    if (showPasswordErrors) {
        PasswordValidationErrors(unmetRequirements)
    }
}

@Suppress("MagicNumber")
@Composable
private fun PasswordValidationErrors(unmetRequirements: List<String>) {
    val kiwiColors = LocalKiwiColors.current

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(top = getResponsiveSizeHeight(Spacing.xSmall))
                .testTag(UsersTestTags.PASSWORD_ERROR),
        verticalArrangement = Arrangement.spacedBy(getResponsiveSizeHeight(Spacing.xSmall)),
    ) {
        unmetRequirements.forEach { requirement ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(getResponsiveSizeHeight(Spacing.small)),
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(getResponsiveSizeHeight(6.dp))
                            .background(color = kiwiColors.colorR, shape = CircleShape),
                )
                Kiwi_Label2(
                    KiwiTextArguments(
                        requirement,
                        color = kiwiColors.colorR,
                    ),
                )
            }
        }
    }
}

@Composable
private fun SignUpErrorMessage(
    usersUiState: UIState<Unit>,
    personalityUiState: UIState<Unit>,
) {
    val kiwiColors = LocalKiwiColors.current
    var errorMessage by remember { mutableStateOf("") }
    errorMessage =
        when (usersUiState) {
            is UIState.Error -> {
                usersUiState.message
            }
            else -> {
                when (personalityUiState) {
                    is UIState.Error -> {
                        personalityUiState.message
                    }
                    else -> {
                        ""
                    }
                }
            }
        }

    Box(modifier = Modifier.alpha(if (errorMessage.isEmpty()) 0f else 1f)) {
        Kiwi_InfoBox(
            message = errorMessage,
            color = kiwiColors.colorR,
            testTag = UsersTestTags.ERROR_TEXT,
        )
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
            usersViewModel =
                UsersFakeViewModel(
                    UsersState(
                        validUsersDTO().email,
                        validUsersDTO().password,
                        validUsersDTO().registerDate,
                    ),
                ),
            personalityViewModel =
                PersonalityFakeViewModel(
                    PersonalityState(
                        validPersonalityDTO().realName,
                        validPersonalityDTO().knightName,
                        validPersonalityDTO().build,
                        validPersonalityDTO().goodApps,
                        validPersonalityDTO().badApps,
                        validPersonalityDTO().neutralApps,
                    ),
                ),
            navController = rememberNavController(),
        )
    }
}
