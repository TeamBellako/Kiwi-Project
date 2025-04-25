package com.bellako.kiwi.usersettings

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bellako.kiwi.ui.utils.TestTags
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

    private lateinit var fakeViewModel: FakeUserSettingsViewModel
    private lateinit var state: UserSettingsState

    private val validUserSettings = UserSettingsDto(
        email = "finn@thehuman.com",
        areNotificationsEnabled = true,
        theme = UserSettingsDto.Theme.DARK
    )
    private val inValidUserSettings = UserSettingsDto(
        email = "bmolovesfootball.com",
        areNotificationsEnabled = true,
        theme = UserSettingsDto.Theme.DARK
    )

    @Before
    fun setUp() {
        state = UserSettingsState.fromDto(UserSettingsDto())

        fakeViewModel = FakeUserSettingsViewModel(state)

        composeTestRule.setContent {
            UserSettingsScreen(viewModel = fakeViewModel)
        }

        robot = UserSettingsScreenTestRobot(composeTestRule)

    }

    @Test
    fun emailField_renderField_showsInitialValue() {
        composeTestRule.emailField().assertTextContains("Email")
    }

    @Test
    fun emailField_enterValidInput_updatesFieldValue() {
        robot.enterEmail(validUserSettings.email)

        composeTestRule.emailField().assertTextContains(validUserSettings.email)
    }

    @Test
    fun emailField_enterInvalidInput_showsErrorMessage() {
        fakeViewModel.simulateError = true
        fakeViewModel.simulatedErrorMessage = "Invalid email format"

        robot.enterEmail(inValidUserSettings.email)

        composeTestRule.errorText().assertIsDisplayed()
        composeTestRule.errorText().assertTextContains(fakeViewModel.simulatedErrorMessage)
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

    private fun ComposeTestRule.errorText() = onNodeWithTag(TestTags.ERROR_TEXT)
    private fun ComposeTestRule.emailField() = onNodeWithTag(TestTags.EMAIL_FIELD)
    private fun ComposeTestRule.notificationsSwitch() = onNodeWithTag(TestTags.NOTIFICATIONS_SWITCH)
    private fun ComposeTestRule.themeRadioButtonLight() = onNodeWithTag(TestTags.RADIO_LIGHT)
    private fun ComposeTestRule.themeRadioButtonDark() = onNodeWithTag(TestTags.RADIO_DARK)
}
