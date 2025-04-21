package com.bellako.kiwi.usersettings

import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import retrofit2.HttpException
import retrofit2.Response

class UserSettingsRepositoryTest {

    private lateinit var api: UserSettingsApi
    private lateinit var repository: UserSettingsRepository

    private val testDto = UserSettingsDto(
        email = "finnthehuman@gmail.com",
        areNotificationsEnabled = true,
        theme = UserSettingsDto.Theme.DARK
    )

    @Before
    fun setUp() {
        api = mock(UserSettingsApi::class.java)
        repository = UserSettingsRepository(api)
    }

    @Test
    fun `getUserSettings returns success when API responds`() = runTest {
        `when`(api.getUserSettings()).thenReturn(testDto)

        val result = repository.getUserSettings()

        assertTrue(result.isSuccess)
        assertEquals(testDto, result.getOrNull())
    }

    @Test
    fun `getUserSettings returns failure when API throws any exception`() = runTest {
        `when`(api.getUserSettings()).thenThrow(fakeHttpException(500))

        val result = repository.getUserSettings()

        assertTrue(result.isFailure)
        assertNotNull(result.exceptionOrNull())
    }

    @Test
    fun `updateUserSettings returns success when API completes`() = runTest {
        `when`(api.getUserSettings()).thenReturn(testDto)

        val result = repository.updateUserSettings(testDto)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `updateUserSettings returns failure when API throws any exception`() = runTest {
        doThrow(fakeHttpException(500)).`when`(api).updateUserSettings(testDto)

        val result = repository.updateUserSettings(testDto)

        assertTrue(result.isFailure)
        assertNotNull(result.exceptionOrNull())
    }

    // Helper
    private fun fakeHttpException(code: Int): HttpException {
        val response = Response.error<Any>(
            code,
            "Error $code".toResponseBody(null)
        )
        return HttpException(response)
    }
}
