package com.bellako.kiwi.features.objectives

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_Display1
import com.bellako.kiwi.common.screens.components.Kiwi_HorizontalLine
import com.bellako.kiwi.common.screens.components.Kiwi_HorizontalLine_Text
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer
import com.bellako.kiwi.common.utils.DateUtils.dateToString
import com.bellako.kiwi.features.appbar.screens.AppBarScreen
import com.bellako.kiwi.features.goals.data.IGoal
import com.bellako.kiwi.features.goals.model.IGoalsViewModel
import com.bellako.kiwi.features.goals.screens.GoalComponent
import com.bellako.kiwi.features.goals.tests.GoalsFakeViewModel
import com.bellako.kiwi.features.quests.model.IQuestsViewModel
import com.bellako.kiwi.features.quests.screens.Quest
import com.bellako.kiwi.features.quests.tests.QuestsFakeViewModel
import com.bellako.kiwi.features.quests.tests.QuestsTestFactory
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ObjectivesScreen(
    questsViewModel: IQuestsViewModel,
    goalsViewModel: IGoalsViewModel,
    focusedQuestId: Int? = null,
) {
    val questsState by questsViewModel.state.collectAsState()
    val goalsState by goalsViewModel.state.collectAsState()
    val kiwiColors = LocalKiwiColors.current
    val listState = rememberLazyListState()

    var todayGoals by remember { mutableStateOf<List<IGoal>?>(null) }
    var yesterdayGoals by remember { mutableStateOf<List<IGoal>?>(null) }

    LaunchedEffect(Unit) {
        questsViewModel.loadActiveQuests()
    }

    LaunchedEffect(questsState, focusedQuestId) {
        if (focusedQuestId == null) return@LaunchedEffect

        val index =
            questsState
                ?.quests
                ?.indexOfFirst { it.id == focusedQuestId }
                ?: -1

        if (index >= 0) {
            listState.animateScrollToItem(index)
        }
    }
    LaunchedEffect(goalsState) {
        val today = dateToString(LocalDate.now())
        val yesterday = dateToString(LocalDate.now().minusDays(1))
        val todayResult = goalsViewModel.getGoalsByDate(today)
        if (todayResult.isSuccess) {
            val goals = todayResult.getOrNull() ?: emptyList()
            if (goals.isNotEmpty()) {
                todayGoals = goals
            }
        }
        val yesterdayResult = goalsViewModel.getGoalsByDate(yesterday)
        if (yesterdayResult.isSuccess) {
            val goals = yesterdayResult.getOrNull() ?: emptyList()
            if (goals.isNotEmpty()) {
                yesterdayGoals = goals
            }
        }
    }

    LazyColumn(
        modifier =
            Modifier
                .fillMaxSize()
                .background(kiwiColors.color2)
                .padding(horizontal = getResponsiveSizeHeight(Spacing.large)),
    ) {
        // TITLE
        item {
            Kiwi_Spacer(Spacing.large)
            Kiwi_Display1(
                arguments =
                    KiwiTextArguments(
                        text = "Main Missions",
                        color = kiwiColors.colorF,
                        modifier = Modifier.padding(horizontal = getResponsiveSizeHeight(Spacing.small), vertical = Spacing.medium),
                    ),
            )
        }

        // QUEST LIST
        questsState?.quests?.let { quests ->
            items(quests) { quest ->
                Quest(
                    quest = quest,
                    isExpanded = quest.id == focusedQuestId,
                    modifier = Modifier.padding(bottom = getResponsiveSizeHeight(Spacing.small)),
                )
            }
        }

        item {
            Kiwi_Spacer(Spacing.medium)
            Kiwi_HorizontalLine(kiwiColors.color1A)
        }

        // TITLE GOALS
        item {
            Kiwi_Display1(
                arguments =
                    KiwiTextArguments(
                        text = "Daily Challenges",
                        color = kiwiColors.colorF,
                        modifier = Modifier.padding(horizontal = getResponsiveSizeHeight(Spacing.small), vertical = Spacing.medium),
                    ),
            )
        }

        // GOALS LIST
        todayGoals?.let { goals ->
            items(goals) { goal ->
                GoalComponent(goal, goalsViewModel)
            }
        }

        item {
            Kiwi_Spacer(Spacing.medium)
            Kiwi_HorizontalLine_Text(
                "Yesterday",
                kiwiColors.color1A,
                kiwiColors.colorF,
                modifier = Modifier.padding(horizontal = getResponsiveSizeHeight(Spacing.large)),
            )
            Kiwi_Spacer(Spacing.medium)
        }

        // YESTERDAY GOALS LIST
        yesterdayGoals?.let { goals ->
            items(goals) { goal ->
                GoalComponent(goal, goalsViewModel)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("ViewModelConstructorInComposable")
@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Composable
fun ObjectivesScreen_Preview() {
    Kiwi_Theme {
        val fakeViewModel = QuestsFakeViewModel(QuestsTestFactory.validQuestsState())

        Scaffold(
            bottomBar = {
                AppBarScreen(navController = rememberNavController())
            },
        ) { paddingValues ->
            Box(
                modifier =
                    Modifier
                        .padding(paddingValues)
                        .fillMaxSize(),
            ) {
                ObjectivesScreen(
                    questsViewModel = fakeViewModel,
                    goalsViewModel = GoalsFakeViewModel(),
                )
            }
        }
    }
}
