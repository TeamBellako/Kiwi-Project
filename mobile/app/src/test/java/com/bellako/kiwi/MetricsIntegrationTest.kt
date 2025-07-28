package com.bellako.kiwi

import com.bellako.kiwi.features.metrics.model.IMetricsAPI
import com.bellako.kiwi.features.metrics.model.IMetricsViewModel
import com.bellako.kiwi.features.metrics.model.MetricsFactory
import com.bellako.kiwi.features.metrics.model.MetricsMapper
import com.bellako.kiwi.features.metrics.model.MetricsRepository
import com.bellako.kiwi.features.metrics.model.MetricsViewModel
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import retrofit2.Response

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
        whenever(api.getMetricsByDate(validMetricsDTO.date))
            .thenReturn(Response.success(null))

        val result : Result<Unit> = viewModel.createMetrics(MetricsMapper.toState(validMetricsDTO))
        assertTrue(result.isSuccess)
    }

    @Test
    fun `update valid metrics`() = runTest {
        val updatedMetricsDTO = validMetricsDTO.copy(steps = validMetricsDTO.steps + 1)
        whenever(api.updateMetrics(updatedMetricsDTO))
            .thenReturn(Response.success(Unit))
        whenever(api.getMetricsByDate(validMetricsDTO.date))
            .thenReturn(Response.success(validMetricsDTO))

        val result : Result<Unit> = viewModel.updateMetrics(MetricsMapper.toState(updatedMetricsDTO))
        assertTrue(result.isSuccess)
    }

    @Test
    fun `load valid metrics`() = runTest {
        whenever(api.getMetricsByDate(validMetricsDTO.date))
            .thenReturn(Response.success(validMetricsDTO))

        val result : Result<Unit> = viewModel.loadMetrics(validMetricsDTO.date)
        assertTrue(result.isSuccess)
    }

    @Test
    fun `load non-existing metrics`() = runTest {
        whenever(api.getMetricsByDate(validMetricsDTO.date))
            .thenReturn(Response.success(validMetricsDTO.copy(steps = 0, screenTimeSeconds = 0)))

        val result : Result<Unit> = viewModel.loadMetrics(validMetricsDTO.date)
        assertTrue(result.isSuccess)
    }
}