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
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_Display1
import com.bellako.kiwi.common.screens.components.Kiwi_HorizontalLine
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer
import com.bellako.kiwi.features.appbar.screens.AppBarScreen
import com.bellako.kiwi.features.skills.data.SkillDomain
import com.bellako.kiwi.features.skills.model.ISkillsViewModel
import com.bellako.kiwi.features.skills.tests.SkillsFakeViewModel
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SkillsScreen(skillsViewModel: ISkillsViewModel) {
    val skillsState by skillsViewModel.state.collectAsState()
    val kiwiColors = LocalKiwiColors.current

    LaunchedEffect(Unit) {
        skillsViewModel.loadAllSkills()
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
            skillsState?.let { SkillsGrid(it.deckSkills, false) }
        }

        item {
            Kiwi_Spacer(Spacing.medium)
            Kiwi_HorizontalLine(kiwiColors.color7D)
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
            skillsState?.let { SkillsGrid(it.skills, true) }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun SkillsGrid(
    skills: List<SkillDomain>,
    disableEquipped: Boolean

) {
    val horizontalSpacing = getResponsiveSizeHeight(Spacing.small)

    skills.chunked(2).forEach { rowSkills ->
        Row(
            horizontalArrangement = Arrangement.spacedBy(horizontalSpacing),
            modifier = Modifier.fillMaxWidth(),
        ) {
            rowSkills.forEach { skill ->
                Skill(
                    skill = skill,
                    if(disableEquipped){
                        skill.deckSlot != 0
                    }else{
                        false;
                    },
                    modifier = Modifier.weight(0.5f),
                )
            }

            if (rowSkills.size == 1) {
                Box(modifier = Modifier.weight(0.5f))
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
