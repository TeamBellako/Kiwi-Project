package com.bellako.kiwi.features.quests.screens

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.R
import com.bellako.kiwi.audio.AudioManager
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_H1
import com.bellako.kiwi.common.screens.components.Kiwi_Image
import com.bellako.kiwi.common.screens.components.Kiwi_Label1
import com.bellako.kiwi.common.screens.components.Kiwi_Label2
import com.bellako.kiwi.common.screens.components.Kiwi_P2
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer
import com.bellako.kiwi.features.appbar.screens.AppBarScreen
import com.bellako.kiwi.features.quests.data.QuestDomain
import com.bellako.kiwi.features.quests.data.QuestStatus
import com.bellako.kiwi.features.quests.data.SubquestDomain
import com.bellako.kiwi.features.quests.data.SubquestStatus
import com.bellako.kiwi.features.quests.model.IQuestsViewModel
import com.bellako.kiwi.features.quests.model.QuestNotificationEvent
import com.bellako.kiwi.features.quests.tests.QuestsTestFactory
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield

@Composable
fun Quest(
    quest: QuestDomain,
    modifier: Modifier = Modifier,
    isExpanded: Boolean = false,
) {
    val kiwiColors = LocalKiwiColors.current
    var expanded by remember(isExpanded) {
        mutableStateOf(isExpanded)
    }

    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        // QUEST HEADER
        Box(
            modifier =
                Modifier
                    .clickable { expanded = !expanded }
                    .zIndex(1f),
        ) {
            // Background image
            Kiwi_Image(
                if (expanded) {
                    R.drawable.card_goals_selected
                } else {
                    R.drawable.card_goals
                },
                "Quest background",
            )

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // ICON
                Column(
                    modifier =
                        Modifier
                            .padding(getResponsiveSizeHeight(12.dp)),
                ) {
                    Kiwi_Image(
                        questIconFor(quest),
                        "Quest Icon",
                        modifier =
                            Modifier
                                .size(getResponsiveSizeHeight(65.dp)),
                    )
                }

                // TEXT
                Column {
                    Kiwi_H1(
                        KiwiTextArguments(
                            text = quest.name,
                        ),
                    )

                    Kiwi_P2(
                        KiwiTextArguments(
                            color = kiwiColors.color7A,
                            text = quest.description,
                            italic = true,
                            modifier =
                                Modifier
                                    .padding(
                                        bottom = getResponsiveSizeHeight(Spacing.xSmall),
                                        end = getResponsiveSizeHeight(Spacing.small),
                                    ),
                        ),
                    )
                }
            }
        }

        // SUBQUEST LIST (EXPANDED)
        AnimatedVisibility(visible = expanded) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .offset(y = (-getResponsiveSizeHeight(11.dp)))
                        .zIndex(0f),
            ) {
                // Background image
                Kiwi_Image(
                    R.drawable.dropdown_bg,
                    "Subquest background",
                    modifier = Modifier.matchParentSize(),
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.BottomCenter,
                )

                Column(
                    modifier =
                        Modifier
                            .padding(
                                vertical = getResponsiveSizeHeight(Spacing.medium),
                                horizontal = getResponsiveSizeHeight(28.dp),
                            ),
                ) {
                    quest.subquests.forEachIndexed { index, subquest ->
                        Subquest(
                            subquest = subquest,
                            isLast = index == quest.subquests.lastIndex,
                        )
                    }

                    Kiwi_Spacer(getResponsiveSizeHeight(Spacing.medium))
                }
            }
        }
    }
}

@Composable
fun Subquest(
    subquest: SubquestDomain,
    isLast: Boolean,
    modifier: Modifier = Modifier,
) {
    val kiwiColors = LocalKiwiColors.current

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(top = getResponsiveSizeHeight(Spacing.xSmall)),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(getResponsiveSizeHeight(Spacing.medium)),
    ) {
        // STATUS ICON
        Column(
            verticalArrangement = Arrangement.Top,
        ) {
            Kiwi_Image(
                subquestStatusIcon(subquest.status),
                "Subquest Status Icon",
                modifier =
                    Modifier
                        .size(getResponsiveSizeHeight(20.dp)),
            )
            if (!isLast) {
                Box(
                    modifier =
                        Modifier
                            .align(Alignment.CenterHorizontally)
                            .width(getResponsiveSizeHeight(3.dp))
                            .height(getResponsiveSizeHeight(40.dp))
                            .padding(top = getResponsiveSizeHeight(3.dp))
                            .background(kiwiColors.color0A),
                )
            }
        }

        // TEXT
        Column {
            Kiwi_Label1(
                KiwiTextArguments(
                    text = subquest.name,
                ),
            )

            Kiwi_Label1(
                KiwiTextArguments(
                    text = subquestStatusText(subquest.status),
                    color = kiwiColors.color7A,
                ),
            )
        }
    }
}

@Composable
private fun QuestNotification(
    quest: QuestDomain,
    name: String,
    isCompleted: Boolean = false,
    onClick: () -> Unit,
) {
    val kiwiColors = LocalKiwiColors.current

    Box(
        modifier =
            Modifier
                .wrapContentSize()
                .clickable { onClick() }
                .zIndex(1f),
    ) {
        // Background image
        Kiwi_Image(
            if (isCompleted) {
                R.drawable.notification_quest_completed
            } else {
                R.drawable.notification_quest_new
            },
            "Quest notification background",
        )

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .padding(getResponsiveSizeHeight(Spacing.medium)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // ICON
            Column {
                Kiwi_Image(
                    questIconFor(quest),
                    "Quest Icon",
                    modifier =
                        Modifier
                            .size(getResponsiveSizeHeight(68.dp)),
                )
            }

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
                        text = if (isCompleted) "Quest Completed!" else "Your have a New Quest!",
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

@Composable
fun QuestCompletedNotification(quest: QuestDomain) {
    QuestNotification(
        quest = quest,
        name = quest.name,
        isCompleted = true,
        onClick = {},
    )
}

@Composable
fun SubquestCompletedNotification(quest: QuestDomain) {
    QuestNotification(
        quest = quest,
        name = quest.name,
        isCompleted = true,
        onClick = {},
    )
}

@Composable
fun SubquestFailedNotification(quest: QuestDomain) {
    QuestNotification(
        quest = quest,
        name = quest.name,
        isCompleted = true,
        onClick = {},
    )
}

@Composable
fun NewQuestNotification(
    quest: QuestDomain,
    navController: NavController,
) {
    QuestNotification(
        quest = quest,
        name = quest.name,
        isCompleted = false,
        onClick = {
            navController.navigate("objectives/${quest.id}")
        },
    )
}

data class NotificationItem(
    val event: QuestNotificationEvent,
    val visible: MutableState<Boolean> = mutableStateOf(false),
)

@Composable
fun QuestNotificationsOverlay(
    questsViewModel: IQuestsViewModel,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val notifications = remember { mutableStateListOf<NotificationItem>() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        questsViewModel.getNotifications().collect { event ->
            val item = NotificationItem(event, visible = mutableStateOf(false))
            notifications += item
        }
    }

    Box(modifier = modifier) {
        Column(modifier = Modifier.padding(getResponsiveSizeHeight(Spacing.large))) {
            notifications.forEach { item ->
                key(item) {
                    LaunchedEffect(item) {
                        yield()
                        item.visible.value = true

                        when (item.event) {
                            is QuestNotificationEvent.NewQuest -> {
                                AudioManager.playSFX(
                                    context,
                                    R.raw.snd_ui_check,
                                )
                            }
                            is QuestNotificationEvent.QuestCompleted -> {
                                AudioManager.playSFX(
                                    context,
                                    R.raw.snd_ui_confirmationsuccess,
                                )
                            }

                            is QuestNotificationEvent.SubquestCompleted -> {
                                AudioManager.playSFX(
                                    context,
                                    R.raw.snd_ui_confirmationsuccess,
                                )
                            }
                            is QuestNotificationEvent.SubquestFailed -> TODO()
                        }

                        delay(4000)

                        item.visible.value = false
                        delay(300)
                        notifications.remove(item)
                    }

                    AnimatedVisibility(
                        visible = item.visible.value,
                        enter =
                            slideInVertically(
                                initialOffsetY = { -it },
                                animationSpec = tween(durationMillis = 300),
                            ),
                        exit =
                            slideOutVertically(
                                targetOffsetY = { -it },
                                animationSpec = tween(durationMillis = 300),
                            ),
                    ) {
                        when (item.event) {
                            is QuestNotificationEvent.NewQuest -> NewQuestNotification(item.event.quest, navController)
                            is QuestNotificationEvent.QuestCompleted -> QuestCompletedNotification(item.event.quest)
                            is QuestNotificationEvent.SubquestCompleted -> TODO()
                            is QuestNotificationEvent.SubquestFailed -> TODO()
                        }
                    }
                }
            }
        }
    }
}

// HELPERS
@DrawableRes
fun questIconFor(quest: QuestDomain): Int =
    when (quest.status) { // TODO cambiar para usar icon de la BBDD
        QuestStatus.ACTIVE -> R.drawable.ic_quest_comet
        QuestStatus.COMPLETED -> R.drawable.ic_quest_star
    }

@DrawableRes
fun subquestStatusIcon(status: SubquestStatus): Int =
    when (status) {
        SubquestStatus.COMPLETED -> R.drawable.ic_dropdown_tick
        SubquestStatus.FAILED -> R.drawable.ic_dropdown_fail
        SubquestStatus.ACTIVE -> R.drawable.ic_dropdown_location
        SubquestStatus.LOCKED -> R.drawable.ic_dropdown_lock
    }

fun subquestStatusText(status: SubquestStatus): String =
    when (status) {
        SubquestStatus.COMPLETED -> "Completed"
        SubquestStatus.FAILED -> "Failed"
        SubquestStatus.ACTIVE -> "Current Objective"
        SubquestStatus.LOCKED -> "Upcoming"
    }

@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Composable
private fun NewQuestNotification_Preview() {
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
                                horizontal = getResponsiveSizeHeight(Spacing.xLarge),
                            ),
                ) {
                    NewQuestNotification(QuestsTestFactory.questWithFourSubquests(), nav)
                    QuestCompletedNotification(QuestsTestFactory.questWithThreeSubquests())
                }
            }
        }
    }
}
