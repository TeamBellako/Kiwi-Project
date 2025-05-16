package com.bellako.kiwi.users

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

private val separatorHeight = 24.dp
private val loadingOverlayColor = Color(0x20FFFFFF)

@Composable
fun UsersScreen(
    viewModel: IUsersViewModel,
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
            UsersForm(currentState, viewModel, isLoading)
            Spacer(modifier = Modifier.height(separatorHeight))

            UsersButtons(currentState, viewModel, isLoading, errorMessageState, successMessageState)
            Spacer(modifier = Modifier.height(separatorHeight))

            if (!errorMessageState.value.isEmpty()) {
                ResultBox(
                    errorMessageState.value,
                    Color.Red,
                    UsersTestTags.ERROR_TEXT
                )
            }

            if (!successMessageState.value.isEmpty()) {
                ResultBox(
                    successMessageState.value,
                    Color.Green,
                    UsersTestTags.SUCCESS_TEXT
                )
            }

        }
    }
}

@Composable
private fun ResultBox(
    message: String,
    color: Color,
    testTag: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color)
    ) {
        Text(
            modifier = Modifier
                .padding(10.dp)
                .testTag(testTag),
            text = message
        )
    }
}

@Composable
private fun UsersForm(
    currentState: UsersState,
    viewModel: IUsersViewModel,
    isLoading: Boolean,
) {
    Text(text = "Welcome", style = MaterialTheme.typography.headlineMedium)

    Spacer(modifier = Modifier.height(separatorHeight))

    InputField(
        isLoading,
        InputFieldData(
            currentState.email,
            { email -> viewModel.onEmailChanged(email) },
            { Text("Email") },
            false,
            UsersTestTags.EMAIL_FIELD
        )
    )

    Spacer(modifier = Modifier.height(separatorHeight))

    InputField(
        isLoading,
        InputFieldData(
            currentState.password,
            { password -> viewModel.onPasswordChanged(password) },
            { Text("Password") },
            true,
            UsersTestTags.PASSWORD_FIELD
        )
    )
}

@Composable
private fun InputField(
    isLoading: Boolean,
    inputFieldData: InputFieldData
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        var keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        var visualTransformation : VisualTransformation = VisualTransformation.None

        if (inputFieldData.shouldHideInput) {
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            visualTransformation = PasswordVisualTransformation()
        }

        OutlinedTextField(
            value = inputFieldData.value,
            onValueChange = inputFieldData.onValueChange,
            label = inputFieldData.label,
            singleLine = true,
            enabled = !isLoading,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            modifier = Modifier
                .fillMaxWidth()
                .testTag(UsersTestTags.EMAIL_FIELD)
        )
        if (isLoading) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(loadingOverlayColor)
            )
        }
    }
}

@Composable
private fun UsersButtons(
    currentState: UsersState,
    viewModel: IUsersViewModel,
    isLoading: Boolean,
    errorMessageState: MutableState<String>,
    successMessageState: MutableState<String>
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        FormButton(
            "Sign Up",
            {
                errorMessageState.value = ""
                successMessageState.value = ""

                val result : Result<Unit> = viewModel.signup(currentState)
                if (result.isSuccess) {
                    successMessageState.value = "New User Successfully Created!"
                } else {
                    errorMessageState.value = result.exceptionOrNull()?.message.toString()
                }
            },
            isLoading,
            UsersTestTags.SIGNUP_BUTTON,
            Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(separatorHeight))

        FormButton(
            "Login",
            {
                errorMessageState.value = ""
                successMessageState.value = ""

                val result : Result<Unit> = viewModel.login(currentState)
                if (result.isSuccess) {

                } else {
                    errorMessageState.value = result.exceptionOrNull()?.message.toString()
                }
            },
            isLoading,
            UsersTestTags.LOGIN_BUTTON,
            Modifier.weight(1f)
        )
    }
}

@Composable
private fun FormButton(
    text: String,
    onClick: () -> Unit,
    isLoading: Boolean,
    testTag: String,
    rowModifier: Modifier
) {
    Box(modifier = rowModifier) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .testTag(testTag),
            enabled = !isLoading
        ) {
            Text(text)
        }
        if (isLoading) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(loadingOverlayColor)
            )
        }
    }
}

private data class InputFieldData(
    var value: String,
    val onValueChange: (String) -> Unit,
    val label: @Composable (() -> Unit)?,
    val shouldHideInput: Boolean,
    val testTag: String
)

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
        )
    )
}