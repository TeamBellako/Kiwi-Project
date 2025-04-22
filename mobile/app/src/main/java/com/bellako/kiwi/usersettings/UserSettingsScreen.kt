package com.bellako.kiwi.usersettings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bellako.kiwi.ui.utils.TestTags

@Composable
fun EmailField(
    email: String,
    onEmailChanged: (String) -> Unit
) {
    OutlinedTextField(
        value = email,
        onValueChange = onEmailChanged,
        label = { Text("Email") },
        modifier = Modifier
            .testTag(TestTags.EMAIL_FIELD)
            .fillMaxWidth()
    )
}

@Composable
fun NotificationsSwitch(
    areNotificationsEnabled: Boolean,
    onNotificationChange: (Boolean) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("Enable Notifications")
        Spacer(modifier = Modifier.width(8.dp))
        Switch(
            modifier = Modifier.testTag(TestTags.NOTIFICATIONS_SWITCH),
            checked = areNotificationsEnabled,
            onCheckedChange = onNotificationChange
        )
    }
}

@Composable
fun ThemeRadioButtons(
    selectedTheme: UserSettingsDto.Theme,
    onThemeClicked: (UserSettingsDto.Theme) -> Unit
) {
    Text("Theme")
    UserSettingsDto.Theme.entries.forEach { themeOption ->
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(end = 16.dp)
        ) {
            RadioButton(
                selected = selectedTheme == themeOption,
                onClick = { onThemeClicked(themeOption) },
                modifier = Modifier.testTag("radio_${themeOption.name.lowercase()}")
            )
            Text(text = themeOption.name)
        }
    }
}

@Composable
fun UserSettingsScreen(viewModel: IUserSettingsViewModel) {
    val settingsState = viewModel.state.collectAsState()
    val state = settingsState.value

    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadSettings()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (state != null) {
            EmailField(
                email = state.email,
                onEmailChanged = { state.email = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            NotificationsSwitch(
                areNotificationsEnabled = state.areNotificationsEnabled,
                onNotificationChange = { state.areNotificationsEnabled = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ThemeRadioButtons(
                selectedTheme = state.theme,
                onThemeClicked = { selected ->
                    state.theme = selected
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.updateSettings() },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Save")
            }
        }

        if (error != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Error: $error", color = Color.Red)
        }
    }
}

@Preview
@Composable
fun UserSettingsScreenPreview() {
    val previewUserSettings = UserSettingsDto(
        1, // TODO: Remove when JWT is implemented
        "finn@thehuman.com",
        true,
        UserSettingsDto.Theme.DARK
    )
    UserSettingsScreen(FakeUserSettingsViewModel(previewUserSettings.toState()));
}