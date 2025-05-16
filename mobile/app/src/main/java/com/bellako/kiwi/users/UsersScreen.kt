package com.bellako.kiwi.users

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

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

@Composable
fun UsersScreen(
    viewModel: IUsersViewModel,
) {
    val state by viewModel.state.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val loadingOverlayColor = Color(0x20FFFFFF)

    var errorMessage by remember { mutableStateOf("") }
    var resultMessage by remember { mutableStateOf("") }

    state?.let { currentState ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Welcome", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(24.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = currentState.email,
                    onValueChange = { email -> viewModel.onEmailChanged(email) },
                    label = { Text("Email") },
                    singleLine = true,
                    enabled = !isLoading,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
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

            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = currentState.password,
                    onValueChange = { password -> viewModel.onPasswordChanged(password) },
                    label = { Text("Password") },
                    singleLine = true,
                    enabled = !isLoading,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(UsersTestTags.PASSWORD_FIELD)
                )
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(loadingOverlayColor)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    Button(
                        onClick = {
                            errorMessage = ""
                            resultMessage = ""

                            val result : Result<Unit> = viewModel.signup(currentState)
                            if (result.isSuccess) {
                                resultMessage = "New User Successfully Created!"
                            } else {
                                errorMessage = result.exceptionOrNull()?.message.toString()
                            }
                      },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag(UsersTestTags.SIGNUP_BUTTON),
                        enabled = !isLoading
                    ) {
                        Text("Sign Up")
                    }
                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(loadingOverlayColor)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Box(modifier = Modifier.weight(1f)) {
                    Button(
                        onClick = {
                            errorMessage = ""
                            resultMessage = ""

                            val result : Result<Unit> = viewModel.login(currentState)
                            if (result.isSuccess) {

                            } else {
                                errorMessage = result.exceptionOrNull()?.message.toString()
                            }
                      },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag(UsersTestTags.LOGIN_BUTTON),
                        enabled = !isLoading
                    ) {
                        Text("Login")
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

            Spacer(modifier = Modifier.height(24.dp))

            if (!errorMessage.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Red)
                ) {
                    Text(
                        modifier = Modifier
                            .padding(10.dp)
                            .testTag(UsersTestTags.ERROR_TEXT),
                        text =  errorMessage)
                }
            }
            if (!resultMessage.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Green)
                ) {
                    Text(
                        modifier = Modifier
                            .padding(10.dp)
                            .testTag(UsersTestTags.RESULT_TEXT),
                        text =  resultMessage)
                }
            }
        }
    }
}
