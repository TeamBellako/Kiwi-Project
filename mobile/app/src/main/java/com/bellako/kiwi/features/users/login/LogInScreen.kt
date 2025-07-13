package com.bellako.kiwi.features.users.login

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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.withLink
import com.bellako.kiwi.features.users.IUsersViewModel
import com.bellako.kiwi.features.users.UsersFakeViewModel
import com.bellako.kiwi.features.users.UsersState
import com.bellako.kiwi.features.users.UsersTestTags
import com.bellako.kiwi.ui.components.Kiwi_AnnotatedString
import com.bellako.kiwi.ui.components.Kiwi_AnnotatedStringArguments
import com.bellako.kiwi.ui.components.Kiwi_Button
import com.bellako.kiwi.ui.components.Kiwi_InputField
import com.bellako.kiwi.ui.components.Kiwi_P1


@Composable
fun LogInScreen(
    viewModel: IUsersViewModel,
    navController: NavController
) {

    val state by viewModel.state.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {

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

                    Kiwi_Image(
                        R.drawable.ph_login_bkg,
                        "Login Background",
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
                        LogIn(
                            viewModel,
                            navController
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = Spacing.medium),
                            contentAlignment = Alignment.BottomCenter
                        ) {

                            SignUp() {
                                navController.navigate(ScreenRoutes.SIGNUP_WELCOME)
                            }

                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LogIn(
    viewModel: IUsersViewModel,
    navController: NavController
) {
    val state by viewModel.state.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    state?.let { currentState ->

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .testTag(CommonTestTags.USERS_SCREEN),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // TEXT WELCOME

            Kiwi_H1(Kiwi_TextArguments(
                "Welcome Back, \nKnight",
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.secondary
            ))


            // CREDENTIALS

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
                value = currentState.password,
                onValueChange = { viewModel.onPasswordChanged(it) },
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

            Kiwi_Button(
                Kiwi_TextArguments(
                    "LOG IN",
                    color = MaterialTheme.colorScheme.secondary,
                    bold = true
                ),
                onClick = {
                    CoroutineScope(Dispatchers.Main).launch {
                        if (viewModel.login(currentState).isSuccess) {
                            navController.navigate(ScreenRoutes.HOME)
                        }
                    }
                },
                enabled = !isLoading,
                testTag = UsersTestTags.LOGIN_BUTTON,
            )

            Kiwi_Spacer()

            // LOGIN ERROR  MESSAGE

            var errorMessage by remember { mutableStateOf("") }
            errorMessage = when (uiState) {
                is UIState.Error -> { (uiState as UIState.Error).message }
                else -> { "" }
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

@Composable
private fun SignUp(
    onSignUp: () -> Unit
) {
    val annotatedString = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                color = MaterialTheme.colorScheme.secondary,
            )
        ) {
            append("First Time? \nStart Your Adventure ")
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
                append("Here")
            }
        }
    }

    Kiwi_AnnotatedString(Kiwi_AnnotatedStringArguments(
        annotatedString,
        TextAlign.Center,
    ))
}

@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Composable
fun LogInScreenPreview() {
    KiwiTheme {
        LogInScreen(
            UsersFakeViewModel(
                UsersState("finn@thehuman.com", "Math3matical!"),
            ),
            navController = rememberNavController()
        )
    }
}
