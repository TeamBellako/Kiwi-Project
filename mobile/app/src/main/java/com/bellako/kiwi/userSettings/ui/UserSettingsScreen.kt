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
import com.bellako.kiwi.ui.utils.TestTags
import com.bellako.kiwi.userSettings.viewModel.IUserSettingsViewModel
import com.bellako.kiwi.userSettings.types.UserSettings
import com.bellako.kiwi.userSettings.types.UserSettingsDTO
import com.bellako.kiwi.userSettings.types.UserSettingsFactory
import com.bellako.kiwi.userSettings.viewModel.UserSettingsFakeViewModel
import com.bellako.kiwi.userSettings.types.UserSettingsState
import com.bellako.kiwi.userSettings.types.UserSettingsValidationState

@Preview
@Composable
fun UserSettingsScreenPreview() {
    val previewState = UserSettingsFactory.fromDto(
        UserSettingsDTO(
            email = "finn@thehuman.com",
            areNotificationsEnabled = true,
            theme = UserSettings.Theme.DARK
        )
    ).getOrThrow().let { UserSettingsFactory.toState(it) }

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
            EmailField(
                email = it.email,
                error = validationState?.emailError,
                onEmailChanged = { email ->
                    viewModel.updateSettings(it.copy(email = email))
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            NotificationsSwitch(
                checked = it.areNotificationsEnabled,
                onCheckedChange = { checked ->
                    viewModel.updateSettings(it.copy(areNotificationsEnabled = checked))
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ThemeRadioButtons(
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
            .testTag(TestTags.SERVER_ERROR)
    )
}

@Composable
fun EmailField(email: String, error: String? = null, onEmailChanged: (String) -> Unit) {
    Column {
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChanged,
            label = { Text("Email") },
            isError = error != null,
            modifier = Modifier
                .testTag(TestTags.EMAIL_FIELD)
                .fillMaxWidth()
        )
        if (error != null) {
            Text(
                text = error,
                color = Color.Red,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .testTag(TestTags.FIELD_ERROR)
            )
        }
    }
}


@Composable
fun NotificationsSwitch(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("Enable Notifications")
        Spacer(modifier = Modifier.width(8.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.testTag(TestTags.NOTIFICATIONS_SWITCH)
        )
    }
}

@Composable
fun ThemeRadioButtons(
    selectedTheme: UserSettings.Theme,
    onThemeChange: (UserSettings.Theme) -> Unit
) {
    Text("Theme")
    UserSettings.Theme.entries.forEach { theme ->
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = selectedTheme == theme,
                onClick = { onThemeChange(theme) },
                modifier = Modifier.testTag("radio_${theme.name.lowercase()}")
            )
            Text(text = theme.name)
        }
    }
}
