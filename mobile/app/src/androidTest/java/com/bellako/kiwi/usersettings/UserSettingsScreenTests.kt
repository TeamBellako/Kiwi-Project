package com.bellako.kiwi.usersettings

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalMaterial3Api::class)
@RunWith(AndroidJUnit4::class)
class UserSettingsScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun emailField_updatesOnInput() {
        composeTestRule.setContent {
            UserSettingsScreen()
        }

        val emailInput = "finnthehuman@gmail.com"

        composeTestRule
            .onNodeWithText("Email")
            .performTextInput(emailInput)

        composeTestRule
            .onNodeWithText(emailInput)
            .assertExists()
    }

    @Test
    fun notificationSwitch_togglesOnClick() {
        composeTestRule.setContent {
            UserSettingsScreen()
        }

        composeTestRule
            .onNode(isToggleable())
            .performClick()
            .assertIsOn()
    }

    @Test
    fun themeRadioButton_selectsDarkTheme() {
        composeTestRule.setContent {
            UserSettingsScreen()
        }

        composeTestRule.onNodeWithTag("radio_dark").performClick()
        composeTestRule.onNodeWithTag("radio_dark").assertIsSelected()
    }
}
