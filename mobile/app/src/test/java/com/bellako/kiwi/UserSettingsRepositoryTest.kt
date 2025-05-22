package com.bellako.kiwi

import com.bellako.kiwi.userSettings.network.IUserSettingsAPI
import com.bellako.kiwi.userSettings.network.UserSettingsRepository
import com.bellako.kiwi.userSettings.utils.UserSettingsTestFactory.validUserSettings
import com.bellako.kiwi.utils.HTTPUtils.createFakeHttpException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

class UserSettingsRepositoryTest {

    private lateinit var api: IUserSettingsAPI
    private lateinit var repository: UserSettingsRepository

    @Before
    fun setUp() {
        api = mock(IUserSettingsAPI::class.java)
        repository = UserSettingsRepository(api)
    }

    @Test
    fun `getUserSettings returns success when API responds`() = runTest {
        `when`(api.getUserSettings()).thenReturn(validUserSettings())

        val result = repository.getUserSettings()

        assertTrue(result.isSuccess)
        assertEquals(validUserSettings(), result.getOrNull())
    }

    @Test
    fun `getUserSettings returns failure when API throws any exception`() = runTest {
        `when`(api.getUserSettings()).thenThrow(createFakeHttpException(500))

        val result = repository.getUserSettings()

        assertTrue(result.isFailure)
        assertNotNull(result.exceptionOrNull())
    }

    @Test
    fun `updateUserSettings returns success when API completes`() = runTest {
        `when`(api.getUserSettings()).thenReturn(validUserSettings())

        val result = repository.updateUserSettings(validUserSettings())

        assertTrue(result.isSuccess)
    }

    @Test
    fun `updateUserSettings returns failure when API throws any exception`() = runTest {
        doThrow(createFakeHttpException(500)).`when`(api).updateUserSettings(validUserSettings())

        val result = repository.updateUserSettings(validUserSettings())

        assertTrue(result.isFailure)
        assertNotNull(result.exceptionOrNull())
    }
}
