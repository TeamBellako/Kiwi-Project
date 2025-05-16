package com.bellako.kiwi.userSettings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bellako.kiwi.userSettings.utils.UserSettingsTestTags
import com.bellako.kiwi.userSettings.types.Theme
import com.bellako.kiwi.userSettings.viewModel.IUserSettingsViewModel
import com.bellako.kiwi.userSettings.viewModel.UserSettingsFakeViewModel
import com.bellako.kiwi.userSettings.types.UserSettingsState
import com.bellako.kiwi.userSettings.types.UserSettingsValidationState

@Preview
@Composable
fun UserSettingsScreenPreview() {
    val previewState = UserSettingsState(
        email = "finn@thehuman.com",
        areNotificationsEnabled = true,
        theme = Theme.DARK
    )

    UserSettingsScreen(UserSettingsFakeViewModel(previewState))
}

@Composable
fun UserSettingsScreen(viewModel: IUserSettingsViewModel) {
    val state by viewModel.state.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val validationState by viewModel.validationState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadSettings()
    }

    when {
        isLoading -> LoadingIndicator()
        !validationState?.generalError.isNullOrBlank() -> ServerError(validationState?.generalError!!)
        else -> UserSettingsFields(state, viewModel, validationState)
    }
}

@Composable
private fun UserSettingsFields(
    state: UserSettingsState?,
    viewModel: IUserSettingsViewModel,
    validationState: UserSettingsValidationState?
) {
    state?.let {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp)
        ) {
            UserSettingsEmailField(
                email = it.email,
                error = validationState?.emailError,
                onEmailChanged = { email ->
                    viewModel.updateSettings(it.copy(email = email))
                }
            )
            Spacer(modifier = Modifier.height(16.dp))

            UserSettingsNotificationsToggle(
                checked = it.areNotificationsEnabled,
                onCheckedChange = { checked ->
                    viewModel.updateSettings(it.copy(areNotificationsEnabled = checked))
                }
            )
            Spacer(modifier = Modifier.height(16.dp))

            UserSettingsThemeSelector(
                selectedTheme = it.theme,
                onThemeChange = { theme ->
                    viewModel.updateSettings(it.copy(theme = theme))
                }
            )
        }
    }
}

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ServerError(message: String) {
    Text(
        text = message,
        color = Color.Red,
        modifier = Modifier
            .padding(vertical = 80.dp)
            .testTag(UserSettingsTestTags.SERVER_ERROR)
    )
}