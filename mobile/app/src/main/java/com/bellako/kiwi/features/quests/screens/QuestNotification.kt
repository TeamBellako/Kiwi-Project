package com.bellako.kiwi.features.quests.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.R
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_H1
import com.bellako.kiwi.common.screens.components.Kiwi_Image
import com.bellako.kiwi.common.screens.components.Kiwi_Label2
import com.bellako.kiwi.features.appbar.screens.AppBarScreen
import com.bellako.kiwi.features.quests.tests.QuestsTestFactory
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight

@Composable
fun QuestNotification(
    name: String,
    questIcon: Int,
    type: QuestNotificationType,
    onClick: () -> Unit,
) {
    val kiwiColors = LocalKiwiColors.current

    Box(
        contentAlignment = Alignment.Center,
        modifier =
            Modifier
                .wrapContentSize()
                .clickable { onClick() }
                .zIndex(1f),
    ) {
        // Background image
        Kiwi_Image(
            when (type) {
                QuestNotificationType.NEW -> R.drawable.notification_quest_new
                QuestNotificationType.QUEST_COMPLETED -> R.drawable.notification_quest_completed
                QuestNotificationType.SUBQUEST_COMPLETED -> R.drawable.notification_quest_completed
                QuestNotificationType.SUBQUEST_FAILED -> R.drawable.notification_quest_failed
            },
            "Quest notification background",
        )

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(getResponsiveSizeHeight(Spacing.medium)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // ICON
            Kiwi_Image(
                questIcon(questIcon),
                "Quest Icon",
                modifier =
                    Modifier
                        .size(getResponsiveSizeHeight(68.dp)),
            )

            // TEXT
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Kiwi_H1(
                    KiwiTextArguments(
                        color = kiwiColors.colorF,
                        text = name,
                    ),
                )

                Kiwi_Label2(
                    KiwiTextArguments(
                        color = kiwiColors.colorF,
                        text =
                            when (type) {
                                QuestNotificationType.NEW -> "Your have a New Quest!"
                                QuestNotificationType.QUEST_COMPLETED -> "Quest Completed!"
                                QuestNotificationType.SUBQUEST_COMPLETED -> "Objective Completed!"
                                QuestNotificationType.SUBQUEST_FAILED -> "Objective Failed"
                            },
                        italic = true,
                        modifier =
                            Modifier
                                .offset(
                                    y = -getResponsiveSizeHeight(Spacing.xSmall),
                                ),
                    ),
                )
            }
        }
    }
}

enum class QuestNotificationType {
    NEW,
    QUEST_COMPLETED,
    SUBQUEST_COMPLETED,
    SUBQUEST_FAILED,
}

@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Composable
fun NewQuestNotification_Preview() {
    val nav = rememberNavController()
    Kiwi_Theme {
        Scaffold(
            bottomBar = {
                AppBarScreen(navController = nav)
            },
        ) { paddingValues ->
            Box(
                modifier =
                    Modifier
                        .padding(paddingValues)
                        .fillMaxSize(),
            ) {
                Column(
                    modifier =
                        Modifier
                            .padding(
                                vertical = getResponsiveSizeHeight(Spacing.large),
                                horizontal = getResponsiveSizeHeight(Spacing.large),
                            ),
                ) {
                    val quest1 = QuestsTestFactory.questWithFourSubquests()
                    val quest2 = QuestsTestFactory.questWithThreeSubquests()
                    QuestNotification(quest1.name, quest1.icon, QuestNotificationType.QUEST_COMPLETED) {}
                    QuestNotification(quest2.name, quest2.icon, QuestNotificationType.NEW) {}
                }
            }
        }
    }
}
