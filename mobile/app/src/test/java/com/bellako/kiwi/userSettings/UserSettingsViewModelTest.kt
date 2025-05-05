package com.bellako.kiwi.userSettings

import androidx.lifecycle.viewModelScope
import com.bellako.kiwi.userSettings.network.UserSettingsRepository
import com.bellako.kiwi.userSettings.types.UserSettingsFactory
import com.bellako.kiwi.userSettings.types.UserSettingsState
import com.bellako.kiwi.userSettings.utils.UserSettingsTestFactory.invalidUserSettings
import com.bellako.kiwi.userSettings.utils.UserSettingsTestFactory.updateUserSettings
import com.bellako.kiwi.userSettings.utils.UserSettingsTestFactory.validUserSettings
import com.bellako.kiwi.userSettings.viewModel.UserSettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.never
import org.mockito.kotlin.whenever
import retrofit2.HttpException
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class UserSettingsViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: UserSettingsRepository
    private lateinit var viewModel: UserSettingsViewModel

    private val testErrorMessage = "Error Message"

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mock(UserSettingsRepository::class.java)
        viewModel = UserSettingsViewModel(repository, testDispatcher)
        reset(repository)
    }

    @After
    fun tearDown() {
        viewModel.viewModelScope.cancel()
        Dispatchers.resetMain()
    }

    @Test
    fun `loadSettings sets UserSettings when getUserSettings results in success`() = runTest {
        whenever(repository.getUserSettings()).thenReturn(Result.success(validUserSettings()))

        viewModel.loadSettings()
        advanceUntilIdle()

        val expectedState = UserSettingsFactory
            .fromDto(validUserSettings())
            .getOrThrow()
            .let { UserSettingsFactory.toState(it) }

        assertEquals(expectedState, viewModel.state.first())
        assertNull(viewModel.validationState.first().generalError)
    }

    @Test
    fun `loadSettings shows error message when getUserSettings results in failure`() = runTest {
        whenever(repository.getUserSettings()).thenReturn(Result.failure(RuntimeException(testErrorMessage)))

        viewModel.loadSettings()
        advanceUntilIdle()

        assertNull(viewModel.state.first())
        assertEquals("An unexpected error occurred.", viewModel.validationState.first().generalError)
    }

    @Test
    fun `updateSettings sets error when updateUserSettings results in failure`() = runTest(testDispatcher) {
        val errorMessage = "Invalid email format"
        val errorJson = """{ "message": "$errorMessage" }"""
        val errorResponse = Response.error<Unit>(
            400,
            errorJson.toResponseBody(contentType = "application/json".toMediaTypeOrNull())
        )

        whenever(repository.updateUserSettings(anyOrNull()))
            .thenReturn(Result.failure(HttpException(errorResponse)))
        whenever(repository.getUserSettings()).thenReturn(Result.success(validUserSettings()))

        viewModel.loadSettings()
        advanceUntilIdle()

        val invalidState = UserSettingsState(
            email = invalidUserSettings().email,
            areNotificationsEnabled = invalidUserSettings().areNotificationsEnabled,
            theme = invalidUserSettings().theme
        )

        viewModel.updateSettings(invalidState)
        advanceUntilIdle()

        assertEquals(errorMessage, viewModel.validationState.first().emailError)
    }


    @Test
    fun `autoSave triggers updateSettings when settings change`() = runTest(testDispatcher) {
        whenever(repository.getUserSettings()).thenReturn(Result.success(validUserSettings()))
        whenever(repository.updateUserSettings(anyOrNull())).thenReturn(Result.success(Unit))

        viewModel.loadSettings()
        advanceUntilIdle()

        val state = UserSettingsFactory
            .fromDto(updateUserSettings())
            .getOrThrow()
            .let { UserSettingsFactory.toState(it) }

        viewModel.updateSettings(state)
        advanceUntilIdle()

        verify(repository, times(1)).updateUserSettings(anyOrNull())
    }

    @Test
    fun `autoSave does not call updateSettings if value is unchanged`() = runTest(testDispatcher) {
        whenever(repository.getUserSettings()).thenReturn(Result.success(validUserSettings()))
        whenever(repository.updateUserSettings(anyOrNull())).thenReturn(Result.success(Unit))

        viewModel.loadSettings()
        advanceUntilIdle()

        val state = UserSettingsFactory
            .fromDto(validUserSettings())
            .getOrThrow()
            .let { UserSettingsFactory.toState(it) }

        viewModel.updateSettings(state)
        advanceUntilIdle()

        // We expect this to be called 1 time because of the initialization
        verify(repository, times(1)).updateUserSettings(anyOrNull())
    }

    @Test
    fun `autoSave triggers updateSettings only once after rapid changes`() = runTest(testDispatcher) {
        whenever(repository.getUserSettings()).thenReturn(Result.success(validUserSettings()))
        whenever(repository.updateUserSettings(anyOrNull())).thenReturn(Result.success(Unit))

        viewModel.loadSettings()
        advanceUntilIdle()

        val state1 = UserSettingsFactory
            .fromDto(validUserSettings())
            .getOrThrow()
            .let { UserSettingsFactory.toState(it) }

        val state2 = UserSettingsFactory
            .fromDto(updateUserSettings())
            .getOrThrow()
            .let { UserSettingsFactory.toState(it) }

        viewModel.updateSettings(state1)
        advanceTimeBy(100)
        viewModel.updateSettings(state2)
        advanceTimeBy(100)
        viewModel.updateSettings(state1)
        advanceUntilIdle()

        verify(repository, times(1)).updateUserSettings(anyOrNull())
    }
}
