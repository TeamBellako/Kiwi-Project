package com.bellako.kiwi.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.common.CommonTestTags
import com.bellako.kiwi.modals.ErrorModal
import com.bellako.kiwi.modals.LoadingModal
import com.bellako.kiwi.common.UIState
import com.bellako.kiwi.common.ScreenRoutes
import com.bellako.kiwi.ui.components.Kiwi_Button
import com.bellako.kiwi.ui.components.Kiwi_InfoBox
import com.bellako.kiwi.ui.components.Kiwi_InputField
import com.bellako.kiwi.ui.components.Kiwi_Slider
import com.bellako.kiwi.ui.components.Kiwi_Spacer
import com.bellako.kiwi.ui.components.Kiwi_TextArguments
import com.bellako.kiwi.ui.theme.KiwiTheme
import com.bellako.kiwi.login.LoginTestTags
import com.bellako.kiwi.utils.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private val volumeLevels = listOf(0, 33, 67, 100)

private enum class RetryAction {
    LOAD,
    SAVE
}

@Composable
fun SettingsScreen(
    viewModel: ISettingsViewModel,
    navController: NavController
) {
    val state by viewModel.state.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val lastAction = remember { mutableStateOf<RetryAction?>(null) }

    LaunchedEffect(Unit) {
        lastAction.value = RetryAction.LOAD
        Logger.info("Retry action set to " + lastAction.value.toString())

        viewModel.loadSettings()
    }

    when (uiState) {
        is UIState.Loading -> LoadingModal()

        is UIState.GeneralError -> {
            ErrorModal(
                onRetry = {
                    CoroutineScope(Dispatchers.Main).launch {
                        when (lastAction.value) {
                            RetryAction.LOAD -> {
                                lastAction.value = RetryAction.LOAD
                                Logger.info("Retry action set to " + lastAction.value.toString())

                                viewModel.loadSettings()
                            }
                            RetryAction.SAVE -> {
                                viewModel.reset()

                                state?.let {
                                    lastAction.value = RetryAction.SAVE
                                    Logger.info("Retry action set to " + lastAction.value.toString())

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
                testTag = SettingsTestTags.SERVER_ERROR
            )
        }

        else -> {
            SettingsFields(
                state = state,
                viewModel = viewModel,
                onBackToHome = { navController.navigate(ScreenRoutes.HOME) },
                onChange = { lastAction.value = RetryAction.SAVE }
            )
        }
    }
}

@Composable
private fun SettingsFields(
    state: SettingsState?,
    viewModel: ISettingsViewModel,
    onBackToHome: () -> Unit,
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
                .testTag(CommonTestTags.SETTINGS_SCREEN)
        ) {
            Kiwi_InputField(
                enabled = false,
                value = currentState.email,
                onValueChange = {},
                label = { Text("Email") },
                testTag = LoginTestTags.EMAIL_FIELD,
                shouldHideInput = false
            )
            Kiwi_Spacer()

            Kiwi_Slider(
                Kiwi_TextArguments("Sound Volume"),
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
                testTag = SettingsTestTags.SOUND_VOLUME_SLIDER
            )
            Kiwi_Spacer()

            Kiwi_Slider(
                Kiwi_TextArguments("Music Volume"),
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
                testTag = SettingsTestTags.MUSIC_VOLUME_SLIDER
            )
            Kiwi_Spacer(2F)

            Kiwi_Button(
                Kiwi_TextArguments(
                    "Back To Home",
                    color = Color.White
                ),
                onBackToHome
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    val previewState = SettingsState(
        email = "finn@thehuman.com",
        soundVolume = 67,
        musicVolume = 33,
    )

    KiwiTheme {
        SettingsScreen(
            SettingsFakeViewModel(previewState),
            navController = rememberNavController()
        )
    }
}
