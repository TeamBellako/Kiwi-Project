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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
fun UserSettingsScreen(defaultUserSettingsDto: UserSettingsDto = UserSettingsDto()) {
    val userSettingsState = remember { UserSettingsState(defaultUserSettingsDto) }

    Column(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
            .padding(16.dp)
    ) {
        EmailField(
            email = userSettingsState.email,
            onEmailChanged = { userSettingsState.email = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        NotificationsSwitch(
            areNotificationsEnabled = userSettingsState.areNotificationsEnabled,
            onNotificationChange = { userSettingsState.areNotificationsEnabled = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        ThemeRadioButtons(
            selectedTheme = userSettingsState.theme,
            onThemeClicked = { selectedTheme ->
                userSettingsState.theme = selectedTheme
            }
        )
    }
}


@Preview
@Composable
fun UserSettingsScreenPreview() {
    UserSettingsScreen();
}