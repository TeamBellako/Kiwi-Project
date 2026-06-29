package com.bellako.kiwi.features.settings.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.common.data.ScreenRoutes
import com.bellako.kiwi.common.data.UIState
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_Display1
import com.bellako.kiwi.common.screens.components.Kiwi_H3
import com.bellako.kiwi.common.screens.components.Kiwi_InfoBox
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer
import com.bellako.kiwi.common.screens.components.LoadingModal
import com.bellako.kiwi.common.screens.modals.WIPModalScreen
import com.bellako.kiwi.common.tests.CommonTestTags
import com.bellako.kiwi.features.appbar.screens.AppBarScreen
import com.bellako.kiwi.features.settings.data.SettingsState
import com.bellako.kiwi.features.settings.model.ISettingsViewModel
import com.bellako.kiwi.features.settings.tests.SettingsFakeViewModel
import com.bellako.kiwi.features.settings.tests.SettingsTestTags
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight

@Composable
fun SettingsScreen(
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
            settingsViewModel,
            navController,
        )
    }
}

@Composable
private fun SettingsScreenContainer(
    settingsViewModel: ISettingsViewModel,
    navController: NavController,
) {
    val uiState by settingsViewModel.uiState.collectAsState()

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
                        .fillMaxSize()
                        .testTag(CommonTestTags.SETTINGS_SCREEN),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Kiwi_Display1(
                    arguments =
                        KiwiTextArguments(
                            text = "SETTINGS",
                            color = LocalKiwiColors.current.colorF,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.large),
                        ),
                )

                Kiwi_Spacer(Spacing.medium)

                SettingsSectionRow(
                    icon = Icons.Default.Person,
                    label = "Account",
                    testTag = SettingsTestTags.SECTION_ACCOUNT,
                    onClick = { navController.navigate(ScreenRoutes.SETTINGS_ACCOUNT) },
                )

                Kiwi_Spacer(Spacing.medium)

                SettingsSectionRow(
                    icon = Icons.Default.MusicNote,
                    label = "Audio",
                    testTag = SettingsTestTags.SECTION_AUDIO,
                    onClick = { navController.navigate(ScreenRoutes.SETTINGS_AUDIO) },
                )

                Kiwi_Spacer(Spacing.medium)

                SettingsSectionRow(
                    icon = Icons.Default.QuestionMark,
                    label = "Support",
                    testTag = SettingsTestTags.SECTION_SUPPORT,
                    onClick = { navController.navigate(ScreenRoutes.SETTINGS_SUPPORT) },
                )

                Spacer(modifier = Modifier.weight(1f))

                Kiwi_Spacer(Spacing.medium)
            }
        }
    }
}

@Composable
private fun SettingsSectionRow(
    icon: ImageVector,
    label: String,
    testTag: String,
    onClick: () -> Unit,
) {
    val kiwiColors = LocalKiwiColors.current

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(kiwiColors.color3)
                .border(
                    width = 1.dp,
                    color = kiwiColors.color4,
                    shape = RoundedCornerShape(16.dp),
                ).clickable { onClick() }
                .padding(horizontal = Spacing.medium, vertical = Spacing.large)
                .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(kiwiColors.color5A),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = kiwiColors.colorOcean,
                modifier = Modifier.size(28.dp),
            )
        }

        Kiwi_H3(
            arguments =
                KiwiTextArguments(
                    text = label,
                    color = kiwiColors.colorF,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f).padding(start = Spacing.medium),
                ),
        )

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = kiwiColors.colorOcean,
            modifier = Modifier.size(20.dp),
        )
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
