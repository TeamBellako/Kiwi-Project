
package com.bellako.kiwi.usersettings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bellako.kiwi.ui.utils.TestTags

@Composable
fun EmailField(email: String, onEmailChanged: (String) -> Unit) {
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
fun ThemeRadioButtons(selectedTheme: UserSettingsDto.Theme, onThemeChange: (UserSettingsDto.Theme) -> Unit) {
    Text("Theme")
    UserSettingsDto.Theme.entries.forEach { theme ->
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

@Composable
fun UserSettingsScreen(viewModel: IUserSettingsViewModel) {
    LaunchedEffect(Unit) {
        viewModel.loadSettings()
    }

    val state: UserSettingsState? = viewModel.state.collectAsState().value

    state?.let {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp)
        ) {
            EmailField(email = it.email, onEmailChanged = { email ->
                viewModel.updateSettings(it.copy(email = email).toDto())
            })
            Spacer(modifier = Modifier.height(16.dp))

            NotificationsSwitch(
                checked = it.areNotificationsEnabled,
                onCheckedChange = { checked ->
                    viewModel.updateSettings(it.copy(areNotificationsEnabled = checked).toDto())
                }
            )
            Spacer(modifier = Modifier.height(16.dp))

            ThemeRadioButtons(
                selectedTheme = it.theme,
                onThemeChange = { theme ->
                    viewModel.updateSettings(it.copy(theme = theme).toDto())
                }
            )
        }
    } ?: run {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
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