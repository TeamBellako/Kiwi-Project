package com.bellako.kiwi.usersettings

import androidx.compose.foundation.background
import androidx.compose.material3.Switch
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun UserSettingsScreen() {
    var email by remember { mutableStateOf("") }
    var areNotificationsEnabled by remember { mutableStateOf(false) }
    var selectedTheme by remember { mutableStateOf(UserSettings.Theme.LIGHT) }

    Column(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Email Input
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                println("Email changed: $email")
            },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Notifications Toggle
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Enable Notifications")
            Spacer(modifier = Modifier.width(8.dp))
            Switch(
                checked = areNotificationsEnabled,
                onCheckedChange = {
                    areNotificationsEnabled = it
                    println("Notifications enabled: $areNotificationsEnabled")
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Theme Selector
        Text("Theme")
        UserSettings.Theme.entries.forEach { themeOption ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = 16.dp)
            ) {
                RadioButton(
                    selected = selectedTheme == themeOption,
                    onClick = {
                        selectedTheme = themeOption
                        println("Theme selected: $selectedTheme")
                    },
                    modifier = Modifier.testTag("radio_${themeOption.name.lowercase()}") // 👈 here
                )
                Text(text = themeOption.name)
            }
        }
    }
}

@Preview
@Composable
fun UserSettingsScreenPreview() {
    UserSettingsScreen();
}