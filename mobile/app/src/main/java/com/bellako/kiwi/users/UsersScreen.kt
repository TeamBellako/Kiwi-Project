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
import com.bellako.kiwi.ui.theme.KiwiTheme
import com.bellako.kiwi.ui.theme.SeparatorHeight
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun UsersScreen(
    viewModel: IUsersViewModel,
    onLoginSuccess: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val uiState by (viewModel as? UsersViewModel)?.uiState?.collectAsState() ?: remember { mutableStateOf(UIState.Idle) }

    state?.let { currentState ->

        when (uiState) {
            is UIState.GeneralError -> {
                ErrorScreen(onRetry = {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModel.signup(currentState)
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
                    Spacer(modifier = Modifier.height(SeparatorHeight))

                    Fields(viewModel, currentState, isLoading)
                    Spacer(modifier = Modifier.height(SeparatorHeight))

                    Buttons(viewModel, currentState, isLoading, onLoginSuccess)
                    Spacer(modifier = Modifier.height(SeparatorHeight))

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

    Spacer(modifier = Modifier.height(SeparatorHeight))

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
    onLoginSuccess: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Kiwi_Button(
            text = "Sign Up",
            onClick = {
                CoroutineScope(Dispatchers.Main).launch {
                    viewModel.signup(currentState)
                }
            },
            isLoading = isLoading,
            testTag = UsersTestTags.SIGNUP_BUTTON,
            rowModifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(SeparatorHeight))

        Kiwi_Button(
            text = "Login",
            onClick = {
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
