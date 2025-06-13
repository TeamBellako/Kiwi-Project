package com.bellako.kiwi

import androidx.lifecycle.viewModelScope
import com.bellako.kiwi.services.network.HealthApiService
import com.bellako.kiwi.features.settings.ISettingsAPI
import com.bellako.kiwi.features.settings.SettingsRepository
import com.bellako.kiwi.features.settings.SettingsTestFactory.updateSettings
import com.bellako.kiwi.features.settings.SettingsTestFactory.validSettings
import com.bellako.kiwi.features.settings.SettingsViewModel
import com.bellako.kiwi.services.common.HTTPUtils.createFakeHttpException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import retrofit2.Response

@RunWith(RobolectricTestRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class SettingsIntegrationTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var api: ISettingsAPI
    private lateinit var healthApi: HealthApiService
    private lateinit var repository: SettingsRepository
    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        api = mock(ISettingsAPI::class.java)
        healthApi = mock(HealthApiService::class.java)
        repository = SettingsRepository(api, healthApi)
        viewModel = SettingsViewModel(repository, testDispatcher)
    }

    @After
    fun tearDown() {
        viewModel.viewModelScope.cancel()
        Dispatchers.resetMain()
    }

    @Test
    fun `getSettings returns success when API responds`() = runTest {
        `when`(api.getSettings()).thenReturn(validSettings())

        val result = repository.getSettings()

        Assert.assertTrue(result.isSuccess)
        Assert.assertEquals(validSettings(), result.getOrNull())
    }

    @Test
    fun `getSettings returns failure when API throws exception`() = runTest {
        `when`(api.getSettings()).thenThrow(createFakeHttpException(500))

        val result = repository.getSettings()

        Assert.assertTrue(result.isFailure)
        Assert.assertNotNull(result.exceptionOrNull())
    }

    @Test
    fun `updateSettings returns success when API completes`() = runTest {
        `when`(api.updateSettings(validSettings())).thenReturn(Unit)

        val result = repository.updateSettings(validSettings())

        Assert.assertTrue(result.isSuccess)
    }

    @Test
    fun `updateSettings returns failure when API throws exception`() = runTest {
        doThrow(createFakeHttpException(500)).`when`(api).updateSettings(validSettings())

        val result = repository.updateSettings(validSettings())

        Assert.assertTrue(result.isFailure)
        Assert.assertNotNull(result.exceptionOrNull())
    }

    @Test
    fun `loadSettings sets Settings when getSettings is successful`() = runTest {
        whenever(api.getSettings()).thenReturn(validSettings())

        viewModel.loadSettings()
        advanceUntilIdle()

        val expectedState = validSettings().toState()

        Assert.assertEquals(expectedState, viewModel.state.first())
    }

    @Test
    fun `autoSave triggers updateSettings when values change`() = runTest {
        whenever(healthApi.ping()).thenReturn(Response.success(Unit))
        whenever(api.getSettings()).thenReturn(validSettings())
        whenever(api.updateSettings(anyOrNull())).thenReturn(Unit)

        viewModel.loadSettings()
        advanceUntilIdle()

        val newState = updateSettings().toState()
        viewModel.updateSettings(newState)
        advanceUntilIdle()

        verify(api, times(1)).updateSettings(anyOrNull())
    }

    @Test
    fun `autoSave does not trigger updateSettings for same state`() = runTest {
        whenever(api.getSettings()).thenReturn(validSettings())
        whenever(api.updateSettings(anyOrNull())).thenReturn(Unit)

        viewModel.loadSettings()
        advanceUntilIdle()

        val sameState = validSettings().toState()
        viewModel.updateSettings(sameState)
        advanceUntilIdle()

        // We expect this to be called once because the initial load syncs its content with the server
        verify(api, times(1)).updateSettings(anyOrNull())
    }

    @Test
    fun `autoSave triggers updateSettings only once for rapid changes`() = runTest {
        whenever(api.getSettings()).thenReturn(validSettings())
        whenever(api.updateSettings(anyOrNull())).thenReturn(Unit)

        viewModel.loadSettings()
        advanceUntilIdle()

        viewModel.updateSettings(validSettings().toState())
        advanceTimeBy(100)
        viewModel.updateSettings(updateSettings().toState())
        advanceTimeBy(100)
        viewModel.updateSettings(validSettings().toState())
        advanceUntilIdle()

        verify(api, times(1)).updateSettings(anyOrNull())
    }
}