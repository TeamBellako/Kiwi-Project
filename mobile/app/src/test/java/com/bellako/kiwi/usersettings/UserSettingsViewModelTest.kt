package com.bellako.kiwi.usersettings

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.eq

@OptIn(ExperimentalCoroutinesApi::class)
class UserSettingsViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: UserSettingsRepository
    private lateinit var viewModel: UserSettingsViewModel

    private val testDto = UserSettingsDto(
        email = "finnthehuman@gmail.com",
        areNotificationsEnabled = true,
        theme = UserSettingsDto.Theme.DARK
    )
    private val testErrorMessage = "Error Message";


    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        repository = mock(UserSettingsRepository::class.java)
        viewModel = UserSettingsViewModel(repository)
    }


    @Test
    fun `loadSettings sets UserSettings when getUserSettings results in success`() = runTest {
        `when`(repository.getUserSettings()).thenReturn(Result.success(testDto))

        viewModel.loadSettings()
        testScheduler.advanceUntilIdle()

        assertEquals(testDto, viewModel.state.first()?.toDto())
        assertNull(viewModel.error.first())
    }

    @Test
    fun `loadSettings shows error message when getUserSettings results in failure`() = runTest {
        `when`(repository.getUserSettings()).thenReturn(Result.failure(RuntimeException(testErrorMessage)))

        viewModel.loadSettings()
        testScheduler.advanceUntilIdle()

        assertNull(viewModel.state.first())
        assertEquals(testErrorMessage, viewModel.error.first())
    }

    @Test
    fun `updateSettings calls repository with current state`() = runTest {
        `when`(repository.getUserSettings()).thenReturn(Result.success(testDto))
        `when`(repository.updateUserSettings(eq(testDto))).thenReturn(Result.success(Unit))

        viewModel.loadSettings()
        viewModel.updateSettings()
        testScheduler.advanceUntilIdle()

        verify(repository).updateUserSettings(eq(testDto))
    }

    @Test
    fun `updateSettings does nothing when state is null`() = runTest {
        viewModel.updateSettings()

        verify(repository, never()).updateUserSettings(anyOrNull())
    }
}
