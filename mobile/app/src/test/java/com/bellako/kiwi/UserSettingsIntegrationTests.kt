package com.bellako.kiwi

import androidx.lifecycle.viewModelScope
import com.bellako.kiwi.network.AuthRepository
import com.bellako.kiwi.network.HealthApiService
import com.bellako.kiwi.userSettings.IUserSettingsAPI
import com.bellako.kiwi.userSettings.UserSettingsRepository
import com.bellako.kiwi.userSettings.UserSettingsTestFactory.updateUserSettings
import com.bellako.kiwi.userSettings.UserSettingsTestFactory.validUserSettings
import com.bellako.kiwi.userSettings.UserSettingsViewModel
import com.bellako.kiwi.utils.HTTPUtils.createFakeHttpException
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
class UserSettingsIntegrationTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var api: IUserSettingsAPI
    private lateinit var healthApi: HealthApiService
    private lateinit var repository: UserSettingsRepository
    private lateinit var viewModel: UserSettingsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        api = mock(IUserSettingsAPI::class.java)
        healthApi = mock(HealthApiService::class.java)
        repository = UserSettingsRepository(api, healthApi)
        viewModel = UserSettingsViewModel(repository, testDispatcher)
    }

    @After
    fun tearDown() {
        viewModel.viewModelScope.cancel()
        Dispatchers.resetMain()
    }

    @Test
    fun `getUserSettings returns success when API responds`() = runTest {
        `when`(api.getUserSettings()).thenReturn(validUserSettings())

        val result = repository.getUserSettings()

        Assert.assertTrue(result.isSuccess)
        Assert.assertEquals(validUserSettings(), result.getOrNull())
    }

    @Test
    fun `getUserSettings returns failure when API throws exception`() = runTest {
        `when`(api.getUserSettings()).thenThrow(createFakeHttpException(500))

        val result = repository.getUserSettings()

        Assert.assertTrue(result.isFailure)
        Assert.assertNotNull(result.exceptionOrNull())
    }

    @Test
    fun `updateUserSettings returns success when API completes`() = runTest {
        `when`(api.updateUserSettings(validUserSettings())).thenReturn(Unit)

        val result = repository.updateUserSettings(validUserSettings())

        Assert.assertTrue(result.isSuccess)
    }

    @Test
    fun `updateUserSettings returns failure when API throws exception`() = runTest {
        doThrow(createFakeHttpException(500)).`when`(api).updateUserSettings(validUserSettings())

        val result = repository.updateUserSettings(validUserSettings())

        Assert.assertTrue(result.isFailure)
        Assert.assertNotNull(result.exceptionOrNull())
    }

    @Test
    fun `loadSettings sets UserSettings when getUserSettings is successful`() = runTest {
        whenever(api.getUserSettings()).thenReturn(validUserSettings())

        viewModel.loadSettings()
        advanceUntilIdle()

        val expectedState = validUserSettings().toState()

        Assert.assertEquals(expectedState, viewModel.state.first())
    }

    @Test
    fun `autoSave triggers updateSettings when values change`() = runTest {
        whenever(healthApi.ping()).thenReturn(Response.success(Unit))
        whenever(api.getUserSettings()).thenReturn(validUserSettings())
        whenever(api.updateUserSettings(anyOrNull())).thenReturn(Unit)

        viewModel.loadSettings()
        advanceUntilIdle()

        val newState = updateUserSettings().toState()
        viewModel.updateSettings(newState)
        advanceUntilIdle()

        verify(api, times(1)).updateUserSettings(anyOrNull())
    }

    @Test
    fun `autoSave does not trigger updateSettings for same state`() = runTest {
        whenever(api.getUserSettings()).thenReturn(validUserSettings())
        whenever(api.updateUserSettings(anyOrNull())).thenReturn(Unit)

        viewModel.loadSettings()
        advanceUntilIdle()

        val sameState = validUserSettings().toState()
        viewModel.updateSettings(sameState)
        advanceUntilIdle()

        // We expect this to be called once because the initial load syncs its content with the server
        verify(api, times(1)).updateUserSettings(anyOrNull())
    }

    @Test
    fun `autoSave triggers updateSettings only once for rapid changes`() = runTest {
        whenever(api.getUserSettings()).thenReturn(validUserSettings())
        whenever(api.updateUserSettings(anyOrNull())).thenReturn(Unit)

        viewModel.loadSettings()
        advanceUntilIdle()

        viewModel.updateSettings(validUserSettings().toState())
        advanceTimeBy(100)
        viewModel.updateSettings(updateUserSettings().toState())
        advanceTimeBy(100)
        viewModel.updateSettings(validUserSettings().toState())
        advanceUntilIdle()

        verify(api, times(1)).updateUserSettings(anyOrNull())
    }
}