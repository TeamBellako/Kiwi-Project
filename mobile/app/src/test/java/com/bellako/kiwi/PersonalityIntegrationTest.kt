package com.bellako.kiwi

import com.bellako.kiwi.features.personality.model.IPersonalityAPI
import com.bellako.kiwi.features.personality.model.IPersonalityViewModel
import com.bellako.kiwi.features.personality.model.PersonalityRepository
import com.bellako.kiwi.features.personality.tests.PersonalityTestFactory.validPersonalityBuildDTO
import com.bellako.kiwi.features.personality.tests.PersonalityTestFactory.validPersonalityDTO
import com.bellako.kiwi.features.personality.tests.PersonalityTestFactory.validPersonalityKnightNameDTO
import com.bellako.kiwi.features.personality.tests.PersonalityTestFactory.validPersonalityRealNameDTO
import com.bellako.kiwi.features.personality.model.PersonalityViewModel
import com.bellako.kiwi.common.model.HealthApiService
import junit.framework.TestCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import kotlin.test.Test

@RunWith(RobolectricTestRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class PersonalityIntegrationTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var api: IPersonalityAPI
    private lateinit var repository: PersonalityRepository
    private lateinit var viewModel: IPersonalityViewModel
    private lateinit var healthApi: HealthApiService

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        api = mock(IPersonalityAPI::class.java)
        healthApi = mock(HealthApiService::class.java)
        repository = PersonalityRepository(api, healthApi)
        viewModel = PersonalityViewModel(repository)
    }

    @Test
    fun `load personality`() = runTest {
        whenever(api.getPersonality()).thenReturn(validPersonalityDTO())

        viewModel.loadPersonality()
        TestCase.assertEquals(viewModel.state.value?.realName, validPersonalityDTO().realName)
        TestCase.assertEquals(viewModel.state.value?.knightName, validPersonalityDTO().knightName)
        TestCase.assertEquals(viewModel.state.value?.build, validPersonalityDTO().build)
    }

    @Test
    fun `update personality real name`() = runTest {
        `when`(api.updateRealName(validPersonalityRealNameDTO())).thenReturn(Unit)

        val result = repository.updateRealName(validPersonalityRealNameDTO())

        Assert.assertTrue(result.isSuccess)
    }

    @Test
    fun `update personality knight name`() = runTest {
        `when`(api.updateKnightName(validPersonalityRealNameDTO())).thenReturn(Unit)

        val result = repository.updateKnightName(validPersonalityKnightNameDTO())

        Assert.assertTrue(result.isSuccess)
    }

    @Test
    fun `update personality build`() = runTest {
        `when`(api.updateBuild(validPersonalityBuildDTO())).thenReturn(Unit)

        val result = repository.updateBuild(validPersonalityBuildDTO())

        Assert.assertTrue(result.isSuccess)
    }

}