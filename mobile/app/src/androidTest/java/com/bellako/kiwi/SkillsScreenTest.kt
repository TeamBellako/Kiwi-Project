package com.bellako.kiwi

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.bellako.kiwi.features.skills.screen.SkillsScreen
import com.bellako.kiwi.features.skills.tests.SkillsFakeViewModel
import com.bellako.kiwi.ui.Kiwi_Theme
import org.junit.Rule
import org.junit.Test

class SkillsScreenTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun deckSkillsAreDisplayed() {
        val fakeViewModel = SkillsFakeViewModel()

        rule.setContent {
            Kiwi_Theme {
                SkillsScreen(skillsViewModel = fakeViewModel)
            }
        }

        fakeViewModel.state.value.deckSkills.forEach { skill ->
            val nodes = rule.onAllNodesWithText(skill.name)

            nodes.fetchSemanticsNodes().forEach { node ->
                assert(node.config.contains(SemanticsProperties.Text))
            }
            nodes.assertCountEquals(2)
        }
    }

    @Test
    fun allSkillsAreDisplayed() {
        val fakeViewModel = SkillsFakeViewModel()

        rule.setContent {
            Kiwi_Theme {
                SkillsScreen(skillsViewModel = fakeViewModel)
            }
        }

        fakeViewModel.state.value.allSkills.forEach { skill ->
            val nodes = rule.onAllNodesWithTag("skill-${skill.id}").fetchSemanticsNodes()
            assert(nodes.isNotEmpty()) { "Skill '${skill.name}' should exist in the UI" }
        }
    }

    @Test
    fun focusedSkillIsVisible() {
        val fakeViewModel = SkillsFakeViewModel()

        val focusedSkill =
            fakeViewModel.state.value.allSkills
                .last()

        rule.setContent {
            Kiwi_Theme {
                SkillsScreen(
                    skillsViewModel = fakeViewModel,
                    focusedSkillId = focusedSkill.id,
                )
            }
        }

        rule.onNodeWithText(focusedSkill.name).assertIsDisplayed()
    }
}
