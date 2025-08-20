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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.common.data.UIState
import com.bellako.kiwi.common.screens.ScreenRoutes
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_Button
import com.bellako.kiwi.common.screens.components.Kiwi_H2
import com.bellako.kiwi.common.screens.components.Kiwi_InfoBox
import com.bellako.kiwi.common.screens.components.Kiwi_InputField
import com.bellako.kiwi.common.screens.components.Kiwi_Label2
import com.bellako.kiwi.common.screens.components.Kiwi_Slider
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer
import com.bellako.kiwi.common.screens.components.LoadingModal
import com.bellako.kiwi.common.tests.CommonTestTags
import com.bellako.kiwi.features.settings.data.SettingsState
import com.bellako.kiwi.features.settings.model.ISettingsViewModel
import com.bellako.kiwi.features.settings.tests.SettingsFakeViewModel
import com.bellako.kiwi.features.settings.tests.SettingsTestTags
import com.bellako.kiwi.features.users.tests.UsersTestTags
import com.bellako.kiwi.ui.KiwiTheme
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    viewModel: ISettingsViewModel,
    navController: NavController,
    onLogout: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(getResponsiveSizeHeight(Spacing.medium)),
    ) {
        SettingsScreenLayout(
            viewModel,
            navController,
            onLogout,
        )
    }
}

@Composable
private fun SettingsScreenLayout(
    viewModel: ISettingsViewModel,
    navController: NavController,
    onLogout: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    when (uiState) {
        is UIState.Loading -> LoadingModal()
        is UIState.Error -> {
            Kiwi_InfoBox(
                message = (uiState as UIState.Error).message,
                color = MaterialTheme.colorScheme.error,
                testTag = SettingsTestTags.SERVER_ERROR,
            )
        }
        else -> {
            SettingsFields(
                state = state,
                viewModel = viewModel,
                navController = navController,
                onLogout = onLogout,
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
) {
    state?.let { currentState ->
        var soundSliderPosition by remember {
            mutableFloatStateOf(currentState.soundVolume.coerceIn(0f, 1f))
        }
        var musicSliderPosition by remember {
            mutableFloatStateOf(currentState.musicVolume.coerceIn(0f, 1f))
        }

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .testTag(CommonTestTags.SETTINGS_SCREEN),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Kiwi_H2(
                KiwiTextArguments(
                    "SETTINGS",
                    bold = true,
                ),
            )

            Kiwi_Spacer()

            Kiwi_InputField(
                enabled = false,
                value = currentState.email,
                onValueChange = {},
                label = {
                    Kiwi_Label2(
                        KiwiTextArguments(
                            "Email",
                            color = MaterialTheme.colorScheme.inversePrimary,
                        ),
                    )
                },
                textColor = MaterialTheme.colorScheme.inversePrimary,
                testTag = UsersTestTags.EMAIL_FIELD,
                shouldHideInput = false,
            )

            Kiwi_Spacer(Spacing.large)

            Kiwi_Slider(
                KiwiTextArguments("SFX Volume"),
                value = soundSliderPosition,
                onValueChange = { newValue ->
                    soundSliderPosition = newValue
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModel.updateSettings(currentState.copy(soundVolume = newValue))
                    }
                },
                valueRange = 0f..1f,
                steps = 0,
                testTag = SettingsTestTags.SOUND_VOLUME_SLIDER,
            )

            Kiwi_Spacer()

            Kiwi_Slider(
                KiwiTextArguments("Music Volume"),
                value = musicSliderPosition,
                onValueChange = { newValue ->
                    musicSliderPosition = newValue
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModel.updateSettings(currentState.copy(musicVolume = newValue))
                    }
                },
                valueRange = 0f..1f,
                steps = 0,
                testTag = SettingsTestTags.MUSIC_VOLUME_SLIDER,
            )

            Kiwi_Spacer(Spacing.large)

            Kiwi_Button(
                KiwiTextArguments(
                    "SUPPORT",
                    color = White,
                    bold = true,
                ),
                { navController.navigate(ScreenRoutes.HELP) },
            )

            Kiwi_Spacer()

            Kiwi_Button(
                KiwiTextArguments(
                    "LOG OUT",
                    color = White,
                    bold = true,
                ),
                onLogout,
            )
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Composable
fun SettingsScreenPreview() {
    val previewState =
        SettingsState(
            email = "finn@thehuman.com",
            soundVolume = 0.67f,
            musicVolume = 0.33f,
        )

    KiwiTheme {
        SettingsScreen(
            SettingsFakeViewModel(previewState),
            navController = rememberNavController(),
        ) {}
    }
}
