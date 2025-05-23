package com.bellako.kiwi

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bellako.kiwi.ui.tags.KIWI_LOADING_INDICATOR
import com.bellako.kiwi.userSettings.UserSettingsTestTags
import com.bellako.kiwi.userSettings.UserSettingsState
import com.bellako.kiwi.userSettings.UserSettingsScreen
import com.bellako.kiwi.userSettings.UserSettingsTestFactory.validUserSettings
import com.bellako.kiwi.userSettings.UserSettingsFakeViewModel
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
            UserSettingsScreen(viewModel = fakeViewModel, {})
        }

    }

    @Test
    fun serverErrorOnLoad() {
        fakeViewModel.simulateLoadError = true
        fakeViewModel.loadSettings()

        composeTestRule.onNodeWithTag(KIWI_LOADING_INDICATOR).assertIsNotDisplayed()
        composeTestRule.serverError().assertIsDisplayed()
    }

    @Test
    fun serverErrorOnUpdate() {
        fakeViewModel.simulateUpdateError = true

        val updatedState = UserSettingsState(
            email = validUserSettings().email,
            soundVolume = validUserSettings().soundVolume,
            musicVolume = validUserSettings().musicVolume
        )

        fakeViewModel.updateSettings(updatedState)

        composeTestRule.serverError().assertIsDisplayed()
    }

    private fun ComposeTestRule.serverError() = onNodeWithTag(UserSettingsTestTags.SERVER_ERROR)
}
