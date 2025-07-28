package com.bellako.kiwi.ui.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.bellako.kiwi.features.users.UsersTestTags
import com.bellako.kiwi.ui.theme.KiwiTheme
import com.bellako.kiwi.ui.theme.getResponsiveRelativeSize

@Composable
fun Kiwi_InputField(
    enabled: Boolean,
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable (() -> Unit)?,
    shouldHideInput: Boolean,
    textColor: Color,
    testTag: String
) {
    var inputFieldColor = textColor
    if (!enabled) {
        inputFieldColor = inputFieldColor.copy(alpha = 0.3F)
    }

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
            colors = OutlinedTextFieldDefaults.colors().copy(
                cursorColor = inputFieldColor,
                focusedIndicatorColor = inputFieldColor,
                focusedTextColor = inputFieldColor,
                unfocusedTextColor = inputFieldColor,
                disabledTextColor = inputFieldColor,
                disabledLabelColor = inputFieldColor,
                disabledIndicatorColor = inputFieldColor
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag(testTag),
            trailingIcon = {
                if (shouldHideInput) {
                    ShowPasswordTrailingIcon(shouldShowPassword)
                }
            },
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                fontSize = getResponsiveRelativeSize(MaterialTheme.typography.bodyMedium.fontSize.value.toInt()).sp
            )
        )
    }
}

@Composable
private fun ShowPasswordTrailingIcon(
    shouldShowPasswordState: MutableState<Boolean>
) {
    Icon(
        imageVector = if (shouldShowPasswordState.value) Icons.Default.Visibility else Icons.Default.VisibilityOff,
        tint = MaterialTheme.colorScheme.inversePrimary,
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

// -------------------------------------------------------------------------------------------------

@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Composable
fun Kiwi_InputField_Preview() {
    KiwiTheme {
        Column {
            Kiwi_InputField(
                enabled = true,
                value = "finn@thehuman.com",
                onValueChange = { },
                label = {
                    Kiwi_Label2(
                        Kiwi_TextArguments(
                            "Email",
                            color = MaterialTheme.colorScheme.inversePrimary
                        )
                    )
                },
                shouldHideInput = false,
                textColor = MaterialTheme.colorScheme.inversePrimary,
                testTag = UsersTestTags.EMAIL_FIELD,
            )

            Kiwi_Spacer()

            Kiwi_InputField(
                enabled = false,
                value = "finn@thehuman.com",
                onValueChange = { },
                label = {
                    Kiwi_Label2(
                        Kiwi_TextArguments(
                            "Email",
                            color = MaterialTheme.colorScheme.inversePrimary
                        )
                    )
                },
                shouldHideInput = false,
                textColor = MaterialTheme.colorScheme.inversePrimary,
                testTag = UsersTestTags.EMAIL_FIELD,
            )
        }
    }
}