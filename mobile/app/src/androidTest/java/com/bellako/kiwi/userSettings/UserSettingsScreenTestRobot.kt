package com.bellako.kiwi.userSettings

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeTestRule
import com.bellako.kiwi.userSettings.utils.UserSettingsTestTags

class UserSettingsScreenTestRobot(private val rule: ComposeTestRule) {
    fun enterEmail(email: String)  {
        rule.onNodeWithTag(UserSettingsTestTags.EMAIL_FIELD).performTextReplacement(email)
        rule.waitForIdle()
    }

    fun toggleNotifications() = apply {
        rule.onNodeWithTag(UserSettingsTestTags.NOTIFICATIONS_SWITCH).performClick()
    }

    fun clickTheme(theme: String) = apply {
        rule.onNodeWithTag("radio_$theme").performClick()
    }
}
