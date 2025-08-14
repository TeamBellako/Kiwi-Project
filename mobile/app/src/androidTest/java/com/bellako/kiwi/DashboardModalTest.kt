package com.bellako.kiwi

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.assertTextContains
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
import com.bellako.kiwi.audio.AudioManager
import com.bellako.kiwi.features.metrics.data.MetricsDTO
import com.bellako.kiwi.features.metrics.model.MetricsFactory
import com.bellako.kiwi.features.metrics.tests.MetricsFakeViewModel
import com.bellako.kiwi.features.metrics.model.MetricsMapper
import com.bellako.kiwi.features.metrics.data.MetricsState
import com.bellako.kiwi.features.metrics.model.MetricsUtils
import com.bellako.kiwi.common.screens.modals.DashboardModal
import com.bellako.kiwi.common.tests.DashboardModalTestTags
import com.bellako.kiwi.features.personality.data.PersonalityState
import com.bellako.kiwi.features.personality.tests.PersonalityFakeViewModel
import com.bellako.kiwi.features.personality.tests.PersonalityTestFactory.validPersonalityDTO
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import com.bellako.kiwi.ui.getScreenHeight
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@RunWith(AndroidJUnit4::class)
class DashboardModalTest {
    @get:Rule
    val rule = createComposeRule()

    private lateinit var fakeMetricsViewModel: MetricsFakeViewModel
    private lateinit var metricsState: MetricsState

    private lateinit var fakePersonalityViewModel: PersonalityFakeViewModel
    private lateinit var personalityState: PersonalityState

    private lateinit var futureMetricsDTO: MetricsDTO
    private lateinit var todayMetricsDTO: MetricsDTO
    private lateinit var pastMetricsDTO: MetricsDTO

    private var screenHeightDp = 0.dp
    private val states = listOf(150, 260, 650)
    private var statesBottom: List<Float> = listOf()

    @Before
    fun setUp() {
        AudioManager.setEnabled(false)

        val todayLocalDate = LocalDate.now()
        todayMetricsDTO = MetricsDTO(
            LocalDate.now().toString(),
            6 * 60 * 60,
            1 * 60 * 60,
            6 * 60 * 60,
            2 * 60
        )
        pastMetricsDTO = MetricsFactory.generateRandomValidMetricDTO().copy(date = todayLocalDate.minusDays(1).toString())
        futureMetricsDTO = todayMetricsDTO.copy(date = todayLocalDate.plusDays(1).toString(), currentGoodTimeSeconds = 0, currentBadTimeSeconds = 0)

        metricsState = MetricsMapper.toState(todayMetricsDTO.copy(currentGoodTimeSeconds = 0, currentBadTimeSeconds = 0))
        fakeMetricsViewModel = MetricsFakeViewModel(
            metricsState,
            todayMetricsDTO,
            pastMetricsDTO,
            futureMetricsDTO
        )

        personalityState = PersonalityState(
            validPersonalityDTO().realName,
            validPersonalityDTO().knightName,
            validPersonalityDTO().build,
            validPersonalityDTO().goodApps,
            validPersonalityDTO().badApps,
        )
        fakePersonalityViewModel = PersonalityFakeViewModel(personalityState)
    }

    @Test
    fun loadPastMetrics() {
        setContent(false, 2)

        val yesterdayTestTag =
            DashboardModalTestTags.DAY_INDICATOR_PREFIX + MetricsUtils.getDayOfWeekNumber(
                LocalDate.now().minusDays(1)
            ).toString()
        rule.onNodeWithTag(yesterdayTestTag).performClick()

        rule.onNodeWithTag(DashboardModalTestTags.GOOD_TIME)
            .assertTextContains(MetricsUtils.parseTimeSeconds(pastMetricsDTO.currentGoodTimeSeconds), true)
        rule.onNodeWithTag(DashboardModalTestTags.BAD_TIME)
            .assertTextContains(MetricsUtils.parseTimeSeconds(pastMetricsDTO.currentBadTimeSeconds), true)
    }

    @Test
    fun loadFutureMetrics() {
        setContent(false, 2)

        val tomorrowTestTag =
            DashboardModalTestTags.DAY_INDICATOR_PREFIX + MetricsUtils.getDayOfWeekNumber(
                LocalDate.now().plusDays(1)
            ).toString()
        rule.onNodeWithTag(tomorrowTestTag).performClick()

        rule.onNodeWithTag(DashboardModalTestTags.GOOD_TIME)
            .assertTextContains(MetricsUtils.parseTimeSeconds(futureMetricsDTO.currentGoodTimeSeconds), true)
        rule.onNodeWithTag(DashboardModalTestTags.BAD_TIME)
            .assertTextContains(MetricsUtils.parseTimeSeconds(futureMetricsDTO.currentBadTimeSeconds), true)
    }

    @Test
    fun dragFromHiddenToCollapsed() {
        setContent(false, 0)

        rule.isInHiddenState()

        rule.swipeDashboardModal(
            fromState = 0,
            toState = 1
        )

        rule.isInCollapsedState()
    }

    @Test
    fun dragFromHiddenToExpanded() {
        setContent(false, 0)

        rule.isInHiddenState()

        rule.swipeDashboardModal(
            fromState = 0,
            toState = 2
        )

        rule.isInExpandedState()
    }

    @Test
    fun dragFromCollapsedToExpanded() {
        setContent(false, 1)

        rule.isInCollapsedState()

        rule.swipeDashboardModal(
            fromState = 1,
            toState = 2
        )

        rule.isInExpandedState()
    }

    @Test
    fun showCalendarViewFromCollapsedAndThenHideIt() {
        setContent(true, 1)

        rule.onNodeWithTag(DashboardModalTestTags.CALENDAR_VIEW_BUTTON)
            .performClick()
        rule.onNodeWithTag(DashboardModalTestTags.CALENDAR_VIEW)
            .isDisplayed()

        rule.swipeDashboardModal(
            fromState = 2,
            toState = 1
        )
        rule.swipeDashboardModal(
            fromState = 1,
            toState = 2
        )

        rule.onNodeWithTag(DashboardModalTestTags.CALENDAR_VIEW)
            .isNotDisplayed()
    }

    @Test
    fun showCalendarViewFromExpandedAndThenHideIt() {
        setContent(false, 2)

        rule.onNodeWithTag(DashboardModalTestTags.CALENDAR_VIEW_BUTTON)
            .performClick()
        rule.onNodeWithTag(DashboardModalTestTags.CALENDAR_VIEW)
            .isDisplayed()

        rule.onNodeWithTag(DashboardModalTestTags.DAY_INDICATOR_PREFIX + "2")
            .performClick()
        rule.onNodeWithTag(DashboardModalTestTags.DAY_INDICATOR_PREFIX + "2")
            .performClick()
        rule.onNodeWithTag(DashboardModalTestTags.CALENDAR_VIEW)
            .isNotDisplayed()
    }

    @Test
    fun navigateToPastMonthInCalendarView() {
        setContent(true, 2)

        val originalMonthYearText = rule.onNodeWithTag(DashboardModalTestTags.SELECTED_MONTH_TEXT)
            .fetchSemanticsNode()
            .config
            .getOrNull(SemanticsProperties.Text)
            ?.joinToString("") ?: ""

        rule.onNodeWithTag(DashboardModalTestTags.CALENDAR_VIEW)
            .performTouchInput {
                swipe(
                    start = center,
                    end = Offset(center.x + 150F, center.y),
                    durationMillis = 300
                )
            }
        val newMonthYearText = rule.onNodeWithTag(DashboardModalTestTags.SELECTED_MONTH_TEXT)
            .fetchSemanticsNode()
            .config
            .getOrNull(SemanticsProperties.Text)
            ?.joinToString("") ?: ""

        val originalDate = YearMonth.parse(originalMonthYearText, DateTimeFormatter.ofPattern("MM-yyyy"))
        val newDate = YearMonth.parse(newMonthYearText, DateTimeFormatter.ofPattern("MM-yyyy"))
        assert(originalDate.isAfter(newDate))

    }

    private fun ComposeTestRule.swipeDashboardModal(
        fromState: Int,
        toState: Int
    ) {
        val density = Density(1f, 1f)
        val fromY = with(density) { (screenHeightDp - statesBottom[fromState].dp).toPx() }
        val toY = with(density) { (screenHeightDp - statesBottom[toState].dp).toPx() }
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

    private fun ComposeTestRule.isInExpandedState() {
        rule.onNodeWithTag(DashboardModalTestTags.DAY_INDICATOR_PREFIX + "0")
            .isDisplayed()
    }

    private fun ComposeTestRule.isInCollapsedState() {
        rule.onNodeWithTag(DashboardModalTestTags.DAY_INDICATOR_PREFIX + "0")
            .isNotDisplayed()

        rule.onNodeWithTag(DashboardModalTestTags.CALENDAR_VIEW_BUTTON)
            .isDisplayed()
    }

    private fun ComposeTestRule.isInHiddenState() {
        rule.onNodeWithTag(DashboardModalTestTags.DAY_INDICATOR_PREFIX + "0")
            .isNotDisplayed()

        rule.onNodeWithTag(DashboardModalTestTags.CALENDAR_VIEW_BUTTON)
            .isNotDisplayed()
    }

    private fun setContent(showCalendarView: Boolean, initialStateIndex: Int) {
        rule.setContent {
            screenHeightDp = getScreenHeight(withoutInsetTop = true).dp
            statesBottom = states.map { state -> getResponsiveSizeHeight(state).toFloat() }
            DashboardModal(fakeMetricsViewModel, fakePersonalityViewModel, showCalendarView, initialStateIndex)
        }
        rule.waitUntil {
            screenHeightDp > 0.dp
        }
    }

}
