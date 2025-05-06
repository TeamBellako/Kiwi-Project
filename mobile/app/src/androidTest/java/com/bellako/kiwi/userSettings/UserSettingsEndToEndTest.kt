package com.bellako.kiwi.userSettings

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.bellako.kiwi.userSettings.ui.UserSettingsScreen
import com.bellako.kiwi.userSettings.viewModel.UserSettingsViewModel
import com.bellako.kiwi.userSettings.types.UserSettingsState
import com.bellako.kiwi.userSettings.utils.UserSettingsTestFactory.updateUserSettings
import com.bellako.kiwi.userSettings.utils.UserSettingsTestFactory.validUserSettings
import com.bellako.kiwi.utils.TestTags
import com.google.gson.Gson
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*
import org.junit.After
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@HiltAndroidTest
class UserSettingEndToEndTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createComposeRule()

    @Inject
    lateinit var viewModel: UserSettingsViewModel

    private lateinit var mockWebServer: MockWebServer

    private lateinit var state: UserSettingsState

    private lateinit var robot: UserSettingsTestRobot

    @Before
    fun setUp() {
        state = updateUserSettings().toState()

        mockWebServer = MockWebServer()
        mockWebServer.start()

        hiltRule.inject()

        composeTestRule.setContent {
            UserSettingsScreen(viewModel = viewModel)
        }

        robot = UserSettingsTestRobot(composeTestRule)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun loadUserSettings_validInput_displaysCurrentSettings() {
        val mockResponse = MockResponse().setBody(Gson().toJson(validUserSettings()))
        mockWebServer.enqueue(mockResponse)
        composeTestRule.setContent {
            UserSettingsScreen(viewModel = viewModel)
        }

        composeTestRule.onNodeWithTag(TestTags.EMAIL_FIELD).assertTextContains(validUserSettings().email)
        composeTestRule.onNodeWithTag(TestTags.NOTIFICATIONS_SWITCH).assertIsOn()
        composeTestRule.onNodeWithTag(TestTags.RADIO_DARK).assertIsSelected()
    }

    @Test
    fun updateUserSettings_validInput_sendsPUTRequest() {
        val mockResponse = MockResponse().setBody(Gson().toJson(validUserSettings()))
        mockWebServer.enqueue(mockResponse)
        composeTestRule.setContent {
            UserSettingsScreen(viewModel = viewModel)
        }

        robot.enterEmail(updateUserSettings().email)
        robot.toggleNotifications()
        robot.clickTheme("LIGHT")
        composeTestRule.waitForIdle()

        val request = mockWebServer.takeRequest()
        assertEquals("/api/settings/", request.path)
        assertTrue(request.body.readUtf8().contains(Gson().toJson(updateUserSettings())))
    }
}
