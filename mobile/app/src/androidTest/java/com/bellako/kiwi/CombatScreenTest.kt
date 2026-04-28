package com.bellako.kiwi

import android.os.Build
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.test.filters.SdkSuppress
import com.bellako.kiwi.audio.AudioManager
import com.bellako.kiwi.features.combat.data.CombatActionDomain
import com.bellako.kiwi.features.combat.data.CombatActor
import com.bellako.kiwi.features.combat.data.CombatDomain
import com.bellako.kiwi.features.combat.screens.CombatScreen
import com.bellako.kiwi.features.combat.tests.CombatTestFactory
import com.bellako.kiwi.features.skills.data.SkillDomain
import com.bellako.kiwi.features.skills.tests.SkillsTestFactory
import com.bellako.kiwi.ui.Kiwi_Theme
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
class CombatScreenTest {
    @get:Rule
    val rule = createComposeRule()

    private val deckSkills: List<SkillDomain> =
        listOf(
            SkillsTestFactory.timeCooldownSkillEquipped(),
            SkillsTestFactory.goalCooldownSkillEquipped(),
        )

    @Before
    fun setUp() {
        AudioManager.setEnabled(false)
    }

    @Test
    fun headerIsDisplayed() {
        setContent(combat())
        rule.onNodeWithText("Ongoing Combat").assertIsDisplayed()
    }

    @Test
    fun enemyAndPlayerHpAreDisplayed() {
        setContent(combat(enemyHp = 100, enemyMaxHp = 100, userHp = 80, userMaxHp = 100))
        rule.onNodeWithText("100/100").assertIsDisplayed()
        rule.onNodeWithText("80/100").assertIsDisplayed()
    }

    @Test
    fun deckSkillsAreRendered() {
        setContent(combat())
        deckSkills.forEach { skill ->
            rule.onNodeWithTag("skill-${skill.id}").assertIsDisplayed()
        }
    }

    @Test
    fun closeButtonShowsAbandonModal() {
        setContent(combat())
        rule.onNodeWithContentDescription("Close combat").performClick()
        rule.onNodeWithText("Leave combat?").assertIsDisplayed()
    }

    @Test
    fun confirmingAbandonFiresCallback() {
        var confirmed = false
        setContent(combat(), onConfirmAbandon = { confirmed = true })
        rule.onNodeWithContentDescription("Close combat").performClick()
        rule.onNodeWithText("Leave").performClick()
        rule.waitForIdle()
        assert(confirmed) { "onConfirmAbandon callback should have fired" }
    }

    @Test
    fun cancellingAbandonClosesModal() {
        var confirmed = false
        setContent(combat(), onConfirmAbandon = { confirmed = true })
        rule.onNodeWithContentDescription("Close combat").performClick()
        rule.onNodeWithText("Leave combat?").assertIsDisplayed()
        rule.onNodeWithText("Cancel").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("Leave combat?").assertIsNotDisplayed()
        assert(!confirmed) { "Cancel must not fire onConfirmAbandon" }
    }

    @Test
    fun longPressOnDeckSkillTriggersOnSkillClick() {
        var clicked: Pair<Long, String>? = null
        setContent(combat(), onSkillClick = { id, name -> clicked = id to name })
        val skill = deckSkills.first()
        rule
            .onNodeWithTag("skill-${skill.id}")
            .performTouchInput { longClick() }
        rule.waitForIdle()
        assertEquals(skill.id to skill.name, clicked)
    }

    @Test
    fun tappingTurnIndicatorOpensTheLog() {
        val skillName = "Smite"
        val combatWithLog =
            combat(
                log =
                    listOf(
                        CombatTestFactory.skillUsedAction(
                            actor = CombatActor.ENEMY,
                            skillName = skillName,
                        ),
                    ),
            )
        setContent(combatWithLog)

        // Initially the indicator shows the message; the log overlay is closed.
        rule.onAllNodesWithText(skillName, substring = true).assertCountEquals(1)

        rule.onNodeWithText(skillName, substring = true).performClick()
        rule.waitForIdle()

        // Log overlay is now open: indicator + log entry both contain the skill name.
        rule.onAllNodesWithText(skillName, substring = true).assertCountEquals(2)
    }

    @Test
    fun deckIsDisabledWhileTurnIsPlaying() {
        var clicked: Pair<Long, String>? = null
        setContent(
            combat(),
            isTurnPlaying = true,
            onSkillClick = { id, name -> clicked = id to name },
        )
        val skill = deckSkills.first()
        rule
            .onNodeWithTag("skill-${skill.id}")
            .performTouchInput { longClick() }
        rule.waitForIdle()
        assert(clicked == null) { "onSkillClick must not fire while a turn is playing" }
    }

    private fun combat(
        enemyHp: Int = 100,
        enemyMaxHp: Int = 100,
        userHp: Int = 100,
        userMaxHp: Int = 100,
        log: List<CombatActionDomain> = emptyList(),
    ): CombatDomain =
        CombatTestFactory.validCombatDomain(
            enemyName = "Procrastinogre",
            enemy =
                CombatTestFactory.validCombatActorDomain(
                    stats =
                        CombatTestFactory.validCombatStatsDomain(
                            currentHp = enemyHp,
                            maxHp = enemyMaxHp,
                        ),
                ),
            user =
                CombatTestFactory.validCombatActorDomain(
                    stats =
                        CombatTestFactory.validCombatStatsDomain(
                            currentHp = userHp,
                            maxHp = userMaxHp,
                        ),
                ),
            log = log,
        )

    private fun setContent(
        combat: CombatDomain,
        isTurnPlaying: Boolean = false,
        onConfirmAbandon: () -> Unit = {},
        onSkillClick: (Long, String) -> Unit = { _, _ -> },
    ) {
        rule.setContent {
            Kiwi_Theme {
                CombatScreen(
                    combat = combat,
                    deckSkills = deckSkills,
                    isTurnPlaying = isTurnPlaying,
                    onConfirmAbandon = onConfirmAbandon,
                    onSkillClick = onSkillClick,
                )
            }
        }
        rule.waitForIdle()
    }
}
