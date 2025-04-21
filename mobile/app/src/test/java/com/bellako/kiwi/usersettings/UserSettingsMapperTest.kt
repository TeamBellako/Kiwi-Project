package com.bellako.kiwi.usersettings
import org.junit.Assert.*
import org.junit.Test

class UserSettingsMapperTest {

    @Test
    fun `toState maps dto to state correctly`() {
        val dto = UserSettingsDto(
            email = "test@kiwi.com",
            areNotificationsEnabled = true,
            theme = UserSettingsDto.Theme.DARK
        )

        val state = dto.toState()

        assertEquals("test@kiwi.com", state.email)
        assertTrue(state.areNotificationsEnabled)
        assertEquals(UserSettingsDto.Theme.DARK, state.theme)
    }

    @Test
    fun `toDto maps state to dto correctly`() {
        val dto = UserSettingsDto(
            email = "finn@kiwi.com",
            areNotificationsEnabled = false,
            theme = UserSettingsDto.Theme.LIGHT
        )

        val state = UserSettingsState(dto)
        val result = state.toDto()

        assertEquals(dto, result)
    }

    @Test
    fun `toDto reflects changes made to state`() {
        val dto = UserSettingsDto(
            email = "jake@kiwi.com",
            areNotificationsEnabled = true,
            theme = UserSettingsDto.Theme.LIGHT
        )

        val state = UserSettingsState(dto)
        state.email = "new@kiwi.com"
        state.areNotificationsEnabled = false
        state.theme = UserSettingsDto.Theme.DARK

        val updated = state.toDto()

        assertEquals("new@kiwi.com", updated.email)
        assertFalse(updated.areNotificationsEnabled)
        assertEquals(UserSettingsDto.Theme.DARK, updated.theme)
    }
}
