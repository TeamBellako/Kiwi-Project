package com.bellako.kiwi.features.users.screens

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.R
import com.bellako.kiwi.common.data.ScreenRoutes
import com.bellako.kiwi.common.data.UIState
import com.bellako.kiwi.common.screens.components.KiwiAnnotatedStringArguments
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_AnnotatedString_P1
import com.bellako.kiwi.common.screens.components.Kiwi_FixedSizeButton
import com.bellako.kiwi.common.screens.components.Kiwi_H1
import com.bellako.kiwi.common.screens.components.Kiwi_Image
import com.bellako.kiwi.common.screens.components.Kiwi_InfoBox
import com.bellako.kiwi.common.screens.components.Kiwi_InputField
import com.bellako.kiwi.common.screens.components.Kiwi_Label2
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer
import com.bellako.kiwi.common.screens.LOGIN_LOADING_ANIM_DURATION_MS
import com.bellako.kiwi.common.screens.components.LoadingModal
import com.bellako.kiwi.common.screens.modals.ErrorModalScreen
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
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LogInScreen(
    usersViewModel: IUsersViewModel,
    personalityViewModel: IPersonalityViewModel,
    navController: NavController,
) {
    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current
    val kiwiColors = LocalKiwiColors.current

    val usersState by usersViewModel.state.collectAsState()
    val usersUiState by usersViewModel.uiState.collectAsState()

    @Suppress("MagicNumber")
    val imgPercentage = 0.46f

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(kiwiColors.color2),
        contentAlignment = Alignment.Center,
    ) {
        when (usersUiState) {
            is UIState.GeneralError -> {
                ErrorModalScreen(onButtonClick = {
                    CoroutineScope(Dispatchers.Main).launch {
                        usersViewModel.clearLocalCredentials(context)
                        usersViewModel.resetUiState()
                    }
                })
            }
            else -> {
                Kiwi_Image(
                    R.drawable.login_bg,
                    "Login Background",
                    modifier =
                        Modifier
                            .fillMaxHeight(imgPercentage)
                            .align(Alignment.TopStart),
                    contentScale = ContentScale.FillHeight,
                    alignment = Alignment.TopStart,
                )

                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(imgPercentage)
                            .align(Alignment.TopStart)
                            .background(
                                Brush.verticalGradient(
                                    colors =
                                        listOf(
                                            Color.Transparent,
                                            Color.Transparent,
                                            kiwiColors.color2,
                                            kiwiColors.color2,
                                        ),
                                    startY = 0f,
                                    endY = Float.POSITIVE_INFINITY,
                                ),
                            ),
                )

                LogInLayout(
                    context = context,
                    isPreview = isPreview,
                    usersViewModel = usersViewModel,
                    usersState = usersState,
                    usersUiState = usersUiState,
                    personalityViewModel = personalityViewModel,
                    navController = navController,
                )
            }
        }
    }
}

@Composable
private fun LogInLayout(
    context: Context,
    isPreview: Boolean,
    usersViewModel: IUsersViewModel,
    usersState: UsersState?,
    usersUiState: UIState<Unit>,
    personalityViewModel: IPersonalityViewModel,
    navController: NavController,
) {
    val usersIsLoading by usersViewModel.isLoading.collectAsState()
    val personalityIsLoading by personalityViewModel.isLoading.collectAsState()
    var initializing by remember { mutableStateOf(true) }
    var localLoading by remember { mutableStateOf(false) }
    val isLoading by remember { derivedStateOf { initializing || localLoading || usersIsLoading || personalityIsLoading } }

    // check stored credentials for auto login
    LaunchedEffect(Unit) {
        initializing = false
        val (username, password) = usersViewModel.getLocalCredentials(context)
        if (!username.isNullOrBlank() && !password.isNullOrBlank()) {
            usersViewModel.onEmailChanged(username)
            usersViewModel.onPasswordChanged(password)
            usersViewModel.markInitialAuthCheckPerformed()
            localLoading = performLogin(context, usersViewModel, personalityViewModel, navController)
        } else {
            usersViewModel.markInitialAuthCheckPerformed()
        }
    }

    usersState?.let { currentState ->

        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(getResponsiveSizeHeight(Spacing.large)),
            contentAlignment = Alignment.CenterStart,
        ) {
            if (isLoading || isPreview) {
                LoadingModal()
            }

            LogInForm(
                context = context,
                isLoading = isLoading,
                isPreview = isPreview,
                usersViewModel = usersViewModel,
                usersState = usersState,
                usersUiState = usersUiState,
                personalityViewModel = personalityViewModel,
                navController = navController,
                onLoginSuccess = {
                    localLoading = true
                },
            )
        }

        GoToSignUp(
            isLoading = isLoading,
            isPreview = isPreview,
            navController = navController,
        )
    }
}

@Composable
private fun LogInForm(
    context: Context,
    isLoading: Boolean,
    isPreview: Boolean,
    usersViewModel: IUsersViewModel,
    usersState: UsersState,
    usersUiState: UIState<Unit>,
    personalityViewModel: IPersonalityViewModel,
    navController: NavController,
    onLoginSuccess: (() -> Unit),
) {
    val kiwiColors = LocalKiwiColors.current

    Column(
        modifier =
            Modifier
                .offset(y = getResponsiveSizeHeight(Spacing.xLarge))
                .alpha(if (!isLoading || isPreview) 1f else 0f),
    ) {
        WelcomeText()

        Kiwi_Spacer()

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
            keyboardType = KeyboardType.Email,
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

        Kiwi_Spacer()

        Kiwi_FixedSizeButton(
            textArguments =
                KiwiTextArguments(
                    "LOG IN",
                    color = kiwiColors.colorF,
                    fontWeight = FontWeight.Bold,
                ),
            color = kiwiColors.color5,
            onClick = {
                CoroutineScope(Dispatchers.Main).launch {
                    usersViewModel.setManualAuthOverlayActive(true)
                    delay(LOGIN_LOADING_ANIM_DURATION_MS.toLong())
                    val success = performLogin(context, usersViewModel, personalityViewModel, navController)
                    if (success) {
                        onLoginSuccess()
                    }
                    usersViewModel.setManualAuthOverlayActive(false)
                }
            },
            enabled = !isLoading,
            testTag = UsersTestTags.LOGIN_BUTTON,
        )

        Kiwi_Spacer()

        ForgotPassword { navController.navigate(ScreenRoutes.WIP) }

        Kiwi_Spacer()

        LogInErrorMessage(usersUiState)
    }
}

@Composable
private fun WelcomeText() {
    val kiwiColors = LocalKiwiColors.current

    Kiwi_H1(
        KiwiTextArguments(
            "Welcome Back,\nKnight",
            TextAlign.Center,
            color = kiwiColors.colorF,
            fontWeight = FontWeight.Bold,
            modifier =
                Modifier
                    .fillMaxWidth(),
        ),
    )
}

@Composable
private fun LogInErrorMessage(usersUiState: UIState<Unit>) {
    val kiwiColors = LocalKiwiColors.current
    var errorMessage by remember { mutableStateOf("") }
    errorMessage =
        when (usersUiState) {
            is UIState.Error -> {
                usersUiState.message
            } else -> {
                ""
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

@Composable
private fun GoToSignUp(
    isLoading: Boolean,
    isPreview: Boolean,
    navController: NavController,
) {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(bottom = getResponsiveSizeHeight(Spacing.xLarge))
                .alpha(if (!isLoading || isPreview) 1f else 0f),
        contentAlignment = Alignment.BottomCenter,
    ) {
        SignUp {
            navController.navigate(ScreenRoutes.SIGNUP1_WELCOME)
        }
    }
}

private suspend fun performLogin(
    context: Context,
    usersViewModel: IUsersViewModel,
    personalityViewModel: IPersonalityViewModel,
    navController: NavController,
): Boolean {
    if (usersViewModel.login(context).isSuccess) {
        // check personality registered and configured
        // navigate to Home or to the corresponding personality test if anything missing
        personalityViewModel.loadPersonality().fold(
            onSuccess = {
                if (personalityViewModel.state.value?.build == "") {
                    navController.navigate(ScreenRoutes.SIGNUP3_TEST)
                } else if (personalityViewModel.state.value
                        ?.goodApps
                        ?.isEmpty()!! &&
                    personalityViewModel.state.value
                        ?.badApps
                        ?.isEmpty()!!
                ) {
                    navController.navigate(ScreenRoutes.SIGNUP4_APPS)
                } else {
                    navController.navigate(ScreenRoutes.HOME)
                }
            },
            onFailure = {
                navController.navigate(ScreenRoutes.SIGNUP3_TEST)
            },
        )
        return true
    }
    return false
}

@Composable
private fun ForgotPassword(onForgotPass: () -> Unit) {
    val kiwiColors = LocalKiwiColors.current

    val annotatedString =
        buildAnnotatedString {
            withLink(
                link =
                    LinkAnnotation.Clickable(
                        tag = "FORGOTPASS",
                        linkInteractionListener = {
                            onForgotPass()
                        },
                    ),
            ) {
                withStyle(
                    style =
                        SpanStyle(
                            color = kiwiColors.color7B,
                        ),
                ) {
                    append("Forgot Password?")
                }
            }
        }

    Kiwi_AnnotatedString_P1(
        KiwiAnnotatedStringArguments(
            annotatedString,
            TextAlign.Center,
            modifier =
                Modifier
                    .fillMaxWidth(),
        ),
    )
}

@Composable
private fun SignUp(onSignUp: () -> Unit) {
    val kiwiColors = LocalKiwiColors.current

    val annotatedString =
        buildAnnotatedString {
            withStyle(
                style =
                    SpanStyle(
                        color = kiwiColors.color6,
                    ),
            ) {
                append("First Time? \nStart Your Adventure ")
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
                            color = kiwiColors.color7B,
                            textDecoration = TextDecoration.Underline,
                        ),
                ) {
                    append("Here")
                }
            }
        }

    Kiwi_AnnotatedString_P1(
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
fun LogInScreen_Preview() {
    Kiwi_Theme {
        LogInScreen(
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
