package com.bellako.kiwi.features.objectives

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.bellako.kiwi.features.appbar.screens.AppBarScreen
import com.bellako.kiwi.features.quests.model.IQuestsViewModel
import com.bellako.kiwi.features.quests.screens.Quest
import com.bellako.kiwi.features.quests.tests.QuestsFakeViewModel
import com.bellako.kiwi.features.quests.tests.QuestsTestFactory
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight

@Composable
fun ObjectivesScreen(questsViewModel: IQuestsViewModel) {
    val questsState by questsViewModel.state.collectAsState()
    val kiwiColors = LocalKiwiColors.current

    LaunchedEffect(Unit) {
        questsViewModel.loadActiveQuests()
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
                    modifier = Modifier.padding(bottom = getResponsiveSizeHeight(Spacing.medium)),
                )
            }
        }
    }
}

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
                ObjectivesScreen(questsViewModel = fakeViewModel)
            }
        }
    }
}
