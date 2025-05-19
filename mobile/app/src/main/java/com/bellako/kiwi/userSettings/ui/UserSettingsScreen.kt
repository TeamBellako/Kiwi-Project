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
import com.bellako.kiwi.ui.components.Kiwi_InfoBox
import com.bellako.kiwi.ui.components.Kiwi_InputField
import com.bellako.kiwi.ui.components.Kiwi_LoadingIndicator
import com.bellako.kiwi.ui.theme.KiwiTheme
import com.bellako.kiwi.userSettings.utils.UserSettingsTestTags
import com.bellako.kiwi.userSettings.types.Theme
import com.bellako.kiwi.userSettings.viewModel.IUserSettingsViewModel
import com.bellako.kiwi.userSettings.viewModel.UserSettingsFakeViewModel
import com.bellako.kiwi.userSettings.types.UserSettingsState

@Composable
fun UserSettingsScreen(
    viewModel: IUserSettingsViewModel,
    onLogout: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val validationState by viewModel.validationState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadSettings()
    }

    when {
        isLoading -> Kiwi_LoadingIndicator()

        !validationState?.generalError.isNullOrBlank() -> Kiwi_InfoBox(
            validationState?.generalError!!,
            Color.Red,
            UserSettingsTestTags.SERVER_ERROR
        )

        else -> UserSettingsFields(state, viewModel, onLogout)
    }
}

@Composable
private fun UserSettingsFields(
    state: UserSettingsState?,
    viewModel: IUserSettingsViewModel,
    onLogout: () -> Unit
) {
    state?.let { currentState ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Kiwi_InputField(
                false,
                currentState.email,
                {},
                { Text("Email") },
                false,
                ""
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Enable Notifications")
                Spacer(modifier = Modifier.width(8.dp))
                Switch(
                    checked = currentState.areNotificationsEnabled,
                    onCheckedChange = { checked ->
                        viewModel.updateSettings(currentState.copy(areNotificationsEnabled = checked))
                    },
                    modifier = Modifier.testTag(UserSettingsTestTags.NOTIFICATIONS_SWITCH)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            Text("Theme")
            Theme.entries.forEach { theme ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = currentState.theme == theme,
                        onClick = {
                            viewModel.updateSettings(currentState.copy(theme = theme))
                        },
                        modifier = Modifier.testTag("radio_${theme.name.lowercase()}")
                    )
                    Text(text = theme.name)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.clearToken()
                    onLogout()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Logout")
            }
        }
    }
}

@Preview
@Composable
fun UserSettingsScreenPreview() {
    val previewState = UserSettingsState(
        email = "finn@thehuman.com",
        areNotificationsEnabled = true,
        theme = Theme.DARK
    )

    KiwiTheme {
        UserSettingsScreen(UserSettingsFakeViewModel(previewState), {})
    }
}