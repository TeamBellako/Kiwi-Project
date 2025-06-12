package com.bellako.kiwi

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bellako.kiwi.common.CommonTestTags
import com.bellako.kiwi.ui.ScreenRoutes
import com.bellako.kiwi.userSettings.UserSettingsFakeViewModel
import com.bellako.kiwi.userSettings.UserSettingsScreen
import com.bellako.kiwi.userSettings.UserSettingsState
import com.bellako.kiwi.userSettings.UserSettingsTestFactory.validUserSettings
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalMaterial3Api::class)
@RunWith(AndroidJUnit4::class)
class UserSettingsUITest {
    @get:Rule
    val rule = createComposeRule()

    private lateinit var fakeViewModel: UserSettingsFakeViewModel
    private lateinit var state: UserSettingsState

    @Before
    fun setUp() {
        state = UserSettingsState(
            email = "initial@gmail.com",
            soundVolume = 67,
            musicVolume = 33
        )

        fakeViewModel = UserSettingsFakeViewModel(state)

        rule.setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = ScreenRoutes.SETTINGS) {
                composable(ScreenRoutes.SETTINGS) {
                    UserSettingsScreen(
                        fakeViewModel,
                        rememberNavController()
                    )
                }
            }
        }
    }

    @Test
    fun errorOnLoad() {
        fakeViewModel.simulateLoadError = true

        fakeViewModel.loadSettings()

        rule.onNodeWithTag(CommonTestTags.ERROR_SCREEN).assertIsDisplayed()
    }

    @Test
    fun errorOnUpdate() {
        fakeViewModel.simulateUpdateError = true

        fakeViewModel.updateSettings(
            UserSettingsState(
                email = validUserSettings().email,
                soundVolume = validUserSettings().soundVolume,
                musicVolume = validUserSettings().musicVolume
            )
        )

        rule.onNodeWithTag(CommonTestTags.ERROR_SCREEN).assertIsDisplayed()
    }
}
