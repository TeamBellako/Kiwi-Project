package com.bellako.kiwi.users

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
            false
        )
    )
}

@Composable
fun UsersScreen(
    viewModel: IUsersViewModel,
) {
    val state by viewModel.state.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    state?.let {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Welcome", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = it.email,
                onValueChange = { email -> viewModel.onEmailChanged(email) },
                label = { Text("Email") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = it.password,
                onValueChange = { password -> viewModel.onPasswordChanged(password) },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { viewModel.signup(state!!) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Sign Up")
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = { viewModel.login(state!!) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Login")
                }
            }
        }
    }
}
