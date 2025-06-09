package com.bellako.kiwi.users

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bellako.kiwi.common.ErrorScreen
import com.bellako.kiwi.common.UIState
import com.bellako.kiwi.ui.components.Kiwi_Button
import com.bellako.kiwi.ui.components.Kiwi_InfoBox
import com.bellako.kiwi.ui.components.Kiwi_InputField
import com.bellako.kiwi.ui.components.Kiwi_Spacer
import com.bellako.kiwi.ui.theme.KiwiTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

enum class RetryAction {
    SIGNUP,
    LOGIN
}

@Composable
fun UsersScreen(
    viewModel: IUsersViewModel,
    onLoginSuccess: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val lastAction = remember { mutableStateOf<RetryAction?>(null) }

    state?.let { currentState ->
        when (uiState) {
            is UIState.GeneralError -> {
                ErrorScreen(onRetry = {
                    CoroutineScope(Dispatchers.Main).launch {
                        when (lastAction.value) {
                            RetryAction.SIGNUP -> viewModel.signup(currentState)
                            RetryAction.LOGIN -> {
                                val result = viewModel.login(currentState)
                                if (result.isSuccess) onLoginSuccess()
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
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "Welcome", style = MaterialTheme.typography.headlineMedium)
                    Kiwi_Spacer()

                    Fields(viewModel, currentState, isLoading)
                    Kiwi_Spacer()

                    Buttons(viewModel, currentState, isLoading, lastAction, onLoginSuccess)
                    Kiwi_Spacer()

                    InfoBoxes(uiState)
                }
            }
        }
    }
}

@Composable
private fun Fields(
    viewModel: IUsersViewModel,
    currentState: UsersState,
    isLoading: Boolean,
) {
    Kiwi_InputField(
        enabled = !isLoading,
        value = currentState.email,
        onValueChange = { viewModel.onEmailChanged(it) },
        label = { Text("Email") },
        shouldHideInput = false,
        testTag = UsersTestTags.EMAIL_FIELD
    )

    Kiwi_Spacer()

    Kiwi_InputField(
        enabled = !isLoading,
        value = currentState.password,
        onValueChange = { viewModel.onPasswordChanged(it) },
        label = { Text("Password") },
        shouldHideInput = true,
        testTag = UsersTestTags.PASSWORD_FIELD
    )
}

@Composable
private fun Buttons(
    viewModel: IUsersViewModel,
    currentState: UsersState,
    isLoading: Boolean,
    lastAction: MutableState<RetryAction?>,
    onLoginSuccess: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Kiwi_Button(
            text = "Sign Up",
            onClick = {
                lastAction.value = RetryAction.SIGNUP
                CoroutineScope(Dispatchers.Main).launch {
                    viewModel.signup(currentState)
                }
            },
            isLoading = isLoading,
            testTag = UsersTestTags.SIGNUP_BUTTON,
            rowModifier = Modifier.weight(1f)
        )

        Kiwi_Spacer()

        Kiwi_Button(
            text = "Login",
            onClick = {
                lastAction.value = RetryAction.LOGIN
                CoroutineScope(Dispatchers.Main).launch {
                    val result = viewModel.login(currentState)
                    if (result.isSuccess) onLoginSuccess()
                }
            },
            isLoading = isLoading,
            testTag = UsersTestTags.LOGIN_BUTTON,
            rowModifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun InfoBoxes(uiState: UIState<Unit>) {
    when (uiState) {
        is UIState.Error -> {
            Kiwi_InfoBox(
                message = uiState.message,
                color = Color.Red,
                testTag = UsersTestTags.ERROR_TEXT
            )
        }

        is UIState.Success -> {
            Kiwi_InfoBox(
                message = "New User Successfully Created!",
                color = Color.Green,
                testTag = UsersTestTags.SUCCESS_TEXT
            )
        }

        else -> {}
    }
}

@Preview
@Composable
fun UsersScreenPreview() {
    KiwiTheme {
        UsersScreen(
            UsersFakeViewModel(
                UsersState("finn@thehuman.com", "Math3matical!"),
                isLoading = false
            ),
            onLoginSuccess = {}
        )
    }
}
