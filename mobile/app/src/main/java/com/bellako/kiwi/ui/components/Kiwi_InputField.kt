package com.bellako.kiwi.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun Kiwi_InputField(
    enabled: Boolean,
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable (() -> Unit)?,
    shouldHideInput: Boolean,
    testTag: String
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        val shouldShowPassword = remember { mutableStateOf(false) }

        val keyboardOptions = if (shouldHideInput) {
            KeyboardOptions(keyboardType = KeyboardType.Password)
        } else {
            KeyboardOptions(keyboardType = KeyboardType.Email)
        }

        val visualTransformation = if (shouldHideInput && !shouldShowPassword.value) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        }

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = label,
            singleLine = true,
            enabled = enabled,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            modifier = Modifier
                .fillMaxWidth()
                .testTag(testTag),
            trailingIcon = {
                if (shouldHideInput) {
                    ShowPasswordTrailingIcon(shouldShowPassword)
                }
            }
        )
        if (!enabled) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(MaterialTheme.colorScheme.tertiary)
            )
        }
    }
}

@Composable
private fun ShowPasswordTrailingIcon(
    shouldShowPasswordState: MutableState<Boolean>
) {
    Icon(
        imageVector = if (shouldShowPasswordState.value) Icons.Default.Visibility else Icons.Default.VisibilityOff,
        contentDescription = if (shouldShowPasswordState.value) "Hide password" else "Show password",
        modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        shouldShowPasswordState.value = true
                        tryAwaitRelease()
                        shouldShowPasswordState.value = false
                    }
                )
            }
    )
}