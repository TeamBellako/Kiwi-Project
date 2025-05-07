package com.bellako.kiwi.userSettings.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.bellako.kiwi.utils.TestTags

@Composable
fun UserSettingsEmailField(email: String, error: String? = null, onEmailChanged: (String) -> Unit) {
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