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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.analytics.FirebaseEventNames
import com.bellako.kiwi.analytics.firebaseLogEvent
import com.bellako.kiwi.common.data.ScreenRoutes
import com.bellako.kiwi.common.data.UIState
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_FixedSizeButton
import com.bellako.kiwi.common.screens.components.Kiwi_InfoBox
import com.bellako.kiwi.common.screens.components.Kiwi_InputField
import com.bellako.kiwi.common.screens.components.Kiwi_Label2
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer
import com.bellako.kiwi.common.screens.components.LoadingModal
import com.bellako.kiwi.common.screens.modals.WIPModalScreen
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SettingsAccountScreen(
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
        SettingsAccountScreenContainer(
            usersViewModel = usersViewModel,
            settingsViewModel = settingsViewModel,
            navController = navController,
        )
    }
}

@Composable
private fun SettingsAccountScreenContainer(
    usersViewModel: IUsersViewModel,
    settingsViewModel: ISettingsViewModel,
    navController: NavController,
) {
    val usersState by usersViewModel.state.collectAsState()
    val uiState by settingsViewModel.uiState.collectAsState()
    val context = LocalContext.current

    when (uiState) {
        is UIState.Loading -> {
            LoadingModal()
        }

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
                    title = "Account",
                    navController = navController,
                )

                Kiwi_Spacer(Spacing.medium)

                usersState?.let { currentUsersState ->
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
                        color = LocalKiwiColors.current.color3A,
                        testTag = UsersTestTags.EMAIL_FIELD,
                        keyboardType = KeyboardType.Text,
                        modifier =
                            Modifier.fillMaxWidth(),
                    )

                    Kiwi_Spacer(Spacing.large)
                }

                Kiwi_FixedSizeButton(
                    horizontalMargin = Spacing.xLarge,
                    textArguments =
                        KiwiTextArguments(
                            "CHANGE GOOD/BAD APPS",
                            color = LocalKiwiColors.current.color6,
                            fontWeight = FontWeight.Bold,
                        ),
                    color = LocalKiwiColors.current.color5A,
                    onClick = { navController.navigate(ScreenRoutes.SIGNUP4_APPS) },
                )

                Kiwi_Spacer()

                Kiwi_FixedSizeButton(
                    horizontalMargin = Spacing.xLarge,
                    textArguments =
                        KiwiTextArguments(
                            "RETAKE PERSONALITY TEST",
                            color = LocalKiwiColors.current.color6,
                            fontWeight = FontWeight.Bold,
                        ),
                    color = LocalKiwiColors.current.color5A,
                    onClick = { navController.navigate(ScreenRoutes.SIGNUP3_TEST) },
                )

                Kiwi_Spacer()

                Kiwi_FixedSizeButton(
                    horizontalMargin = Spacing.xLarge,
                    textArguments =
                        KiwiTextArguments(
                            "LOG OUT",
                            color = LocalKiwiColors.current.color6,
                            fontWeight = FontWeight.Bold,
                        ),
                    color = LocalKiwiColors.current.color5A,
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
                            color = LocalKiwiColors.current.color6,
                            fontWeight = FontWeight.Bold,
                        ),
                    color = LocalKiwiColors.current.colorR1,
                    onClick = {
                        firebaseLogEvent(FirebaseEventNames.SETTINGS_RESET_PROGRESS)
                        settingsViewModel.setUiState(UIState.WIP)
                    },
                )
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
fun SettingsAccountScreen_Preview() {
    Kiwi_Theme {
        Scaffold(
            bottomBar = {
                AppBarScreen(navController = rememberNavController())
            },
            content = { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues)) {
                    SettingsAccountScreen(
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
