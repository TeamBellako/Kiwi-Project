package com.bellako.kiwi

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bellako.kiwi.ui.tags.KIWI_LOADING_INDICATOR
import com.bellako.kiwi.userSettings.utils.UserSettingsTestTags
import com.bellako.kiwi.userSettings.types.UserSettingsState
import com.bellako.kiwi.userSettings.ui.UserSettingsScreen
import com.bellako.kiwi.userSettings.utils.UserSettingsTestFactory.validUserSettings
import com.bellako.kiwi.userSettings.viewModel.UserSettingsFakeViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalMaterial3Api::class)
@RunWith(AndroidJUnit4::class)
class UserSettingsTest {
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
    fun loadingIndicator_serverErrorOnLoadSettings_showsErrorMessage() {
        fakeViewModel.simulateLoadError = true
        fakeViewModel.loadSettings()

        composeTestRule.onNodeWithTag(KIWI_LOADING_INDICATOR).assertIsNotDisplayed()
        composeTestRule.serverError().assertIsDisplayed()
    }

    @Test
    fun loadingIndicator_serverErrorOnUpdateSettings_showsErrorMessage() {
        fakeViewModel.simulateUpdateError = true

        val updatedState = UserSettingsState(
            email = validUserSettings().email,
            soundVolume = validUserSettings().soundVolume,
            musicVolume = validUserSettings().musicVolume
        )

        fakeViewModel.updateSettings(updatedState)

        composeTestRule.serverError().assertIsDisplayed()
    }

    @Test
    fun emailField_renderField_showsInitialValue() {
        composeTestRule.emailField().assertTextContains("Email")
    }

    // Helper extensions

    private fun ComposeTestRule.serverError() = onNodeWithTag(UserSettingsTestTags.SERVER_ERROR)
    private fun ComposeTestRule.emailField() = onNodeWithTag(UserSettingsTestTags.EMAIL_FIELD)
}
