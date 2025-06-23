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
    private lateinit var fakeNewMetricsDTO: MetricsDTO
    private lateinit var fakePastMetricsDTO: MetricsDTO

    @Before
    fun setUp() {
        state = MetricsMapper.toState(MetricsFactory.generateRandomValidMetricDTO())

        fakeNewMetricsDTO = MetricsFactory.generateRandomValidMetricDTO()
        fakePastMetricsDTO = MetricsFactory.generateRandomValidMetricDTO()

        fakeViewModel = MetricsFakeViewModel(
            state,
            fakeNewMetricsDTO,
            fakePastMetricsDTO
        )
    }

    @Test
    fun loadTodayMetrics() {
        fakeViewModel.fakeNonExistingMetrics = true

        rule.setContent {
            DashboardModal(fakeViewModel)
        }

        // TODO: Day indicator

        rule.onNodeWithTag(DashboardModalTestTags.STEPS)
            .assertTextEquals(fakeNewMetricsDTO.steps.toString())
        rule.onNodeWithTag(DashboardModalTestTags.SCREEN_TIME)
            .assertTextEquals(MetricsUtils.parseScreenTimeSeconds(fakeNewMetricsDTO.screenTimeSeconds))
    }

    @Test
    fun loadPastMetrics() {
        rule.setContent {
            DashboardModal(fakeViewModel)
        }

        val yesterdayTestTag =
            DashboardModalTestTags.DAY_INDICATOR_PREFIX + MetricsUtils.getDayOfWeekNumber(
                LocalDate.now().minusDays(1)
            ).toString()
        rule.onNodeWithTag(yesterdayTestTag).performClick()

        // TODO: Day indicator

        rule.onNodeWithTag(DashboardModalTestTags.STEPS)
            .assertTextEquals(fakePastMetricsDTO.steps.toString())
        rule.onNodeWithTag(DashboardModalTestTags.SCREEN_TIME)
            .assertTextEquals(MetricsUtils.parseScreenTimeSeconds(fakePastMetricsDTO.screenTimeSeconds))
    }



    @Test
    fun loadFutureMetrics() {
        fakeViewModel.fakeNonExistingMetrics = true

        rule.setContent {
            DashboardModal(fakeViewModel)
        }

        val tomorrowTestTag =
            DashboardModalTestTags.DAY_INDICATOR_PREFIX + MetricsUtils.getDayOfWeekNumber(
                LocalDate.now().plusDays(1)
            ).toString()
        rule.onNodeWithTag(tomorrowTestTag).performClick()

        // TODO: Day indicator

        // We check that a default metrics entry has been created
        rule.onNodeWithTag(DashboardModalTestTags.STEPS)
            .assertTextEquals("0")
        rule.onNodeWithTag(DashboardModalTestTags.SCREEN_TIME)
            .assertTextEquals("0h 0min")
    }
}