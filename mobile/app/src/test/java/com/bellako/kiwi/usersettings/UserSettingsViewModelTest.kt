package com.bellako.kiwi.usersettings

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.reset
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class UserSettingsViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: UserSettingsRepository
    private lateinit var viewModel: UserSettingsViewModel

    private val testDto = UserSettingsDto(
        email = "finn@thehuman.com",
        areNotificationsEnabled = true,
        theme = UserSettingsDto.Theme.DARK
    )
    private val updateDto = UserSettingsDto(
        email = "jake@thedog.com",
        areNotificationsEnabled = false,
        theme = UserSettingsDto.Theme.LIGHT
    )
    private val invalidDto = UserSettingsDto(
        email = "bmolovesfootball.com",
        areNotificationsEnabled = false,
        theme = UserSettingsDto.Theme.LIGHT
    )

    private val testErrorMessage = "Error Message";


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
        whenever(repository.getUserSettings()).thenReturn(Result.success(testDto))

        viewModel.loadSettings()
        advanceUntilIdle()

        assertEquals(testDto, viewModel.state.first()?.toDto())
        assertNull(viewModel.error.first())
    }

    @Test
    fun `loadSettings shows error message when getUserSettings results in failure`() = runTest {
        whenever(repository.getUserSettings()).thenReturn(Result.failure(RuntimeException(testErrorMessage)))

        viewModel.loadSettings()
        advanceUntilIdle()

        assertNull(viewModel.state.first())
        assertEquals(testErrorMessage, viewModel.error.first())
    }

    @Test
    fun `updateSettings calls repository with current state`() = runTest {
        whenever(repository.getUserSettings()).thenReturn(Result.success(testDto))
        whenever(repository.updateUserSettings(eq(testDto))).thenReturn(Result.success(Unit))

        viewModel.loadSettings()
        advanceUntilIdle()
        viewModel.updateSettings(testDto)
        advanceUntilIdle()

        verify(repository).updateUserSettings(eq(testDto))
    }

    @Test
    fun `updateSettings sets error when updateUserSettings results in failure`() = runTest(testDispatcher) {
        val errorMessage : String = "Invalid email format"
        whenever(repository.getUserSettings()).thenReturn(Result.success(testDto))
        whenever(repository.updateUserSettings(invalidDto))
            .thenReturn(Result.failure(RuntimeException(errorMessage)))

        viewModel.loadSettings()
        advanceUntilIdle()
        viewModel.updateSettings(invalidDto)
        advanceUntilIdle()

        assertEquals(errorMessage, viewModel.error.value)
    }

    @Test
    fun `autoSave triggers updateSettings when settings change`() = runTest(testDispatcher) {
        whenever(repository.getUserSettings()).thenReturn(Result.success(testDto))
        whenever(repository.updateUserSettings(anyOrNull())).thenReturn(Result.success(Unit))

        viewModel.loadSettings()
        advanceUntilIdle()
        viewModel.updateSettings(updateDto)
        advanceUntilIdle()

        verify(repository, times(1)).updateUserSettings(anyOrNull())
    }

    @Test
    fun `autoSave does not call updateSettings if value is unchanged`() = runTest(testDispatcher) {
        whenever(repository.getUserSettings()).thenReturn(Result.success(testDto))
        whenever(repository.updateUserSettings(anyOrNull())).thenReturn(Result.success(Unit))

        viewModel.loadSettings()
        advanceUntilIdle()
        viewModel.updateSettings(testDto)
        advanceUntilIdle()
        viewModel.updateSettings(testDto)
        advanceUntilIdle()

        verify(repository, times(1)).updateUserSettings(anyOrNull())
    }

    @Test
    fun `autoSave triggers updateSettings only once after rapid changes`() = runTest(testDispatcher) {
        whenever(repository.getUserSettings()).thenReturn(Result.success(testDto))
        whenever(repository.updateUserSettings(anyOrNull())).thenReturn(Result.success(Unit))

        viewModel.loadSettings()
        advanceUntilIdle()
        viewModel.updateSettings(testDto)
        advanceTimeBy(100)
        advanceUntilIdle()
        viewModel.updateSettings(updateDto)
        advanceTimeBy(100)
        advanceUntilIdle()
        viewModel.updateSettings(testDto)
        advanceUntilIdle()

        verify(repository, times(1)).updateUserSettings(anyOrNull())
    }
}
