package com.bellako.kiwi.userSettings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bellako.kiwi.common.ErrorScreen
import com.bellako.kiwi.common.LoadingScreen
import com.bellako.kiwi.common.UIState
import com.bellako.kiwi.ui.components.Kiwi_InfoBox
import com.bellako.kiwi.ui.components.Kiwi_InputField
import com.bellako.kiwi.ui.components.Kiwi_Slider
import com.bellako.kiwi.ui.theme.KiwiTheme
import com.bellako.kiwi.ui.theme.SeparatorHeight
import com.bellako.kiwi.users.UsersTestTags
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private val volumeLevels = listOf(0, 33, 67, 100)

private enum class RetryAction {
    LOAD,
    SAVE
}

@Composable
fun UserSettingsScreen(
    viewModel: IUserSettingsViewModel,
    onLogout: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val lastAction = remember { mutableStateOf<RetryAction?>(null) }

    LaunchedEffect(Unit) {
        lastAction.value = RetryAction.LOAD
        viewModel.loadSettings()
    }

    when (uiState) {
        is UIState.Loading -> LoadingScreen()

        is UIState.GeneralError -> {
            ErrorScreen(
                onRetry = {
                    CoroutineScope(Dispatchers.Main).launch {
                        when (lastAction.value) {
                            RetryAction.LOAD -> {
                                lastAction.value = RetryAction.LOAD
                                viewModel.loadSettings()
                            }
                            RetryAction.SAVE -> {
                                viewModel.reset()

                                state?.let {
                                    lastAction.value = RetryAction.SAVE
                                    viewModel.updateSettings(it)
                                }
                            }
                            null -> {}
                        }
                    }
                }
            )
        }

        is UIState.Error -> {
            Kiwi_InfoBox(
                message = (uiState as UIState.Error).message,
                color = Color.Red,
                testTag = UserSettingsTestTags.SERVER_ERROR
            )
        }

        else -> {
            UserSettingsFields(
                state = state,
                viewModel = viewModel,
                onLogout = onLogout,
                onChange = { lastAction.value = RetryAction.SAVE }
            )
        }
    }
}

@Composable
private fun UserSettingsFields(
    state: UserSettingsState?,
    viewModel: IUserSettingsViewModel,
    onLogout: () -> Unit,
    onChange: () -> Unit
) {
    state?.let { currentState ->
        var soundSliderPosition by remember {
            mutableStateOf(volumeLevels.indexOfFirst { it >= currentState.soundVolume }.coerceAtLeast(0))
        }
        var musicSliderPosition by remember {
            mutableStateOf(volumeLevels.indexOfFirst { it >= currentState.musicVolume }.coerceAtLeast(0))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Kiwi_InputField(
                enabled = false,
                value = currentState.email,
                onValueChange = {},
                label = { Text("Email") },
                testTag = UsersTestTags.EMAIL_FIELD,
                shouldHideInput = false
            )
            Spacer(modifier = Modifier.height(SeparatorHeight))

            Kiwi_Slider(
                text = "Sound Volume",
                value = soundSliderPosition.toFloat(),
                onValueChange = { newValue ->
                    val intPos = newValue.toInt()
                    if (intPos != soundSliderPosition) {
                        soundSliderPosition = intPos
                        onChange()
                        viewModel.updateSettings(currentState.copy(soundVolume = volumeLevels[intPos]))
                    }
                },
                valueRange = 0f..3f,
                steps = 2,
                testTag = UserSettingsTestTags.SOUND_VOLUME_SLIDER
            )
            Spacer(modifier = Modifier.height(SeparatorHeight))

            Kiwi_Slider(
                text = "Music Volume",
                value = musicSliderPosition.toFloat(),
                onValueChange = { newValue ->
                    val intPos = newValue.toInt()
                    if (intPos != musicSliderPosition) {
                        musicSliderPosition = intPos
                        onChange()
                        viewModel.updateSettings(currentState.copy(musicVolume = volumeLevels[intPos]))
                    }
                },
                valueRange = 0f..3f,
                steps = 2,
                testTag = UserSettingsTestTags.MUSIC_VOLUME_SLIDER
            )
            Spacer(modifier = Modifier.height(SeparatorHeight * 2))

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

@Preview(showBackground = true)
@Composable
fun UserSettingsScreenPreview() {
    val previewState = UserSettingsState(
        email = "finn@thehuman.com",
        soundVolume = 67,
        musicVolume = 33,
    )

    KiwiTheme {
        UserSettingsScreen(UserSettingsFakeViewModel(previewState), onLogout = {})
    }
}
