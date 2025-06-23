package com.bellako.kiwi

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bellako.kiwi.services.common.CommonTestTags
import com.bellako.kiwi.ui.screens.ScreenRoutes
import com.bellako.kiwi.features.settings.SettingsFakeViewModel
import com.bellako.kiwi.features.settings.SettingsScreen
import com.bellako.kiwi.features.settings.SettingsState
import com.bellako.kiwi.features.settings.SettingsTestFactory.validSettings
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalMaterial3Api::class)
@RunWith(AndroidJUnit4::class)
class SettingsScreenTest {
    @get:Rule
    val rule = createComposeRule()

    private lateinit var fakeViewModel: SettingsFakeViewModel
    private lateinit var state: SettingsState

    @Before
    fun setUp() {
        state = SettingsState(
            email = "initial@gmail.com",
            soundVolume = 67,
            musicVolume = 33
        )

        fakeViewModel = SettingsFakeViewModel(state)

        rule.setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = ScreenRoutes.SETTINGS) {
                composable(ScreenRoutes.SETTINGS) {
                    SettingsScreen(
                        fakeViewModel,
                        rememberNavController()
                    ) {}
                }
            }
        }
    }

    @Test
    fun errorOnLoad() {
        fakeViewModel.simulateLoadError = true

        fakeViewModel.loadSettings()

        rule.onNodeWithTag(CommonTestTags.ERROR_MODAL).assertIsDisplayed()
    }

    @Test
    fun errorOnUpdate() {
        fakeViewModel.simulateUpdateError = true

        fakeViewModel.updateSettings(
            SettingsState(
                email = validSettings().email,
                soundVolume = validSettings().soundVolume,
                musicVolume = validSettings().musicVolume
            )
        )

        rule.onNodeWithTag(CommonTestTags.ERROR_MODAL).assertIsDisplayed()
    }
}
