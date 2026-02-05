package com.bellako.kiwi.features.skills.screen

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.R
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_Display1
import com.bellako.kiwi.common.screens.components.Kiwi_HorizontalLine
import com.bellako.kiwi.common.screens.components.Kiwi_Image
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer
import com.bellako.kiwi.features.appbar.screens.AppBarScreen
import com.bellako.kiwi.features.skills.data.SkillDomain
import com.bellako.kiwi.features.skills.model.ISkillsViewModel
import com.bellako.kiwi.features.skills.model.MAX_DECK_SLOTS
import com.bellako.kiwi.features.skills.tests.SkillsFakeViewModel
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight

const val DECK_SKILLS_SECTION_INDEX = 1
const val ALL_SKILLS_SECTION_INDEX = 4

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SkillsScreen(
    skillsViewModel: ISkillsViewModel,
    focusedSkillId: Long? = null,
) {
    val skillsState by skillsViewModel.state.collectAsState()
    val kiwiColors = LocalKiwiColors.current
    val listState = rememberLazyListState()

    LaunchedEffect(skillsState, focusedSkillId) {
        if (focusedSkillId == null) return@LaunchedEffect

        val deckIndex =
            skillsState
                ?.deckSkills
                ?.indexOfFirst { it.id == focusedSkillId }
                ?: -1

        if (deckIndex >= 0) {
            listState.animateScrollToItem(DECK_SKILLS_SECTION_INDEX)
        } else {
            val allIndex =
                skillsState
                    ?.allSkills
                    ?.indexOfFirst { it.id == focusedSkillId }
                    ?: -1
            if (allIndex >= 0) {
                listState.animateScrollToItem(ALL_SKILLS_SECTION_INDEX)
            }
        }
    }

    LazyColumn(
        modifier =
            Modifier
                .fillMaxSize()
                .background(kiwiColors.color2)
                .padding(
                    vertical = getResponsiveSizeHeight(Spacing.large),
                    horizontal = getResponsiveSizeHeight(Spacing.large),
                ),
    ) {
        // DECK
        item {
            Kiwi_Display1(
                arguments =
                    KiwiTextArguments(
                        text = "Current Deck",
                        color = kiwiColors.colorF,
                        modifier = Modifier.padding(horizontal = getResponsiveSizeHeight(Spacing.small), vertical = Spacing.medium),
                    ),
            )
        }

        // DECK SKILLS
        item {
            skillsState?.let {
                DeckGrid(
                    it.deckSkills,
                    onClick =
                        { id -> skillsViewModel.unequipSkill(id) },
                    onApplyGoalProgress =
                        { skillId, goalId, newProgress ->
                            skillsViewModel.updateGoalProgress(skillId, goalId, newProgress)
                        },
                )
            }
        }

        item {
            Kiwi_Spacer(Spacing.medium)
            Kiwi_HorizontalLine(kiwiColors.color1A)
        }

        // ALL
        item {
            Kiwi_Display1(
                arguments =
                    KiwiTextArguments(
                        text = "Skills",
                        color = kiwiColors.colorF,
                        modifier = Modifier.padding(horizontal = getResponsiveSizeHeight(Spacing.small), vertical = Spacing.medium),
                    ),
            )
        }

        // UNEQUIPPED SKILLS
        item {
            skillsState?.let {
                AllSkillsGrid(
                    it.allSkills,
                    onClick =
                        { id -> skillsViewModel.equipSkill(id) },
                    onApplyGoalProgress =
                        { skillId, goalId, newProgress ->
                            skillsViewModel.updateGoalProgress(skillId, goalId, newProgress)
                        },
                )
            }
        }
    }
}

const val SKILL_WEIGHT = 0.5f

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DeckGrid(
    skills: List<SkillDomain>,
    onClick: (Long) -> Unit,
    onApplyGoalProgress: (skillId: Long, goalId: Long, newProgress: Int) -> Unit,
) {
    val slotMap = skills.associateBy { it.deckSlot }

    for (rowStart in 1..MAX_DECK_SLOTS step 2) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(getResponsiveSizeHeight(Spacing.small)),
            modifier = Modifier.fillMaxWidth(),
        ) {
            for (slot in rowStart..rowStart + 1) {
                val skill = slotMap[slot]
                if (skill != null) {
                    SkillComponent(
                        skill = skill,
                        isDisabled = false,
                        onClick = { onClick(skill.id) },
                        modifier = Modifier.weight(SKILL_WEIGHT),
                        onApplyGoalProgress,
                    )
                } else {
                    Kiwi_Image(
                        R.drawable.skill_empty,
                        "Empty skill slot",
                        modifier = Modifier.weight(SKILL_WEIGHT),
                    )
                }
            }
        }
        Kiwi_Spacer(Spacing.small)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AllSkillsGrid(
    skills: List<SkillDomain>,
    onClick: (Long) -> Unit,
    onApplyGoalProgress: (skillId: Long, goalId: Long, newProgress: Int) -> Unit,
) {
    skills.chunked(2).forEach { rowSkills ->
        Row(
            horizontalArrangement = Arrangement.spacedBy(getResponsiveSizeHeight(Spacing.small)),
            modifier = Modifier.fillMaxWidth(),
        ) {
            rowSkills.forEach { skill ->
                SkillComponent(
                    skill = skill,
                    isDisabled = skill.deckSlot != 0,
                    onClick = { onClick(skill.id) },
                    modifier = Modifier.weight(SKILL_WEIGHT),
                    onApplyGoalProgress,
                )
            }

            if (rowSkills.size == 1) {
                Box(
                    modifier = Modifier.weight(SKILL_WEIGHT),
                ) {
                }
            }
        }

        Kiwi_Spacer(Spacing.small)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("ViewModelConstructorInComposable")
@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Composable
fun SkillsScreen_Preview() {
    Kiwi_Theme {
        val fakeViewModel = SkillsFakeViewModel()

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
                SkillsScreen(
                    skillsViewModel = fakeViewModel,
                )
            }
        }
    }
}
