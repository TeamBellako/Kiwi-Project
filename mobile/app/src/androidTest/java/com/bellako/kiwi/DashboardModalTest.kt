package com.bellako.kiwi

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bellako.kiwi.features.metrics.MetricsDTO
import com.bellako.kiwi.features.metrics.MetricsFactory
import com.bellako.kiwi.features.metrics.MetricsFakeViewModel
import com.bellako.kiwi.features.metrics.MetricsMapper
import com.bellako.kiwi.features.metrics.MetricsState
import com.bellako.kiwi.features.metrics.MetricsUtils
import com.bellako.kiwi.ui.modals.DashboardModal
import com.bellako.kiwi.ui.tags.DashboardModalTestTags
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class DashboardModalTest {
    @get:Rule
    val rule = createComposeRule()

    private lateinit var fakeViewModel: MetricsFakeViewModel
    private lateinit var state: MetricsState

    private lateinit var futureMetricsDTO: MetricsDTO
    private lateinit var todayMetricsDTO: MetricsDTO
    private lateinit var pastMetricsDTO: MetricsDTO

    @Before
    fun setUp() {
        val todayLocalDate = LocalDate.now()
        todayMetricsDTO = MetricsFactory.generateRandomValidMetricDTO().copy(date = todayLocalDate.toString())
        pastMetricsDTO = MetricsFactory.generateRandomValidMetricDTO().copy(date = todayLocalDate.minusDays(1).toString())
        futureMetricsDTO = todayMetricsDTO.copy(date = todayLocalDate.plusDays(1).toString(), steps = 0, screenTimeSeconds = 0)

        state = MetricsMapper.toState(todayMetricsDTO.copy(steps = 0, screenTimeSeconds = 0))
        fakeViewModel = MetricsFakeViewModel(
            state,
            todayMetricsDTO,
            pastMetricsDTO,
            futureMetricsDTO
        )

        rule.setContent {
            DashboardModal(fakeViewModel)
        }
    }

    @Test
    fun loadTodayMetrics() {
        rule.onNodeWithTag(DashboardModalTestTags.STEPS)
            .assertTextEquals(todayMetricsDTO.steps.toString())
        rule.onNodeWithTag(DashboardModalTestTags.SCREEN_TIME)
            .assertTextEquals(MetricsUtils.parseScreenTimeSeconds(todayMetricsDTO.screenTimeSeconds))
    }

    @Test
    fun loadPastMetrics() {
        rule.onNodeWithTag(DashboardModalTestTags.STEPS)
            .assertTextEquals(todayMetricsDTO.steps.toString())
        rule.onNodeWithTag(DashboardModalTestTags.SCREEN_TIME)
            .assertTextEquals(MetricsUtils.parseScreenTimeSeconds(todayMetricsDTO.screenTimeSeconds))

        val yesterdayTestTag =
            DashboardModalTestTags.DAY_INDICATOR_PREFIX + MetricsUtils.getDayOfWeekNumber(
                LocalDate.now().minusDays(1)
            ).toString()
        rule.onNodeWithTag(yesterdayTestTag).performClick()

        rule.onNodeWithTag(DashboardModalTestTags.STEPS)
            .assertTextEquals(pastMetricsDTO.steps.toString())
        rule.onNodeWithTag(DashboardModalTestTags.SCREEN_TIME)
            .assertTextEquals(MetricsUtils.parseScreenTimeSeconds(pastMetricsDTO.screenTimeSeconds))
    }

    @Test
    fun loadFutureMetrics() {
        rule.onNodeWithTag(DashboardModalTestTags.STEPS)
            .assertTextEquals(todayMetricsDTO.steps.toString())
        rule.onNodeWithTag(DashboardModalTestTags.SCREEN_TIME)
            .assertTextEquals(MetricsUtils.parseScreenTimeSeconds(todayMetricsDTO.screenTimeSeconds))

        val tomorrowTestTag =
            DashboardModalTestTags.DAY_INDICATOR_PREFIX + MetricsUtils.getDayOfWeekNumber(
                LocalDate.now().plusDays(1)
            ).toString()
        rule.onNodeWithTag(tomorrowTestTag).performClick()

        rule.onNodeWithTag(DashboardModalTestTags.STEPS)
            .assertTextEquals(futureMetricsDTO.steps.toString())
        rule.onNodeWithTag(DashboardModalTestTags.SCREEN_TIME)
            .assertTextEquals(MetricsUtils.parseScreenTimeSeconds(futureMetricsDTO.screenTimeSeconds))
    }
}