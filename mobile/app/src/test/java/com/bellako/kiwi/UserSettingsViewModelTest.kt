package com.bellako.kiwi

import androidx.lifecycle.viewModelScope
import com.bellako.kiwi.network.AuthRepository
import com.bellako.kiwi.userSettings.network.UserSettingsRepository
import com.bellako.kiwi.userSettings.utils.UserSettingsTestFactory.updateUserSettings
import com.bellako.kiwi.userSettings.utils.UserSettingsTestFactory.validUserSettings
import com.bellako.kiwi.userSettings.viewModel.UserSettingsViewModel
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
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.reset
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class UserSettingsViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: UserSettingsRepository
    private lateinit var viewModel: UserSettingsViewModel

    private lateinit var authRepository: AuthRepository

    private val testErrorMessage = "Error Message"

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        repository = mock(UserSettingsRepository::class.java)
        authRepository = mock(AuthRepository::class.java)
        viewModel = UserSettingsViewModel(repository, testDispatcher, authRepository)

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

        val expectedState = validUserSettings().toState()

        assertEquals(expectedState, viewModel.state.first())
        assertNull(viewModel.error.first())
    }

    @Test
    fun `loadSettings shows error message when getUserSettings results in failure`() = runTest {
        whenever(repository.getUserSettings()).thenReturn(Result.failure(RuntimeException(testErrorMessage)))

        viewModel.loadSettings()
        advanceUntilIdle()

        assertNull(viewModel.state.first())
        assertEquals("An unexpected error occurred.", viewModel.error.first())
    }

    @Test
    fun `autoSave triggers updateSettings when settings change`() = runTest(testDispatcher) {
        whenever(repository.getUserSettings()).thenReturn(Result.success(validUserSettings()))
        whenever(repository.updateUserSettings(anyOrNull())).thenReturn(Result.success(Unit))

        viewModel.loadSettings()
        advanceUntilIdle()

        val state = updateUserSettings().toState()

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

        val state = validUserSettings().toState()

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

        val state1 = validUserSettings().toState()

        val state2 = updateUserSettings().toState()

        viewModel.updateSettings(state1)
        advanceTimeBy(100)
        viewModel.updateSettings(state2)
        advanceTimeBy(100)
        viewModel.updateSettings(state1)
        advanceUntilIdle()

        verify(repository, times(1)).updateUserSettings(anyOrNull())
    }
}
