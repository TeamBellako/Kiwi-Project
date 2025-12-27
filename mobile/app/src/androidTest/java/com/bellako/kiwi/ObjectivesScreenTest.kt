package com.bellako.kiwi

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.bellako.kiwi.features.objectives.ObjectivesScreen
import com.bellako.kiwi.features.quests.tests.QuestsFakeViewModel
import com.bellako.kiwi.features.quests.tests.QuestsTestFactory
import com.bellako.kiwi.ui.Kiwi_Theme
import org.junit.Rule
import org.junit.Test

class ObjectivesScreenTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun displaysAllQuests() {
        val fakeViewModel = QuestsFakeViewModel(QuestsTestFactory.validQuestsState())

        rule.setContent {
            Kiwi_Theme {
                ObjectivesScreen(questsViewModel = fakeViewModel)
            }
        }

        fakeViewModel.state.value.quests.forEach { quest ->
            rule.onNodeWithText(quest.name).assertIsDisplayed()
        }
    }

    @Test
    fun focusedQuestIsExpandedAndScrolledTo() {
        val fakeViewModel = QuestsFakeViewModel(QuestsTestFactory.validQuestsState())
        val focusedQuestId =
            fakeViewModel.state.value.quests
                .last()
                .id

        rule.setContent {
            Kiwi_Theme {
                ObjectivesScreen(questsViewModel = fakeViewModel, focusedQuestId = focusedQuestId)
            }
        }

        val focusedQuest =
            fakeViewModel.state.value.quests
                .first { it.id == focusedQuestId }
        rule.onNodeWithText(focusedQuest.description).assertIsDisplayed()
    }

    @Test
    fun questsAreDisplayedOnScreenLaunch() {
        val fakeViewModel = QuestsFakeViewModel()

        rule.setContent {
            Kiwi_Theme {
                ObjectivesScreen(questsViewModel = fakeViewModel)
            }
        }

        rule.waitForIdle()

        val firstQuest =
            fakeViewModel.state.value.quests
                .first()
        rule.onNodeWithText(firstQuest.name).assertIsDisplayed()
    }
}
