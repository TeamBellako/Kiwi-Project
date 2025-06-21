package com.bellako.kiwi

import com.bellako.kiwi.features.metrics.IMetricsAPI
import com.bellako.kiwi.features.metrics.IMetricsViewModel
import com.bellako.kiwi.features.metrics.MetricsDTO
import com.bellako.kiwi.features.metrics.MetricsFactory
import com.bellako.kiwi.features.metrics.MetricsMapper
import com.bellako.kiwi.features.metrics.MetricsRepository
import com.bellako.kiwi.features.metrics.MetricsViewModel
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import retrofit2.Response
import kotlin.test.assertEquals

class MetricsIntegrationTest {
    private lateinit var api: IMetricsAPI
    private lateinit var repository: MetricsRepository
    private lateinit var viewModel: IMetricsViewModel

    private val validMetricsDTO = MetricsFactory.generateRandomValidMetricDTO()

    @Before
    fun setUp(){
        api = mock(IMetricsAPI::class.java)
        repository = MetricsRepository(api)
        viewModel = MetricsViewModel(repository)
    }

    @Test
    fun `create valid metrics`() = runTest {
        whenever(api.createMetrics(validMetricsDTO))
            .thenReturn(Response.success(Unit))
        whenever(api.getMetricsByDateAndUser(validMetricsDTO.email, validMetricsDTO.date))
            .thenReturn(Response.success(null))

        val result : Result<Unit> = viewModel.createMetrics(MetricsMapper.toState(validMetricsDTO))
        assertTrue(result.isSuccess)
    }

    @Test
    fun `update valid metrics`() = runTest {
        val updatedMetricsDTO = validMetricsDTO.copy(steps = validMetricsDTO.steps + 1)
        whenever(api.updateMetrics(updatedMetricsDTO))
            .thenReturn(Response.success(Unit))
        whenever(api.getMetricsByDateAndUser(validMetricsDTO.email, validMetricsDTO.date))
            .thenReturn(Response.success(validMetricsDTO))

        val result : Result<Unit> = viewModel.updateMetrics(MetricsMapper.toState(updatedMetricsDTO))
        assertTrue(result.isSuccess)
    }

    @Test
    fun `load valid metrics`() = runTest {
        whenever(api.getMetricsByDateAndUser(validMetricsDTO.email, validMetricsDTO.date))
            .thenReturn(Response.success(validMetricsDTO))

        val result : Result<MetricsDTO> = viewModel.loadMetrics(validMetricsDTO.email, validMetricsDTO.date)
        assertTrue(result.isSuccess)
        assertEquals(MetricsMapper.toDomain(validMetricsDTO), MetricsMapper.toDomain(result.getOrNull()!!))
    }

    @Test
    fun `load non-existing metrics`() = runTest {
        whenever(api.getMetricsByDateAndUser(validMetricsDTO.email, validMetricsDTO.date))
            .thenReturn(Response.success(null))

        val result : Result<MetricsDTO> = viewModel.loadMetrics(validMetricsDTO.email, validMetricsDTO.date)
        assertTrue(result.isSuccess)
        val expectedMetricsDTO = validMetricsDTO.copy(steps = 0, screenTimeSeconds = 0)
        assertEquals(MetricsMapper.toDomain(validMetricsDTO), MetricsMapper.toDomain(expectedMetricsDTO))
    }
}