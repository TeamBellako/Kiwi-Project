package com.bellako.kiwi

import androidx.lifecycle.viewModelScope
import com.bellako.kiwi.common.utils.HTTPUtils.createFakeHttpException
import com.bellako.kiwi.features.settings.data.SettingsDataMapper
import com.bellako.kiwi.features.settings.model.ISettingsAPI
import com.bellako.kiwi.features.settings.model.SettingsRepository
import com.bellako.kiwi.features.settings.model.SettingsViewModel
import com.bellako.kiwi.features.settings.tests.SettingsTestFactory.updatedSettings
import com.bellako.kiwi.features.settings.tests.SettingsTestFactory.validSettings
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
import org.junit.Assert
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mockito.doThrow
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.`when`
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import kotlin.test.Test

@RunWith(RobolectricTestRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class SettingsIntegrationTest {
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var api: ISettingsAPI
    private lateinit var repository: SettingsRepository
    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        api = mock(ISettingsAPI::class.java)
        repository = SettingsRepository(api)
        viewModel = SettingsViewModel(repository)
    }

    @After
    fun tearDown() {
        viewModel.viewModelScope.cancel()
        Dispatchers.resetMain()
    }

    @Test
    fun `getSettings returns success when API responds`() =
        runTest {
            `when`(api.getSettings()).thenReturn(validSettings())

            val result = repository.getSettings()

            Assert.assertTrue(result.isSuccess)
            Assert.assertEquals(validSettings(), result.getOrNull())
        }

    @Test
    fun `getSettings returns failure when API throws exception`() =
        runTest {
            `when`(api.getSettings()).thenThrow(createFakeHttpException(500))

            val result = repository.getSettings()

            Assert.assertTrue(result.isFailure)
            Assert.assertNotNull(result.exceptionOrNull())
        }

    @Test
    fun `updateSettings returns success when API completes`() =
        runTest {
            `when`(api.updateSettings(validSettings())).thenReturn(validSettings())

            val result = repository.updateSettings(validSettings())

            Assert.assertTrue(result.isSuccess)
        }

    @Test
    fun `updateSettings returns failure when API throws exception`() =
        runTest {
            doThrow(createFakeHttpException(500)).`when`(api).updateSettings(validSettings())

            val result = repository.updateSettings(validSettings())

            Assert.assertTrue(result.isFailure)
            Assert.assertNotNull(result.exceptionOrNull())
        }

    @Test
    fun `loadSettings sets Settings when getSettings is successful`() =
        runTest {
            whenever(api.getSettings()).thenReturn(validSettings())

            viewModel.loadSettings()
            advanceUntilIdle()

            val expectedState = SettingsDataMapper.toState(validSettings())

            Assert.assertEquals(expectedState, viewModel.state.first())
        }

    @Test
    fun `autoSave triggers updateSettings when values change`() =
        runTest {
            whenever(api.getSettings()).thenReturn(validSettings())
            whenever(api.updateSettings(anyOrNull())).thenReturn(updatedSettings())

            viewModel.loadSettings()
            advanceUntilIdle()

            val newState = SettingsDataMapper.toState(updatedSettings())
            viewModel.updateSettings(newState)
            advanceUntilIdle()

            verify(api, times(1)).updateSettings(anyOrNull())
        }

    @Test
    fun `autoSave does not trigger updateSettings for same state`() =
        runTest {
            whenever(api.getSettings()).thenReturn(validSettings())
            whenever(api.updateSettings(anyOrNull())).thenReturn(validSettings())

            viewModel.loadSettings()
            advanceUntilIdle()

            val sameState = SettingsDataMapper.toState(validSettings())
            viewModel.updateSettings(sameState)
            advanceUntilIdle()

            // We expect this to be called once because the initial load syncs its content with the server
            verify(api, times(1)).updateSettings(anyOrNull())
        }

    @Test
    fun `autoSave triggers updateSettings only once for rapid changes`() =
        runTest {
            whenever(api.getSettings()).thenReturn(validSettings())
            whenever(api.updateSettings(anyOrNull())).thenReturn(validSettings())

            viewModel.loadSettings()
            advanceUntilIdle()

            val state = SettingsDataMapper.toState(validSettings())
            viewModel.updateSettings(state)
            advanceTimeBy(100)
            viewModel.updateSettings(state)
            advanceTimeBy(100)
            viewModel.updateSettings(state)
            advanceUntilIdle()

            verify(api, times(1)).updateSettings(anyOrNull())
        }
}
