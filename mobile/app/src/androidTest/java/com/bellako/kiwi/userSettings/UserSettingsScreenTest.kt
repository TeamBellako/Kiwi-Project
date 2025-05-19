package com.bellako.kiwi.userSettings

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bellako.kiwi.ui.tags.KIWI_LOADING_INDICATOR
import com.bellako.kiwi.userSettings.utils.UserSettingsTestTags
import com.bellako.kiwi.userSettings.types.Theme
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
class UserSettingsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var robot: UserSettingsScreenTestRobot
    private lateinit var fakeViewModel: UserSettingsFakeViewModel
    private lateinit var state: UserSettingsState

    @Before
    fun setUp() {
        state = UserSettingsState(
            email = "initial@gmail.com",
            areNotificationsEnabled = false,
            theme = Theme.LIGHT
        )

        fakeViewModel = UserSettingsFakeViewModel(state)

        composeTestRule.setContent {
            UserSettingsScreen(viewModel = fakeViewModel, {})
        }

        robot = UserSettingsScreenTestRobot(composeTestRule)
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
            areNotificationsEnabled = !validUserSettings().areNotificationsEnabled,
            theme = validUserSettings().theme
        )

        fakeViewModel.updateSettings(updatedState)

        composeTestRule.serverError().assertIsDisplayed()
    }

    @Test
    fun emailField_renderField_showsInitialValue() {
        composeTestRule.emailField().assertTextContains("Email")
    }

    @Test
    fun notificationSwitch_render_isOffByDefault() {
        composeTestRule.notificationsSwitch().assertIsOff()
    }

    @Test
    fun notificationSwitch_click_togglesOn() {
        robot.toggleNotifications()
        composeTestRule.notificationsSwitch().assertIsOn()
    }

    @Test
    fun themeRadioButton_render_isLightByDefault() {
        composeTestRule.themeRadioButtonLight().assertIsSelected()
        composeTestRule.themeRadioButtonDark().assertIsNotSelected()
    }

    @Test
    fun themeRadioButton_clickOnBothOptions_onlyAllowsToSelectOneAtATime() {
        robot.clickTheme("light")
        robot.clickTheme("dark")

        composeTestRule.themeRadioButtonLight().assertIsNotSelected()
        composeTestRule.themeRadioButtonDark().assertIsSelected()
    }

    // Helper extensions

    private fun ComposeTestRule.serverError() = onNodeWithTag(UserSettingsTestTags.SERVER_ERROR)
    private fun ComposeTestRule.emailField() = onNodeWithTag(UserSettingsTestTags.EMAIL_FIELD)
    private fun ComposeTestRule.notificationsSwitch() = onNodeWithTag(UserSettingsTestTags.NOTIFICATIONS_SWITCH)
    private fun ComposeTestRule.themeRadioButtonLight() = onNodeWithTag(UserSettingsTestTags.RADIO_LIGHT)
    private fun ComposeTestRule.themeRadioButtonDark() = onNodeWithTag(UserSettingsTestTags.RADIO_DARK)
}
