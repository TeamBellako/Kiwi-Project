package com.bellako.kiwi.userSettings

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bellako.kiwi.ui.utils.TestTags
import com.bellako.kiwi.userSettings.types.UserSettings
import com.bellako.kiwi.userSettings.types.UserSettingsDTO
import com.bellako.kiwi.userSettings.types.UserSettingsFactory
import com.bellako.kiwi.userSettings.types.UserSettingsState
import com.bellako.kiwi.userSettings.ui.UserSettingsScreen
import com.bellako.kiwi.userSettings.utils.UserSettingsTestFactory.invalidUserSettings
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
        state = UserSettingsFactory
            .fromDto(UserSettingsDTO(email = "initial@gmail.com", areNotificationsEnabled = false, theme = UserSettings.Theme.LIGHT))
            .getOrThrow()
            .let { UserSettingsFactory.toState(it) }

        fakeViewModel = UserSettingsFakeViewModel(state)

        composeTestRule.setContent {
            UserSettingsScreen(viewModel = fakeViewModel)
        }

        robot = UserSettingsScreenTestRobot(composeTestRule)
    }

    @Test
    fun loadingIndicator_serverErrorOnLoadSettings_showsErrorMessage() {
        fakeViewModel.simulateLoadError = true
        fakeViewModel.loadSettings()

        composeTestRule.circularProgressIndicator().assertIsNotDisplayed()
        composeTestRule.serverError().assertIsDisplayed()
    }

    @Test
    fun loadingIndicator_serverErrorOnUpdateSettings_showsErrorMessage() {
        fakeViewModel.simulateUpdateError = true

        val updatedState = UserSettingsFactory
            .fromDto(validUserSettings().copy(areNotificationsEnabled = !validUserSettings().areNotificationsEnabled))
            .getOrThrow()
            .let { UserSettingsFactory.toState(it) }

        fakeViewModel.updateSettings(updatedState)

        composeTestRule.serverError().assertIsDisplayed()
    }

    @Test
    fun emailField_renderField_showsInitialValue() {
        composeTestRule.emailField().assertTextContains("Email")
    }

    @Test
    fun emailField_enterValidInput_updatesFieldValue() {
        robot.enterEmail(validUserSettings().email)
        composeTestRule.emailField().assertTextContains(validUserSettings().email)
    }

    @Test
    fun emailField_enterInvalidInput_showsErrorMessage() {
        robot.enterEmail(invalidUserSettings().email)

        composeTestRule.fieldError().assertIsDisplayed()
    }

    @Test
    fun emailField_enterValidEmailAfterInvalidOne_hidesErrorMessage() {
        fakeViewModel.simulateUpdateError = true
        robot.enterEmail(invalidUserSettings().email)
        composeTestRule.fieldError().assertIsDisplayed()

        fakeViewModel.simulateUpdateError = false
        robot.enterEmail(validUserSettings().email)

        composeTestRule.fieldError().assertIsNotDisplayed()
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
    private fun ComposeTestRule.circularProgressIndicator() = onNodeWithTag(TestTags.CIRCULAR_PROGRESS_INDICATOR)
    private fun ComposeTestRule.fieldError() = onNodeWithTag(TestTags.FIELD_ERROR)
    private fun ComposeTestRule.serverError() = onNodeWithTag(TestTags.SERVER_ERROR)
    private fun ComposeTestRule.emailField() = onNodeWithTag(TestTags.EMAIL_FIELD)
    private fun ComposeTestRule.notificationsSwitch() = onNodeWithTag(TestTags.NOTIFICATIONS_SWITCH)
    private fun ComposeTestRule.themeRadioButtonLight() = onNodeWithTag(TestTags.RADIO_LIGHT)
    private fun ComposeTestRule.themeRadioButtonDark() = onNodeWithTag(TestTags.RADIO_DARK)
}
