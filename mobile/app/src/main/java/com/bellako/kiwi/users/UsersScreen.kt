package com.bellako.kiwi.users

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bellako.kiwi.ui.components.Kiwi_Button
import com.bellako.kiwi.ui.components.Kiwi_InfoBox
import com.bellako.kiwi.ui.components.Kiwi_InputField
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

    val errorMessageState = remember { mutableStateOf("") }
    val successMessageState = remember { mutableStateOf("") }

    state?.let { currentState ->
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

            Buttons(viewModel, currentState, isLoading, errorMessageState, successMessageState, onLoginSuccess)
            Spacer(modifier = Modifier.height(SeparatorHeight))

            InfoBoxes(errorMessageState, successMessageState)
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
        isLoading,
        currentState.email,
        { email -> viewModel.onEmailChanged(email) },
        { Text("Email") },
        false,
        UsersTestTags.EMAIL_FIELD
    )

    Spacer(modifier = Modifier.height(SeparatorHeight))

    Kiwi_InputField(
        isLoading,
        currentState.password,
        { password -> viewModel.onPasswordChanged(password) },
        { Text("Password") },
        true,
        UsersTestTags.PASSWORD_FIELD
    )
}

@Composable
private fun Buttons(
    viewModel: IUsersViewModel,
    currentState: UsersState,
    isLoading: Boolean,
    errorMessageState: MutableState<String>,
    successMessageState: MutableState<String>,
    onLoginSuccess: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Kiwi_Button(
            "Sign Up",
            {
                onSignUpClicked(
                    viewModel,
                    currentState,
                    errorMessageState,
                    successMessageState
                )
            },
            isLoading,
            UsersTestTags.SIGNUP_BUTTON,
            Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(SeparatorHeight))

        Kiwi_Button(
            "Login",
            {
                onLoginClicked(
                    viewModel,
                    currentState,
                    errorMessageState,
                    successMessageState,
                    onLoginSuccess
                )
            },
            isLoading,
            UsersTestTags.LOGIN_BUTTON,
            Modifier.weight(1f)
        )
    }
}

private fun onSignUpClicked(
    viewModel: IUsersViewModel,
    currentState: UsersState,
    errorMessageState: MutableState<String>,
    successMessageState: MutableState<String>
) {
    CoroutineScope(Dispatchers.Main).launch {
        errorMessageState.value = ""
        successMessageState.value = ""

        val result: Result<Unit> = viewModel.signup(currentState)
        if (result.isSuccess) {
            successMessageState.value = "New User Successfully Created!"
        } else {
            val exception = result.exceptionOrNull()
            val message = exception?.message ?: "Unknown error"

            errorMessageState.value = message
        }
    }
}

private fun onLoginClicked(
    viewModel: IUsersViewModel,
    currentState: UsersState,
    errorMessageState: MutableState<String>,
    successMessageState: MutableState<String>,
    onLoginSuccess: () -> Unit
) {
    CoroutineScope(Dispatchers.Main).launch {
        errorMessageState.value = ""
        successMessageState.value = ""

        val result : Result<Unit> = viewModel.login(currentState)
        if (result.isSuccess) {
            onLoginSuccess()
        } else {
            val exception = result.exceptionOrNull()
            val message = exception?.message ?: "Unknown error"

            errorMessageState.value = message
        }
    }
}

@Composable
private fun InfoBoxes(
    errorMessageState: MutableState<String>,
    successMessageState: MutableState<String>
) {
    if (!errorMessageState.value.isEmpty()) {
        Kiwi_InfoBox(
            errorMessageState.value,
            Color.Red,
            UsersTestTags.ERROR_TEXT
        )
    }

    if (!successMessageState.value.isEmpty()) {
        Kiwi_InfoBox(
            successMessageState.value,
            Color.Green,
            UsersTestTags.SUCCESS_TEXT
        )
    }
}

@Preview
@Composable
fun UsersScreenPreview() {
    UsersScreen(
        UsersFakeViewModel(
            UsersState(
                "finn@thehuman.com",
                "Math3matical!"
            ),
            false,
        ),
        {}
    )
}