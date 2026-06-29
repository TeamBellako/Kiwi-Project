package com.bellako.kiwi.features.settings.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.common.data.UIState
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_InfoBox
import com.bellako.kiwi.common.screens.components.Kiwi_Slider
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer
import com.bellako.kiwi.common.screens.components.LoadingModal
import com.bellako.kiwi.common.screens.modals.WIPModalScreen
import com.bellako.kiwi.features.appbar.screens.AppBarScreen
import com.bellako.kiwi.features.settings.data.SettingsState
import com.bellako.kiwi.features.settings.model.ISettingsViewModel
import com.bellako.kiwi.features.settings.tests.SettingsFakeViewModel
import com.bellako.kiwi.features.settings.tests.SettingsTestTags
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SettingsAudioScreen(
    settingsViewModel: ISettingsViewModel,
    navController: NavController,
) {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(LocalKiwiColors.current.color2)
                .padding(getResponsiveSizeHeight(Spacing.medium)),
    ) {
        SettingsAudioScreenContainer(
            settingsViewModel = settingsViewModel,
            navController = navController,
        )
    }
}

@Composable
private fun SettingsAudioScreenContainer(
    settingsViewModel: ISettingsViewModel,
    navController: NavController,
) {
    val settingsState by settingsViewModel.state.collectAsState()
    val uiState by settingsViewModel.uiState.collectAsState()
    val kiwiColors = LocalKiwiColors.current
    val coroutineScope = rememberCoroutineScope()

    when (uiState) {
        is UIState.Loading -> LoadingModal()
        is UIState.Error -> {
            Kiwi_InfoBox(
                message = (uiState as UIState.Error).message,
                color = MaterialTheme.colorScheme.error,
                testTag = SettingsTestTags.SERVER_ERROR,
            )
        }
        is UIState.WIP -> {
            WIPModalScreen(navController = navController) {
                settingsViewModel.resetUiState()
            }
        }
        else -> {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                SettingsSubScreenHeader(
                    title = "Audio",
                    navController = navController,
                )

                Kiwi_Spacer(Spacing.medium)

                settingsState?.let { currentSettingsState ->
                    var soundSliderPosition by remember {
                        mutableFloatStateOf(currentSettingsState.soundVolume.coerceIn(0f, 1f))
                    }
                    var musicSliderPosition by remember {
                        mutableFloatStateOf(currentSettingsState.musicVolume.coerceIn(0f, 1f))
                    }

                    Kiwi_Slider(
                        KiwiTextArguments(
                            "SFX Volume",
                            modifier =
                                Modifier
                                    .padding(getResponsiveSizeHeight(Spacing.medium), 0.dp)
                                    .fillMaxWidth(),
                            color = kiwiColors.color6,
                        ),
                        value = soundSliderPosition,
                        onValueChange = { newValue ->
                            soundSliderPosition = newValue
                            coroutineScope.launch(Dispatchers.Main.immediate) {
                                settingsViewModel.updateSettings(
                                    currentSettingsState.copy(soundVolume = newValue),
                                )
                            }
                        },
                        valueRange = 0f..1f,
                        steps = 0,
                        testTag = SettingsTestTags.SOUND_VOLUME_SLIDER,
                    )

                    Kiwi_Spacer()

                    Kiwi_Slider(
                        KiwiTextArguments(
                            "Music Volume",
                            modifier =
                                Modifier
                                    .padding(getResponsiveSizeHeight(Spacing.medium), 0.dp)
                                    .fillMaxWidth(),
                            color = kiwiColors.color6,
                        ),
                        value = musicSliderPosition,
                        onValueChange = { newValue ->
                            musicSliderPosition = newValue
                            coroutineScope.launch(Dispatchers.Main.immediate) {
                                settingsViewModel.updateSettings(
                                    currentSettingsState.copy(musicVolume = newValue),
                                )
                            }
                        },
                        valueRange = 0f..1f,
                        steps = 0,
                        testTag = SettingsTestTags.MUSIC_VOLUME_SLIDER,
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------

@SuppressLint("ViewModelConstructorInComposable")
@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Composable
fun SettingsAudioScreen_Preview() {
    Kiwi_Theme {
        Scaffold(
            bottomBar = {
                AppBarScreen(navController = rememberNavController())
            },
            content = { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues)) {
                    SettingsAudioScreen(
                        settingsViewModel =
                            SettingsFakeViewModel(
                                SettingsState(
                                    soundVolume = 0.67f,
                                    musicVolume = 0.33f,
                                ),
                            ),
                        navController = rememberNavController(),
                    )
                }
            },
        )
    }
}

