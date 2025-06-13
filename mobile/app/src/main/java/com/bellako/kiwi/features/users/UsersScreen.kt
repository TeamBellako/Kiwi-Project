package com.bellako.kiwi.features.users

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.services.common.CommonTestTags
import com.bellako.kiwi.ui.modals.ErrorModal
import com.bellako.kiwi.services.common.UIState
import com.bellako.kiwi.ui.screens.ScreenRoutes
import com.bellako.kiwi.ui.components.Kiwi_Button
import com.bellako.kiwi.ui.components.Kiwi_H1
import com.bellako.kiwi.ui.components.Kiwi_InfoBox
import com.bellako.kiwi.ui.components.Kiwi_InputField
import com.bellako.kiwi.ui.components.Kiwi_Spacer
import com.bellako.kiwi.ui.components.Kiwi_TextArguments
import com.bellako.kiwi.ui.theme.KiwiTheme
import com.bellako.kiwi.services.common.Logger
import com.bellako.kiwi.ui.components.Kiwi_H2
import com.bellako.kiwi.ui.components.Kiwi_P1
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
                        .background(MaterialTheme.colorScheme.background)
                        .padding(16.dp)
                        .testTag(CommonTestTags.USERS_SCREEN),
                    verticalArrangement = Arrangement.Center
                ) {
                    Kiwi_H1(Kiwi_TextArguments(
                        "Welcome Back, Knight",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.inversePrimary
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
                                color = MaterialTheme.colorScheme.error,
                                testTag = UsersTestTags.ERROR_TEXT
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
    viewModel: IUsersViewModel,
    currentState: UsersState,
    isLoading: Boolean,
) {
    Kiwi_InputField(
        enabled = !isLoading,
        value = currentState.email,
        onValueChange = { viewModel.onEmailChanged(it) },
        label = {
            Kiwi_P1(Kiwi_TextArguments(
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
        enabled = !isLoading,
        value = currentState.password,
        onValueChange = { viewModel.onPasswordChanged(it) },
        label = {
            Kiwi_P1(Kiwi_TextArguments(
                "Password",
                color = MaterialTheme.colorScheme.inversePrimary
            ))
        },
        shouldHideInput = true,
        textColor = MaterialTheme.colorScheme.inversePrimary,
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
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Kiwi_Button(
            Kiwi_TextArguments(
                "SIGN UP",
                color = MaterialTheme.colorScheme.inversePrimary,
                bold = true
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
            testTag = UsersTestTags.SIGNUP_BUTTON,
            rowModifier = Modifier.weight(1f)
        )

        Box(modifier = Modifier.padding(24.dp))

        Kiwi_Button(
            Kiwi_TextArguments(
                "LOG IN",
                color = MaterialTheme.colorScheme.inversePrimary,
                bold = true
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
            testTag = UsersTestTags.LOGIN_BUTTON,
            rowModifier = Modifier.weight(1f)
        )
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
            navController = rememberNavController()
        )
    }
}
