package com.bellako.kiwi.usersettings

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.lifecycle.viewModelScope
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bellako.kiwi.ui.utils.TestTags
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
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
    fun loadingIndicator_loadingInProgress_showsCircularProgressIndicator() {
        fakeViewModel.infiniteLoading = true
        fakeViewModel.loadSettings()

        composeTestRule.circularProgressIndicator().assertIsDisplayed()
        composeTestRule.serverError().assertIsNotDisplayed()
    }

    @Test
    fun loadingIndicator_serverError_showsErrorMessage() {
        val errorMessage = "Server error with sensible information"
        fakeViewModel.simulateError = true
        fakeViewModel.simulatedErrorMessage = errorMessage
        fakeViewModel.loadSettings()

        composeTestRule.circularProgressIndicator().assertIsNotDisplayed()
        composeTestRule.serverError().assertIsDisplayed()
        // We don't want any server error to be exposed here for security reasons
        composeTestRule.onNodeWithText(errorMessage).assertDoesNotExist()
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
        val errorMessage : String = "Server error"
        fakeViewModel.simulateError = true
        fakeViewModel.simulatedErrorMessage = errorMessage

        robot.enterEmail(inValidUserSettings.email)

        composeTestRule.fieldError()
            .assertIsDisplayed()
            .assertTextContains(errorMessage)
    }

    @Test
    fun emailField_enterValidEmailAfterInvalidOne_hidesErrorMessage() {
        fakeViewModel.simulateError = true
        robot.enterEmail(inValidUserSettings.email)
        composeTestRule.fieldError().assertIsDisplayed()

        fakeViewModel.simulateError = false
        robot.enterEmail(validUserSettings.email)

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

    private fun ComposeTestRule.circularProgressIndicator() = onNodeWithTag(TestTags.CIRCULAR_PROGRESS_INDICATOR)
    private fun ComposeTestRule.fieldError() = onNodeWithTag(TestTags.FIELD_ERROR)
    private fun ComposeTestRule.serverError() = onNodeWithTag(TestTags.SERVER_ERROR)
    private fun ComposeTestRule.emailField() = onNodeWithTag(TestTags.EMAIL_FIELD)
    private fun ComposeTestRule.notificationsSwitch() = onNodeWithTag(TestTags.NOTIFICATIONS_SWITCH)
    private fun ComposeTestRule.themeRadioButtonLight() = onNodeWithTag(TestTags.RADIO_LIGHT)
    private fun ComposeTestRule.themeRadioButtonDark() = onNodeWithTag(TestTags.RADIO_DARK)
}
