package com.bellako.kiwi

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bellako.kiwi.common.CommonTestTags
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
    val composeTestRule = createComposeRule()

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

        composeTestRule.setContent {
            UserSettingsScreen(viewModel = fakeViewModel) {}
        }

    }

    @Test
    fun errorOnLoad() {
        fakeViewModel.simulateLoadError = true

        fakeViewModel.loadSettings()

        composeTestRule.onNodeWithTag(CommonTestTags.ERROR_SCREEN).assertIsDisplayed()
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

        composeTestRule.onNodeWithTag(CommonTestTags.ERROR_SCREEN).assertIsDisplayed()
    }
}
