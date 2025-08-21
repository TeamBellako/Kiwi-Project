package com.bellako.kiwi.features.users.screens

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
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
import com.bellako.kiwi.R
import com.bellako.kiwi.common.data.UIState
import com.bellako.kiwi.common.screens.ScreenRoutes
import com.bellako.kiwi.common.screens.components.KiwiAnnotatedStringArguments
import com.bellako.kiwi.common.screens.components.KiwiAnnotatedStringP2
import com.bellako.kiwi.common.screens.components.KiwiH2
import com.bellako.kiwi.common.screens.components.KiwiLabel2
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_Button
import com.bellako.kiwi.common.screens.components.Kiwi_Image
import com.bellako.kiwi.common.screens.components.Kiwi_InfoBox
import com.bellako.kiwi.common.screens.components.Kiwi_InputField
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
import com.bellako.kiwi.ui.KiwiTheme
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun LogInScreen(
    usersViewModel: IUsersViewModel,
    personalityViewModel: IPersonalityViewModel,
    navController: NavController,
) {
    val context = LocalContext.current
    val uiState by usersViewModel.uiState.collectAsState()

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        when (uiState) {
            is UIState.GeneralError -> {
                ErrorModal(onRetry = {
                    CoroutineScope(Dispatchers.Main).launch {
                        val result = usersViewModel.login(context)
                        if (result.isSuccess) {
                            navController.navigate(ScreenRoutes.HOME)
                            usersViewModel.onLoginSuccess()
                        }
                    }
                })
            }

            else -> {
                Kiwi_Image(
                    R.drawable.ph_login_bkg,
                    "Login Background",
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter),
                    contentScale = ContentScale.Crop,
                )

                LogIn(
                    usersViewModel,
                    personalityViewModel,
                    navController,
                )
            }
        }
    }
}

@Composable
private fun LogIn(
    usersViewModel: IUsersViewModel,
    personalityViewModel: IPersonalityViewModel,
    navController: NavController,
) {
    val context = LocalContext.current

    val state by usersViewModel.state.collectAsState()
    val uiState by usersViewModel.uiState.collectAsState()

    val usersIsLoading by usersViewModel.isLoading.collectAsState()
    val personalityIsLoading by personalityViewModel.isLoading.collectAsState()
    var initializing by remember { mutableStateOf(true) }
    val isLoading by remember { derivedStateOf { initializing || usersIsLoading || personalityIsLoading } }

    val isPreview = LocalInspectionMode.current

    // check stored credentials for auto login
    LaunchedEffect(Unit) {
        initializing = false
        val (username, password) = usersViewModel.getLocalCredentials(context)
        if (!username.isNullOrBlank() && !password.isNullOrBlank()) {
            usersViewModel.onEmailChanged(username)
            usersViewModel.onPasswordChanged(password)
            performLogin(context, usersViewModel, personalityViewModel, navController)
        }
    }

    state?.let { currentState ->

        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(getResponsiveSizeHeight(Spacing.medium)),
            contentAlignment = Alignment.Center,
        ) {
            if (isLoading || isPreview) {
                LoadingModal()
            }

            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .testTag(CommonTestTags.USERS_SCREEN),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // TEXT WELCOME

                KiwiH2(
                    KiwiTextArguments(
                        "Welcome Back, \nKnight",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(bottom = getResponsiveSizeHeight(Spacing.large)),
                    ),
                )

                Column(
                    modifier =
                        Modifier
                            .alpha(if (!isLoading || isPreview) 1f else 0f),
                ) {
                    // CREDENTIALS

                    Kiwi_InputField(
                        enabled = !isLoading,
                        value = currentState.email,
                        onValueChange = { usersViewModel.onEmailChanged(it) },
                        label = {
                            KiwiLabel2(
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
                        value = currentState.password,
                        onValueChange = { usersViewModel.onPasswordChanged(it) },
                        label = {
                            KiwiLabel2(
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

                    Kiwi_Spacer()

                    Kiwi_Button(
                        KiwiTextArguments(
                            "LOG IN",
                            color = MaterialTheme.colorScheme.secondary,
                            bold = true,
                        ),
                        onClick = {
                            CoroutineScope(Dispatchers.Main).launch {
                                performLogin(context, usersViewModel, personalityViewModel, navController)
                            }
                        },
                        enabled = !isLoading,
                        testTag = UsersTestTags.LOGIN_BUTTON,
                    )

                    Kiwi_Spacer()

                    // LOGIN ERROR  MESSAGE

                    var errorMessage by remember { mutableStateOf("") }
                    errorMessage =
                        when (uiState) {
                            is UIState.Error -> {
                                (uiState as UIState.Error).message
                            }

                            else -> {
                                ""
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
            }
        }

        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(bottom = getResponsiveSizeHeight(Spacing.medium))
                    .alpha(if (!isLoading || isPreview) 1f else 0f),
            contentAlignment = Alignment.BottomCenter,
        ) {
            SignUp {
                navController.navigate(ScreenRoutes.SIGNUP1_WELCOME)
            }
        }
    }
}

private suspend fun performLogin(
    context: Context,
    usersViewModel: IUsersViewModel,
    personalityViewModel: IPersonalityViewModel,
    navController: NavController,
) {
    if (usersViewModel.login(context).isSuccess) {
        // check personality registered and configured, navigate to Home or to the corresponding personality test if anything missing
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
        usersViewModel.onLoginSuccess()
    }
}

@Composable
private fun SignUp(onSignUp: () -> Unit) {
    val annotatedString =
        buildAnnotatedString {
            withStyle(
                style =
                    SpanStyle(
                        color = MaterialTheme.colorScheme.secondary,
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
                            color = MaterialTheme.colorScheme.inversePrimary,
                            textDecoration = TextDecoration.Underline,
                        ),
                ) {
                    append("Here")
                }
            }
        }

    KiwiAnnotatedStringP2(
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
fun LogInScreenPreview() {
    KiwiTheme {
        LogInScreen(
            UsersFakeViewModel(UsersState(validUsersDTO().email, validUsersDTO().password)),
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
