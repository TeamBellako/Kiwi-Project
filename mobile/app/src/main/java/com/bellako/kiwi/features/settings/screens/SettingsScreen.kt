package com.bellako.kiwi.features.settings.screens

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.AndroidRuntimeException
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.BuildConfig
import com.bellako.kiwi.analytics.FirebaseEventNames
import com.bellako.kiwi.analytics.firebaseLogEvent
import com.bellako.kiwi.common.data.ScreenRoutes
import com.bellako.kiwi.common.data.UIState
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_FixedSizeButton
import com.bellako.kiwi.common.screens.components.Kiwi_H2
import com.bellako.kiwi.common.screens.components.Kiwi_InfoBox
import com.bellako.kiwi.common.screens.components.Kiwi_InputField
import com.bellako.kiwi.common.screens.components.Kiwi_Label2
import com.bellako.kiwi.common.screens.components.Kiwi_Slider
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer
import com.bellako.kiwi.common.screens.components.LoadingModal
import com.bellako.kiwi.common.screens.modals.WIPModalScreen
import com.bellako.kiwi.common.tests.CommonTestTags
import com.bellako.kiwi.common.utils.Logger.warn
import com.bellako.kiwi.features.appbar.screens.AppBarScreen
import com.bellako.kiwi.features.settings.data.SettingsState
import com.bellako.kiwi.features.settings.model.ISettingsViewModel
import com.bellako.kiwi.features.settings.tests.SettingsFakeViewModel
import com.bellako.kiwi.features.settings.tests.SettingsTestTags
import com.bellako.kiwi.features.users.data.UsersState
import com.bellako.kiwi.features.users.model.IUsersViewModel
import com.bellako.kiwi.features.users.tests.UsersFakeViewModel
import com.bellako.kiwi.features.users.tests.UsersTestFactory.validUsersDTO
import com.bellako.kiwi.features.users.tests.UsersTestTags
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import com.bellako.kiwi.ui.getResponsiveSizeWidth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    usersViewModel: IUsersViewModel,
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
        SettingsScreenContainer(
            usersViewModel,
            settingsViewModel,
            navController,
        )
    }
}

@Composable
private fun SettingsScreenContainer(
    usersViewModel: IUsersViewModel,
    settingsViewModel: ISettingsViewModel,
    navController: NavController,
) {
    val usersState by usersViewModel.state.collectAsState()
    val settingsState by settingsViewModel.state.collectAsState()
    val uiState by settingsViewModel.uiState.collectAsState()

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
            WIPModalScreen(onButtonClick = {
                settingsViewModel.resetUiState()
            })
        }
        else -> {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .testTag(CommonTestTags.SETTINGS_SCREEN),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                SettingsInfoFields(
                    usersState = usersState,
                )
                SettingsEditFields(
                    settingsState = settingsState,
                    settingsViewModel = settingsViewModel,
                )
                SettingsButtons(
                    usersViewModel = usersViewModel,
                    settingsViewModel = settingsViewModel,
                    navController = navController,
                )
            }
        }
    }
}

@Composable
private fun SettingsInfoFields(usersState: UsersState?) {
    val kiwiColors = LocalKiwiColors.current
    usersState?.let { currentUsersState ->

        Kiwi_H2(
            KiwiTextArguments(
                "SETTINGS",
                fontWeight = FontWeight.Bold,
            ),
        )

        Kiwi_Spacer()

        Kiwi_InputField(
            enabled = false,
            value = currentUsersState.email,
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
            color = kiwiColors.color3A,
            testTag = UsersTestTags.EMAIL_FIELD,
            keyboardType = KeyboardType.Text,
            modifier =
                Modifier
                    .clickable {
                        firebaseLogEvent(FirebaseEventNames.SETTINGS_CLICK_ON_EMAIL)
                    },
        )

        Kiwi_Spacer(Spacing.large)
    }
}

@Composable
private fun SettingsEditFields(
    settingsState: SettingsState?,
    settingsViewModel: ISettingsViewModel,
) {
    val kiwiColors = LocalKiwiColors.current

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
                        .padding(getResponsiveSizeWidth(Spacing.medium), 0.dp)
                        .fillMaxWidth(),
                color = kiwiColors.color6,
            ),
            value = soundSliderPosition,
            onValueChange = { newValue ->
                soundSliderPosition = newValue
                CoroutineScope(Dispatchers.Main).launch {
                    settingsViewModel.updateSettings(currentSettingsState.copy(soundVolume = newValue))
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
                CoroutineScope(Dispatchers.Main).launch {
                    settingsViewModel.updateSettings(currentSettingsState.copy(musicVolume = newValue))
                }
            },
            valueRange = 0f..1f,
            steps = 0,
            testTag = SettingsTestTags.MUSIC_VOLUME_SLIDER,
        )
    }
}

@Composable
private fun SettingsButtons(
    usersViewModel: IUsersViewModel,
    settingsViewModel: ISettingsViewModel,
    navController: NavController,
) {
    val context = LocalContext.current
    val kiwiColors = LocalKiwiColors.current

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(bottom = getResponsiveSizeHeight(Spacing.medium)),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .testTag(CommonTestTags.SETTINGS_SCREEN),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Kiwi_FixedSizeButton(
                horizontalMargin = Spacing.xLarge,
                textArguments =
                    KiwiTextArguments(
                        "CHANGE GOOD/BAD APPS",
                        color = kiwiColors.color6,
                        fontWeight = FontWeight.Bold,
                    ),
                color = kiwiColors.color5A,
                onClick = { navController.navigate(ScreenRoutes.SIGNUP4_APPS) },
            )

            Kiwi_Spacer()

            Kiwi_FixedSizeButton(
                horizontalMargin = Spacing.xLarge,
                textArguments =
                    KiwiTextArguments(
                        "CONTACT US",
                        color = kiwiColors.color6,
                        fontWeight = FontWeight.Bold,
                    ),
                color = kiwiColors.color5A,
                onClick = { openLinkInBrowser(context, BuildConfig.CONCIERGE_FORM_LINK) },
            )

            Kiwi_FixedSizeButton(
                horizontalMargin = Spacing.xLarge,
                textArguments =
                    KiwiTextArguments(
                        "REPORT A BUG",
                        color = kiwiColors.color6,
                        fontWeight = FontWeight.Bold,
                    ),
                color = kiwiColors.color5A,
                onClick = { openLinkInBrowser(context, BuildConfig.BUG_FORM_LINK) },
            )

            Kiwi_Spacer()

            Kiwi_FixedSizeButton(
                horizontalMargin = Spacing.xLarge,
                textArguments =
                    KiwiTextArguments(
                        "LOG OUT",
                        color = kiwiColors.color6,
                        fontWeight = FontWeight.Bold,
                    ),
                color = kiwiColors.color5A,
                onClick = {
                    CoroutineScope(Dispatchers.Main).launch {
                        usersViewModel.logout(context)
                        navController.navigate(ScreenRoutes.LOGIN) {
                            popUpTo(ScreenRoutes.LOGIN) { inclusive = true }
                        }
                    }
                },
            )

            Kiwi_Spacer()

            Kiwi_FixedSizeButton(
                horizontalMargin = Spacing.xLarge,
                textArguments =
                    KiwiTextArguments(
                        "RESET PROGRESS",
                        color = kiwiColors.color6,
                        fontWeight = FontWeight.Bold,
                    ),
                color = kiwiColors.colorR1,
                onClick = {
                    firebaseLogEvent(FirebaseEventNames.SETTINGS_RESET_PROGRESS)
                    settingsViewModel.setUiState(UIState.WIP)
                },
            )
        }
    }
}

private fun openLinkInBrowser(
    context: Context,
    url: String,
) {
    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

    try {
        context.startActivity(browserIntent)
    } catch (e: ActivityNotFoundException) {
        warn("No browser app found: ${e.message}")
    } catch (e: AndroidRuntimeException) {
        warn("Runtime error: ${e.message}")
    }
}

// -------------------------------------------------------------------------------------------------

@SuppressLint("ViewModelConstructorInComposable")
@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Composable
fun SettingsScreen_Preview() {
    Kiwi_Theme {
        Scaffold(
            bottomBar = {
                AppBarScreen(navController = rememberNavController())
            },
            content = { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues)) {
                    SettingsScreen(
                        usersViewModel =
                            UsersFakeViewModel(
                                UsersState(
                                    validUsersDTO().email,
                                    validUsersDTO().password,
                                    validUsersDTO().registerDate,
                                ),
                            ),
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
