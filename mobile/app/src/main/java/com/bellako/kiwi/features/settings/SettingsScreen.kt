package com.bellako.kiwi.features.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.features.users.UsersTestTags
import com.bellako.kiwi.services.common.CommonTestTags
import com.bellako.kiwi.services.common.Logger
import com.bellako.kiwi.services.common.UIState
import com.bellako.kiwi.ui.components.Kiwi_Button
import com.bellako.kiwi.ui.components.Kiwi_H1
import com.bellako.kiwi.ui.components.Kiwi_InfoBox
import com.bellako.kiwi.ui.components.Kiwi_InputField
import com.bellako.kiwi.ui.components.Kiwi_P1
import com.bellako.kiwi.ui.components.Kiwi_Slider
import com.bellako.kiwi.ui.components.Kiwi_Spacer
import com.bellako.kiwi.ui.components.Kiwi_TextArguments
import com.bellako.kiwi.ui.modals.ErrorModal
import com.bellako.kiwi.ui.modals.LoadingModal
import com.bellako.kiwi.ui.screens.ScreenRoutes
import com.bellako.kiwi.ui.theme.KiwiTheme
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
    navController: NavController,
    onLogout: () -> Unit,
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
                color = MaterialTheme.colorScheme.error,
                testTag = SettingsTestTags.SERVER_ERROR
            )
        }

        else -> {
            SettingsFields(
                state = state,
                viewModel = viewModel,
                navController = navController,
                onLogout = onLogout,
                onChange = { lastAction.value = RetryAction.SAVE }
            )
        }
    }
}

@Composable
private fun SettingsFields(
    state: SettingsState?,
    viewModel: ISettingsViewModel,
    navController: NavController,
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
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
                .testTag(CommonTestTags.SETTINGS_SCREEN)
        ) {
            Kiwi_H1(Kiwi_TextArguments(
                "SETTINGS",
                bold = true
            ))

            Kiwi_InputField(
                enabled = false,
                value = currentState.email,
                onValueChange = {},
                label = {
                    Kiwi_P1(Kiwi_TextArguments(
                        "Email",
                        color = MaterialTheme.colorScheme.inversePrimary
                    ))
                },
                textColor = MaterialTheme.colorScheme.inversePrimary,
                testTag = UsersTestTags.EMAIL_FIELD,
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
                    "SUPPORT",
                    color = White,
                    bold = true
                ),
                { navController.navigate(ScreenRoutes.HELP) }
            )

            Kiwi_Spacer()

            Kiwi_Button(
                Kiwi_TextArguments(
                    "LOG OUT",
                    color = White,
                    bold = true
                ),
                onLogout
            )
        }
    }
}

@Preview(device = "spec:width=411dp,height=891dp,dpi=420")
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
        ) {}
    }
}
