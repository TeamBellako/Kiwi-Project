package com.bellako.kiwi

import com.bellako.kiwi.common.utils.HTTPUtils.createFakeHttpException
import com.bellako.kiwi.features.metrics.data.MetricsDataMapper
import com.bellako.kiwi.features.metrics.model.IMetricsAPI
import com.bellako.kiwi.features.metrics.model.IMetricsViewModel
import com.bellako.kiwi.features.metrics.model.MetricsFactory
import com.bellako.kiwi.features.metrics.model.MetricsRepository
import com.bellako.kiwi.features.metrics.model.MetricsViewModel
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.whenever

class MetricsIntegrationTest {
    private lateinit var api: IMetricsAPI
    private lateinit var repository: MetricsRepository
    private lateinit var viewModel: IMetricsViewModel

    private val validMetricsDTO = MetricsFactory.generateRandomValidMetricDTO()

    @Before
    fun setUp() {
        api = mock(IMetricsAPI::class.java)
        repository = MetricsRepository(api)
        viewModel = MetricsViewModel(repository)
    }

    @Test
    fun `create valid metrics`() =
        runTest {
            whenever(api.createMetrics(any()))
                .thenReturn(validMetricsDTO)

            val result: Result<Unit> = viewModel.createMetrics(MetricsDataMapper.toState(validMetricsDTO))
            assertTrue(result.isSuccess)
        }

    @Test
    fun `update valid metrics`() =
        runTest {
            val updatedMetricsDTO = validMetricsDTO.copy(currentGoodTimeSeconds = validMetricsDTO.currentGoodTimeSeconds + 1)
            whenever(api.updateMetrics(updatedMetricsDTO))
                .thenReturn(updatedMetricsDTO)

            val result: Result<Unit> = viewModel.updateMetrics(MetricsDataMapper.toState(updatedMetricsDTO))
            assertTrue(result.isSuccess)
        }

    @Test
    fun `load valid metrics`() =
        runTest {
            whenever(api.getMetricsByDate(validMetricsDTO.date))
                .thenReturn(validMetricsDTO)

            val result: Result<Unit> = viewModel.loadMetrics(validMetricsDTO.date)
            assertTrue(result.isSuccess)
        }

    @Test
    fun `load non-existing metrics`() =
        runTest {
            doThrow(createFakeHttpException(404)).whenever(api).getMetricsByDate(any())

            val result: Result<Unit> = viewModel.loadMetrics(validMetricsDTO.date)
            assertTrue(result.isFailure)
        }
}
