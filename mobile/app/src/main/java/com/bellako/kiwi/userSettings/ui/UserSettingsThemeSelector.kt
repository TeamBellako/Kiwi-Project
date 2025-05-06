package com.bellako.kiwi.userSettings.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.bellako.kiwi.userSettings.types.Theme

@Composable
fun UserSettingsThemeSelector(
    selectedTheme: Theme,
    onThemeChange: (Theme) -> Unit
) {
    Text("Theme")
    Theme.entries.forEach { theme ->
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
