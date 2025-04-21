package com.bellako.kiwi.usersettings

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeTestRule
import com.bellako.kiwi.ui.utils.TestTags

class UserSettingsScreenTestRobot(private val rule: ComposeTestRule) {
    fun enterEmail(email: String) = apply {
        rule.onNodeWithTag(TestTags.EMAIL_FIELD).performTextInput(email)
    }

    fun toggleNotifications() = apply {
        rule.onNodeWithTag(TestTags.NOTIFICATIONS_SWITCH).performClick()
    }

    fun clickTheme(theme: String) = apply {
        rule.onNodeWithTag("radio_$theme").performClick()
    }
}
