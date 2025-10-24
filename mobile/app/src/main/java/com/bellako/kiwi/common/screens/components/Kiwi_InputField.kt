package com.bellako.kiwi.common.screens.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bellako.kiwi.R
import com.bellako.kiwi.features.users.tests.UsersTestFactory.validUsersDTO
import com.bellako.kiwi.features.users.tests.UsersTestTags
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.getResponsiveSizeHeight

@Composable
fun Kiwi_InputField(
    enabled: Boolean,
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable (() -> Unit)?,
    keyboardType: KeyboardType = KeyboardType.Text,
    textColor: Color,
    color: Color,
    testTag: String,
    modifier: Modifier = Modifier,
) {

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .then(modifier),
    ) {
        val shouldShowPassword = remember { mutableStateOf(false) }
        val keyboardOptions = KeyboardOptions(keyboardType = keyboardType)

        val visualTransformation =
            if (keyboardType == KeyboardType.Password && !shouldShowPassword.value) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            }

        TextField(
            value = value,
            onValueChange = onValueChange,
            label = label,
            singleLine = true,
            enabled = enabled,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            shape = RoundedCornerShape(14.dp),
            colors =
                TextFieldDefaults.colors().copy(
                    unfocusedContainerColor = color,
                    focusedContainerColor = color,
                    disabledContainerColor = color.copy(alpha = 0.3f),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    cursorColor = textColor,
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor,
                    disabledTextColor = textColor.copy(alpha = 0.3f),
                    disabledLabelColor = textColor.copy(alpha = 0.3f),
                ),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .testTag(testTag),
            trailingIcon = {
                if (keyboardType == KeyboardType.Password) {
                    ShowPasswordTrailingIcon(shouldShowPassword)
                }
            },
            textStyle =
                MaterialTheme.typography.bodyMedium.copy(
                    fontSize =
                        getResponsiveSizeHeight(
                            MaterialTheme.typography.bodyMedium.fontSize.value
                                .toInt(),
                        ).sp,
                ),
        )
    }
}

@Composable
private fun ShowPasswordTrailingIcon(shouldShowPasswordState: MutableState<Boolean>) {
    Icon(
        painter =
            if (shouldShowPasswordState.value) {
                painterResource(R.drawable.ic_eye_open)
            }
            else {
                painterResource(R.drawable.ic_eye_closed)
            },
        tint = LocalKiwiColors.current.color5A,
        contentDescription = if (shouldShowPasswordState.value) "Hide password" else "Show password",
        modifier =
            Modifier
                .size(24.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            shouldShowPasswordState.value = true
                            tryAwaitRelease()
                            shouldShowPasswordState.value = false
                        },
                    )
                },
    )
}

// -------------------------------------------------------------------------------------------------

@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Composable
fun Kiwi_InputField_Preview() {
    val kiwiColors = LocalKiwiColors.current
    Kiwi_Theme {
        Column {
            Kiwi_InputField(
                enabled = true,
                value = validUsersDTO().email,
                onValueChange = { },
                label = {
                    Kiwi_Label2(
                        KiwiTextArguments(
                            "Email",
                            color = kiwiColors.color7B,
                        ),
                    )
                },
                keyboardType = KeyboardType.Email,
                textColor = kiwiColors.color7B,
                color = kiwiColors.color3A,
                testTag = UsersTestTags.EMAIL_FIELD,
            )

            Kiwi_Spacer()

            Kiwi_InputField(
                enabled = false,
                value = validUsersDTO().email,
                onValueChange = { },
                label = {
                    Kiwi_Label2(
                        KiwiTextArguments(
                            "Email",
                            color = kiwiColors.color7B,
                        ),
                    )
                },
                keyboardType = KeyboardType.Email,
                textColor = kiwiColors.color7B,
                color = kiwiColors.color3A,
                testTag = UsersTestTags.EMAIL_FIELD,
            )
        }
    }
}
