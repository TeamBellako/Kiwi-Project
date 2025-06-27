package com.bellako.kiwi

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.isNotDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipe
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bellako.kiwi.features.metrics.MetricsDTO
import com.bellako.kiwi.features.metrics.MetricsFactory
import com.bellako.kiwi.features.metrics.MetricsFakeViewModel
import com.bellako.kiwi.features.metrics.MetricsMapper
import com.bellako.kiwi.features.metrics.MetricsState
import com.bellako.kiwi.features.metrics.MetricsUtils
import com.bellako.kiwi.ui.modals.DashboardModal
import com.bellako.kiwi.ui.modals.DashboardModalState
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
    }

    @Test
    fun loadTodayMetrics() {
        rule.setContent {
            DashboardModal(
                fakeViewModel,
                DashboardModalState.EXPANDED
            )
        }

        rule.onNodeWithTag(DashboardModalTestTags.STEPS)
            .assertTextEquals(todayMetricsDTO.steps.toString())
        rule.onNodeWithTag(DashboardModalTestTags.SCREEN_TIME)
            .assertTextEquals(MetricsUtils.parseScreenTimeSeconds(todayMetricsDTO.screenTimeSeconds))
    }

    @Test
    fun loadPastMetrics() {
        rule.setContent {
            DashboardModal(
                fakeViewModel,
                DashboardModalState.EXPANDED
            )
        }

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
        rule.setContent {
            DashboardModal(
                fakeViewModel,
                DashboardModalState.EXPANDED
            )
        }

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

    @Test
    fun dragFromHiddenToCollapsed() {
        val screenHeightDpState = setContentAndGetScreenHeight(DashboardModalState.HIDDEN)
        rule.onNodeWithTag(DashboardModalTestTags.STEPS)
            .isNotDisplayed()

        rule.swipeDashboardModal(
            fromState = DashboardModalState.HIDDEN,
            toState = DashboardModalState.COLLAPSED,
            screenHeightDp = screenHeightDpState
        )

        rule.onNodeWithTag(DashboardModalTestTags.STEPS)
            .isDisplayed()
    }

    @Test
    fun dragFromHiddenToExpanded() {
        val screenHeightDpState = setContentAndGetScreenHeight(DashboardModalState.HIDDEN)
        rule.onNodeWithTag(DashboardModalTestTags.DAY_INDICATOR_PREFIX + "0")
            .isNotDisplayed()

        rule.swipeDashboardModal(
            fromState = DashboardModalState.HIDDEN,
            toState = DashboardModalState.EXPANDED,
            screenHeightDp = screenHeightDpState
        )

        rule.onNodeWithTag(DashboardModalTestTags.DAY_INDICATOR_PREFIX + "0")
            .isDisplayed()
    }

    @Test
    fun dragFromCollapsedToExpanded() {
        val screenHeightDpState = setContentAndGetScreenHeight(DashboardModalState.COLLAPSED)

        rule.onNodeWithTag(DashboardModalTestTags.DAY_INDICATOR_PREFIX + "0")
            .isNotDisplayed()

        rule.swipeDashboardModal(
            fromState = DashboardModalState.COLLAPSED,
            toState = DashboardModalState.EXPANDED,
            screenHeightDp = screenHeightDpState
        )

        rule.onNodeWithTag(DashboardModalTestTags.DAY_INDICATOR_PREFIX + "0")
            .isDisplayed()
    }

    private fun setContentAndGetScreenHeight(
        dashboardModalState: DashboardModalState
    ) : Int {
        val screenHeightDpState = mutableStateOf(0)

        rule.setContent {
            val config = LocalConfiguration.current
            screenHeightDpState.value = config.screenHeightDp

            DashboardModal(
                fakeViewModel,
                dashboardModalState
            )
        }

        rule.waitUntil {
            screenHeightDpState.value > 0
        }
        return screenHeightDpState.value
    }

    private fun ComposeTestRule.swipeDashboardModal(
        fromState: DashboardModalState,
        toState: DashboardModalState,
        screenHeightDp: Int
    ) {
        val density = Density(1f, 1f)
        val hidden = with(density) { 150.dp.toPx() }
        val collapsed = with(density) { 300.dp.toPx() }
        val expanded = with(density) { screenHeightDp.dp.toPx() }

        val anchorMap = mapOf(
            DashboardModalState.HIDDEN to hidden,
            DashboardModalState.COLLAPSED to collapsed,
            DashboardModalState.EXPANDED to expanded
        )

        val fromY = with(density) { screenHeightDp.dp.toPx() - anchorMap[fromState]!! }
        val toY = with(density) { screenHeightDp.dp.toPx() - anchorMap[toState]!! }
        val dragDistance = fromY - toY

        onNodeWithTag(DashboardModalTestTags.DRAGGABLE_NODE)
            .performTouchInput {
                swipe(
                    start = center,
                    end = Offset(center.x, center.y - dragDistance),
                    durationMillis = 300
                )
            }
    }



}