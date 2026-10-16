package com.bellako.kiwi.features.settings.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.common.AppLanguage
import com.bellako.kiwi.common.AppLanguageManager
import com.bellako.kiwi.common.data.UIState
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_H3
import com.bellako.kiwi.common.screens.components.Kiwi_InfoBox
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer
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
fun SettingsLanguageScreen(
    navController: NavController,
    settingsViewModel: ISettingsViewModel,
) {
    val context = LocalContext.current
    val kiwiColors = LocalKiwiColors.current
    val selectedLanguage by AppLanguageManager.language.collectAsState()
    val uiState by settingsViewModel.uiState.collectAsState()

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(kiwiColors.color2)
                .padding(getResponsiveSizeHeight(Spacing.medium)),
        verticalArrangement = Arrangement.Top,
    ) {
        SettingsSubScreenHeader(
            title = "language",
            navController = navController,
        )

        Kiwi_Spacer(Spacing.medium)

        if (uiState is UIState.Error) {
            Kiwi_InfoBox(
                message = (uiState as UIState.Error).message,
                color = MaterialTheme.colorScheme.error,
                testTag = SettingsTestTags.SERVER_ERROR,
            )
            Kiwi_Spacer(Spacing.medium)
        }

        AppLanguage.entries.forEach { language ->
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(kiwiColors.color3)
                        .border(1.dp, kiwiColors.color4, RoundedCornerShape(16.dp))
                        .clickable {
                            if (language != selectedLanguage) {
                                settingsViewModel.updateLanguagePreference(language.locale.language) {
                                    AppLanguageManager.setLanguage(
                                        context,
                                        language,
                                        recreateActivity = false,
                                    )
                                    AppLanguageManager.restartActivity(context)
                                }
                            }
                        }.padding(horizontal = Spacing.medium, vertical = Spacing.medium),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                androidx.compose.material3.Text(
                    text = language.flag,
                    fontSize = 28.sp,
                )
                Kiwi_H3(
                    KiwiTextArguments(
                        text = language.labelKey,
                        color = kiwiColors.colorF,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f).padding(start = Spacing.medium),
                    ),
                )
                RadioButton(
                    selected = language == selectedLanguage,
                    onClick = null,
                    colors = RadioButtonDefaults.colors(selectedColor = kiwiColors.colorOcean),
                )
            }

            Kiwi_Spacer(Spacing.medium)
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Composable
fun SettingsLanguageScreen_Preview() {
    Kiwi_Theme {
        Scaffold(
            bottomBar = {
                AppBarScreen(navController = rememberNavController())
            },
        ) { paddingValues ->
            androidx.compose.foundation.layout.Box(modifier = Modifier.padding(paddingValues)) {
                SettingsLanguageScreen(
                    navController = rememberNavController(),
                    settingsViewModel = SettingsFakeViewModel(SettingsState()),
                )
            }
        }
    }
}
