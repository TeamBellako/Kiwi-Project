package com.bellako.kiwi.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.common.CommonTestTags
import com.bellako.kiwi.modals.ErrorModal
import com.bellako.kiwi.common.UIState
import com.bellako.kiwi.common.ScreenRoutes
import com.bellako.kiwi.ui.components.Kiwi_Button
import com.bellako.kiwi.ui.components.Kiwi_H1
import com.bellako.kiwi.ui.components.Kiwi_InfoBox
import com.bellako.kiwi.ui.components.Kiwi_InputField
import com.bellako.kiwi.ui.components.Kiwi_Spacer
import com.bellako.kiwi.ui.components.Kiwi_TextArguments
import com.bellako.kiwi.ui.theme.KiwiTheme
import com.bellako.kiwi.utils.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

enum class RetryAction {
    SIGNUP,
    LOGIN
}

@Composable
fun LoginScreen(
    viewModel: ILoginViewModel,
    navController: NavController
) {
    val state by viewModel.state.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val lastAction = remember { mutableStateOf<RetryAction?>(null) }

    state?.let { currentState ->
        when (uiState) {
            is UIState.GeneralError -> {
                ErrorModal(onRetry = {
                    CoroutineScope(Dispatchers.Main).launch {
                        when (lastAction.value) {
                            RetryAction.SIGNUP -> viewModel.signup(currentState)
                            RetryAction.LOGIN -> {
                                val result = viewModel.login(currentState)
                                if (result.isSuccess) { navController.navigate(ScreenRoutes.HOME) }
                            }
                            null -> {}
                        }
                    }
                })
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .padding(16.dp)
                        .testTag(CommonTestTags.LOGIN_SCREEN),
                    verticalArrangement = Arrangement.Center
                ) {
                    Kiwi_H1(Kiwi_TextArguments(
                        "Welcome!"
                    ))
                    Kiwi_Spacer()

                    Fields(viewModel, currentState, isLoading)
                    Kiwi_Spacer()

                    Buttons(viewModel, currentState, isLoading, lastAction, { navController.navigate(ScreenRoutes.HOME) })
                    Kiwi_Spacer()

                    when (uiState) {
                        is UIState.Error -> {
                            Kiwi_InfoBox(
                                message = (uiState as UIState.Error).message,
                                color = Color.Red,
                                testTag = LoginTestTags.ERROR_TEXT
                            )
                        }

                        else -> {}
                    }
                }
            }
        }
    }
}

@Composable
private fun Fields(
    viewModel: ILoginViewModel,
    currentState: LoginState,
    isLoading: Boolean,
) {
    Kiwi_InputField(
        enabled = !isLoading,
        value = currentState.email,
        onValueChange = { viewModel.onEmailChanged(it) },
        label = { Text("Email") },
        shouldHideInput = false,
        testTag = LoginTestTags.EMAIL_FIELD
    )

    Kiwi_Spacer()

    Kiwi_InputField(
        enabled = !isLoading,
        value = currentState.password,
        onValueChange = { viewModel.onPasswordChanged(it) },
        label = { Text("Password") },
        shouldHideInput = true,
        testTag = LoginTestTags.PASSWORD_FIELD
    )
}

@Composable
private fun Buttons(
    viewModel: ILoginViewModel,
    currentState: LoginState,
    isLoading: Boolean,
    lastAction: MutableState<RetryAction?>,
    onLoginSuccess: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Kiwi_Button(
            Kiwi_TextArguments(
                "Sign Up",
                color = Color.White
            ),
            onClick = {
                lastAction.value = RetryAction.SIGNUP
                Logger.info("Retry action set to " + lastAction.value.toString())

                CoroutineScope(Dispatchers.Main).launch {
                    if (viewModel.signup(currentState).isSuccess) {
                        onLoginSuccess()
                    }
                }
            },
            isLoading = isLoading,
            testTag = LoginTestTags.SIGNUP_BUTTON,
            rowModifier = Modifier.weight(1f)
        )

        Box(modifier = Modifier.padding(24.dp))

        Kiwi_Button(
            Kiwi_TextArguments(
                "Log In",
                color = Color.White
            ),
            onClick = {
                lastAction.value = RetryAction.LOGIN
                Logger.info("Retry action set to " + lastAction.value.toString())

                CoroutineScope(Dispatchers.Main).launch {
                    if (viewModel.login(currentState).isSuccess) {
                        onLoginSuccess()
                    }
                }
            },
            isLoading = isLoading,
            testTag = LoginTestTags.LOGIN_BUTTON,
            rowModifier = Modifier.weight(1f)
        )
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    KiwiTheme {
        LoginScreen(
            LoginFakeViewModel(
                LoginState("finn@thehuman.com", "Math3matical!"),
                isLoading = false
            ),
            navController = rememberNavController()
        )
    }
}
