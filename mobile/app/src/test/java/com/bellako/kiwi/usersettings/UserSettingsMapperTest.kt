package com.bellako.kiwi.usersettings

import org.junit.Assert.*
import org.junit.Test

class UserSettingsMapperTest {
    private val validUserSettingsDto : UserSettingsDto = UserSettingsDto(
            email = "finnthehuman@gmail.com",
            areNotificationsEnabled = false,
            theme = UserSettingsDto.Theme.LIGHT
    )

    @Test
    fun `toState maps dto to state correctly`() {
        val state = validUserSettingsDto.toState()

        assertEquals("finnthehuman@gmail.com", state.email)
        assertFalse(state.areNotificationsEnabled)
        assertEquals(UserSettingsDto.Theme.LIGHT, state.theme)
    }

    @Test
    fun `toDto maps state to dto correctly`() {
        val state = UserSettingsState(validUserSettingsDto)

        val result = state.toDto()

        assertEquals(validUserSettingsDto, result)
    }

    @Test
    fun `toDto reflects changes made to state`() {
        val state = UserSettingsState(validUserSettingsDto)

        state.email = "jakethedog@gmail.com"
        state.areNotificationsEnabled = true
        state.theme = UserSettingsDto.Theme.DARK
        val updated = state.toDto()

        assertEquals("jakethedog@gmail.com", updated.email)
        assertTrue(updated.areNotificationsEnabled)
        assertEquals(UserSettingsDto.Theme.DARK, updated.theme)
    }
}
