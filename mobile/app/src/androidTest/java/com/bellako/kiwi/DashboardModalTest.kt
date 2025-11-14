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
import com.bellako.kiwi.common.tests.DashboardModalTestTags
import com.bellako.kiwi.common.utils.DAYS_IN_WEEK
import com.bellako.kiwi.common.utils.DateUtils
import com.bellako.kiwi.features.dashboard.screens.DashboardScreen
import com.bellako.kiwi.features.metrics.data.MetricsDataMapper
import com.bellako.kiwi.features.metrics.data.MetricsState
import com.bellako.kiwi.features.metrics.model.MetricsFactory
import com.bellako.kiwi.features.metrics.tests.MetricsFakeViewModel
import com.bellako.kiwi.features.personality.data.PersonalityState
import com.bellako.kiwi.features.personality.tests.PersonalityFakeViewModel
import com.bellako.kiwi.features.personality.tests.PersonalityTestFactory.validPersonalityDTO
import com.bellako.kiwi.features.users.data.UsersState
import com.bellako.kiwi.features.users.tests.UsersFakeViewModel
import com.bellako.kiwi.features.users.tests.UsersTestFactory.validUsersDTO
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import com.bellako.kiwi.ui.getScreenHeight
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class DashboardModalTest {
    @get:Rule
    val rule = createComposeRule()

    private lateinit var usersState: UsersState
    private lateinit var usersFakeViewModel: UsersFakeViewModel

    private lateinit var metricsState: MetricsState
    private lateinit var fakeMetricsViewModel: MetricsFakeViewModel

    private lateinit var personalityState: PersonalityState
    private lateinit var fakePersonalityViewModel: PersonalityFakeViewModel

    private var screenHeightDp = 0.dp
    private val states = listOf(150, 260, 650)
    private var statesBottom: List<Float> = listOf()

    private val dateNow = LocalDate.now()

    private val pastMetricsDTO = MetricsFactory.generateRandomValidMetricDTO().copy(date = dateNow.minusDays(1).toString())
    private val todayMetricsDTO = MetricsFactory.generateRandomValidMetricDTO().copy(date = dateNow.toString())

    @Before
    fun setUp() {
        AudioManager.setEnabled(false)

        usersState = UsersState(validUsersDTO().email, validUsersDTO().password, validUsersDTO().registerDate)
        usersFakeViewModel = UsersFakeViewModel(usersState)

        metricsState = MetricsDataMapper.toState(todayMetricsDTO)
        fakeMetricsViewModel = MetricsFakeViewModel(metricsState, todayMetricsDTO, pastMetricsDTO)

        personalityState =
            PersonalityState(
                validPersonalityDTO().realName,
                validPersonalityDTO().knightName,
                validPersonalityDTO().build,
                validPersonalityDTO().goodApps,
                validPersonalityDTO().badApps,
                validPersonalityDTO().neutralApps,
            )
        fakePersonalityViewModel = PersonalityFakeViewModel(personalityState)
    }

    @Test
    fun loadPastMetrics() {
        setContent(false, 2)

        val dateYesterday = dateNow.minusDays(1)
        val yesterdayWeekNumber = DateUtils.getDayOfWeekNumber(dateYesterday)
        if (yesterdayWeekNumber == DAYS_IN_WEEK - 1) {
            rule
                .onNodeWithTag(DashboardModalTestTags.CALENDAR_VIEW_BUTTON)
                .performClick()

            if (dateNow.month != dateYesterday.month) {
                swipeCalendar(-1f)
            }

            rule
                .onNodeWithTag(DashboardModalTestTags.DAY_INDICATOR_PREFIX + dateYesterday.dayOfMonth)
                .performClick()
        } else {
            rule
                .onNodeWithTag(DashboardModalTestTags.DAY_INDICATOR_PREFIX + yesterdayWeekNumber.toString())
                .performClick()
        }

        val pastGoodTimeSeconds = DateUtils.parseTimeSeconds(pastMetricsDTO.currentGoodTimeSeconds)
        val pastBadTimeSeconds = DateUtils.parseTimeSeconds(pastMetricsDTO.currentBadTimeSeconds)

        rule
            .onNodeWithTag(DashboardModalTestTags.GOOD_TIME)
            .assertTextContains(pastGoodTimeSeconds, true)
        rule
            .onNodeWithTag(DashboardModalTestTags.BAD_TIME)
            .assertTextContains(pastBadTimeSeconds, true)
    }

    @Test
    fun tryLoadFutureMetrics() {
        setContent(false, 2)

        val dateTomorrow = dateNow.plusDays(1)
        val tomorrowWeekNumber = DateUtils.getDayOfWeekNumber(dateTomorrow)
        if (tomorrowWeekNumber == 0) {
            rule
                .onNodeWithTag(DashboardModalTestTags.CALENDAR_VIEW_BUTTON)
                .performClick()

            if (dateNow.month != dateTomorrow.month) {
                swipeCalendar(1f)
            }

            rule
                .onNodeWithTag(DashboardModalTestTags.DAY_INDICATOR_PREFIX + dateTomorrow.dayOfMonth)
                .performClick()
        } else {
            rule
                .onNodeWithTag(DashboardModalTestTags.DAY_INDICATOR_PREFIX + tomorrowWeekNumber.toString())
                .performClick()
        }

        val todayGoodTimeSeconds = DateUtils.parseTimeSeconds(todayMetricsDTO.currentGoodTimeSeconds)
        val todayBadTimeSeconds = DateUtils.parseTimeSeconds(todayMetricsDTO.currentBadTimeSeconds)

        val goodTimeNode = rule.onNodeWithTag(DashboardModalTestTags.GOOD_TIME)
        val badTimeNode = rule.onNodeWithTag(DashboardModalTestTags.BAD_TIME)
        if (goodTimeNode.isDisplayed() && badTimeNode.isDisplayed()) {
            goodTimeNode.assertTextContains(todayGoodTimeSeconds, true)
            badTimeNode.assertTextContains(todayBadTimeSeconds, true)
        }
    }

    @Test
    fun dragFromHiddenToCollapsed() {
        setContent(false, 0)

        isInHiddenState()

        rule.swipeDashboardModal(
            fromState = 0,
            toState = 1,
        )

        isInCollapsedState()
    }

    @Test
    fun dragFromHiddenToExpanded() {
        setContent(false, 0)

        isInHiddenState()

        rule.swipeDashboardModal(
            fromState = 0,
            toState = 2,
        )

        isInExpandedState()
    }

    @Test
    fun dragFromCollapsedToExpanded() {
        setContent(false, 1)

        isInCollapsedState()

        rule.swipeDashboardModal(
            fromState = 1,
            toState = 2,
        )

        isInExpandedState()
    }

    @Test
    fun showCalendarViewFromCollapsedAndThenHideIt() {
        setContent(true, 1)

        rule
            .onNodeWithTag(DashboardModalTestTags.CALENDAR_VIEW_BUTTON)
            .performClick()
        rule
            .onNodeWithTag(DashboardModalTestTags.CALENDAR_VIEW)
            .isDisplayed()

        rule.swipeDashboardModal(
            fromState = 2,
            toState = 1,
        )
        rule.swipeDashboardModal(
            fromState = 1,
            toState = 2,
        )

        rule
            .onNodeWithTag(DashboardModalTestTags.CALENDAR_VIEW)
            .isNotDisplayed()
    }

    @Test
    fun showCalendarViewFromExpandedAndThenHideIt() {
        setContent(false, 2)

        rule
            .onNodeWithTag(DashboardModalTestTags.CALENDAR_VIEW_BUTTON)
            .performClick()
        rule
            .onNodeWithTag(DashboardModalTestTags.CALENDAR_VIEW)
            .isDisplayed()

        rule
            .onNodeWithTag(DashboardModalTestTags.DAY_INDICATOR_PREFIX + "2")
            .performClick()
        rule
            .onNodeWithTag(DashboardModalTestTags.DAY_INDICATOR_PREFIX + "2")
            .performClick()
        rule
            .onNodeWithTag(DashboardModalTestTags.CALENDAR_VIEW)
            .isNotDisplayed()
    }

    @Test
    fun navigateToPastMonthInCalendarView() {
        setContent(true, 2)

        val originalMonthYearText =
            rule
                .onNodeWithTag(DashboardModalTestTags.SELECTED_MONTH_TEXT)
                .fetchSemanticsNode()
                .config
                .getOrNull(SemanticsProperties.Text)
                ?.joinToString("") ?: ""

        swipeCalendar(-1f)

        val newMonthYearText =
            rule
                .onNodeWithTag(DashboardModalTestTags.SELECTED_MONTH_TEXT)
                .fetchSemanticsNode()
                .config
                .getOrNull(SemanticsProperties.Text)
                ?.joinToString("") ?: ""

        val originalMonth = originalMonthYearText.toInt()
        val newMonth = newMonthYearText.toInt()

        if (originalMonth == 12) {
            assert(originalMonth < newMonth)
        } else {
            assert(newMonth < originalMonth)
        }
    }

    private fun ComposeTestRule.swipeDashboardModal(
        fromState: Int,
        toState: Int,
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
                    durationMillis = 300,
                )
            }
    }

    private fun swipeCalendar(direction: Float) {
        rule
            .onNodeWithTag(DashboardModalTestTags.CALENDAR_VIEW)
            .performTouchInput {
                swipe(
                    start = center,
                    end = Offset(center.x + -150f * direction.coerceIn(-1f, 1f), center.y),
                    durationMillis = 300,
                )
            }
    }

    private fun isInExpandedState() {
        rule
            .onNodeWithTag(DashboardModalTestTags.DAY_INDICATOR_PREFIX + "0")
            .isDisplayed()
    }

    private fun isInCollapsedState() {
        rule
            .onNodeWithTag(DashboardModalTestTags.DAY_INDICATOR_PREFIX + "0")
            .isNotDisplayed()

        rule
            .onNodeWithTag(DashboardModalTestTags.CALENDAR_VIEW_BUTTON)
            .isDisplayed()
    }

    private fun isInHiddenState() {
        rule
            .onNodeWithTag(DashboardModalTestTags.DAY_INDICATOR_PREFIX + "0")
            .isNotDisplayed()

        rule
            .onNodeWithTag(DashboardModalTestTags.CALENDAR_VIEW_BUTTON)
            .isNotDisplayed()
    }

    private fun setContent(
        showCalendarView: Boolean,
        initialStateIndex: Int,
    ) {
        rule.setContent {
            screenHeightDp = getScreenHeight(withoutInsetTop = true).dp
            statesBottom = states.map { state -> getResponsiveSizeHeight(state).toFloat() }
            DashboardScreen(usersFakeViewModel, fakeMetricsViewModel, fakePersonalityViewModel, showCalendarView, initialStateIndex)
        }
        rule.waitUntil {
            screenHeightDp > 0.dp
        }
    }
}
