package com.bellako.kiwi.usersettings

import org.junit.Assert.*
import org.junit.Test

class UserSettingsMapperTest {
    private val validUserSettingsDto : UserSettingsDto = UserSettingsDto(
            email = "finn@thehuman.com",
            areNotificationsEnabled = false,
            theme = UserSettingsDto.Theme.LIGHT
    )

    @Test
    fun `toState maps dto to state correctly`() {
        val state = validUserSettingsDto.toState()

        assertEquals("finn@thehuman.com", state.email)
        assertFalse(state.areNotificationsEnabled)
        assertEquals(UserSettingsDto.Theme.LIGHT, state.theme)
    }

    @Test
    fun `toDto maps state to dto correctly`() {
        val state = UserSettingsState.fromDto(validUserSettingsDto)

        val result = state.toDto()

        assertEquals(validUserSettingsDto, result)
    }
}
