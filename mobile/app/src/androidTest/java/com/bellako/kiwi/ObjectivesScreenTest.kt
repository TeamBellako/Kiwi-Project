package com.bellako.kiwi

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.bellako.kiwi.features.goals.tests.GoalsFakeViewModel
import com.bellako.kiwi.features.objectives.ObjectivesScreen
import com.bellako.kiwi.features.quests.tests.QuestsFakeViewModel
import com.bellako.kiwi.ui.Kiwi_Theme
import org.junit.Rule
import org.junit.Test

class ObjectivesScreenTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun displaysAllQuests() {
        val questFakeViewModel = QuestsFakeViewModel()
        val goalsFakeViewModel = GoalsFakeViewModel()

        rule.setContent {
            Kiwi_Theme {
                ObjectivesScreen(questsViewModel = questFakeViewModel, goalsFakeViewModel)
            }
        }

        questFakeViewModel.state.value.quests.forEach { quest ->
            rule.onNodeWithText(quest.name).assertIsDisplayed()
        }
    }

    @Test
    fun focusedQuestIsExpandedAndScrolledTo() {
        val questFakeViewModel = QuestsFakeViewModel()
        val goalsFakeViewModel = GoalsFakeViewModel()

        val focusedQuestId =
            questFakeViewModel.state.value.quests
                .last()
                .id

        rule.setContent {
            Kiwi_Theme {
                ObjectivesScreen(questsViewModel = questFakeViewModel, goalsFakeViewModel, focusedQuestId = focusedQuestId)
            }
        }

        val focusedQuest =
            questFakeViewModel.state.value.quests
                .first { it.id == focusedQuestId }
        rule.onNodeWithText(focusedQuest.description).assertIsDisplayed()
    }

    @Test
    fun questsAreDisplayedOnScreenLaunch() {
        val questFakeViewModel = QuestsFakeViewModel()
        val goalsFakeViewModel = GoalsFakeViewModel()

        rule.setContent {
            Kiwi_Theme {
                ObjectivesScreen(questsViewModel = questFakeViewModel, goalsFakeViewModel)
            }
        }

        rule.waitForIdle()

        val firstQuest =
            questFakeViewModel.state.value.quests
                .first()
        rule.onNodeWithText(firstQuest.name).assertIsDisplayed()
    }
}
