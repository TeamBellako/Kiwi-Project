package com.bellako.kiwi.features.map.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.bellako.kiwi.R
import com.bellako.kiwi.audio.AudioManager
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_FixedSizeButton
import com.bellako.kiwi.common.screens.components.Kiwi_H2
import com.bellako.kiwi.common.screens.components.Kiwi_Image
import com.bellako.kiwi.common.tests.CommonTestTags
import com.bellako.kiwi.common.utils.detectTransformGesturesAndEnd
import com.bellako.kiwi.features.goals.data.IGoal
import com.bellako.kiwi.features.goals.model.IGoalsViewModel
import com.bellako.kiwi.features.goals.screens.GoalNotificationType
import com.bellako.kiwi.features.goals.screens.GoalsModal
import com.bellako.kiwi.features.map.model.MapViewModel
import com.bellako.kiwi.features.nodes.data.NodeStatus
import com.bellako.kiwi.features.nodes.model.INodesViewModel
import com.bellako.kiwi.features.nodes.screens.NodeAction
import com.bellako.kiwi.features.nodes.screens.NodeConnections
import com.bellako.kiwi.features.nodes.screens.NodeOnMap
import com.bellako.kiwi.features.nodes.screens.distance
import com.bellako.kiwi.features.nodes.screens.screenToMap
import com.bellako.kiwi.features.notifications.controller.NotificationManager
import com.bellako.kiwi.features.notifications.screens.NotificationOverlay
import com.bellako.kiwi.features.quests.screens.QuestNotificationType
import com.bellako.kiwi.features.skills.model.ISkillsViewModel
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import com.bellako.kiwi.ui.getScreenHeight
import com.bellako.kiwi.ui.getScreenWidth
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlin.collections.forEach
import kotlin.math.min

const val NOTIFICATION_OVERLAY_Z_ORDER = 10f

@RequiresApi(Build.VERSION_CODES.O)
@Composable
@Suppress("ComplexMethod", "LongMethod")
fun MapScreen(
    maxZoom: Float = 8f,
    mapMarginFactor: Float = 0.08f,
    elasticityFactor: Float = 1.4f,
    mapResourceId: Int = R.drawable.mindveil_4k,
    title: String = "MINDVEIL",
    mapViewModel: MapViewModel,
    nodesViewModel: INodesViewModel,
    goalsViewModel: IGoalsViewModel,
    skillsViewModel: ISkillsViewModel,
    notificationManager: NotificationManager,
    navController: NavHostController,
) {
    val kiwiColors = LocalKiwiColors.current
    val density = LocalDensity.current

    @Suppress("MagicNumber")
    val viewportHeightPx =
        with(density) { getScreenHeight().dp.toPx() } * 0.84f // approximate usable space
    val viewportWidthPx = with(density) { getScreenWidth().dp.toPx() }

    val imageBitmap = ImageBitmap.imageResource(id = mapResourceId)
    val imageW = imageBitmap.width.toFloat()
    val imageH = imageBitmap.height.toFloat()

    val fitScale = min(viewportWidthPx / imageW, viewportHeightPx / imageH)

    val displayWidthPx = imageW * fitScale
    val displayHeightPx = imageH * fitScale

    LaunchedEffect(maxZoom, displayWidthPx, displayHeightPx, viewportWidthPx, viewportHeightPx) {
        mapViewModel.setParameters(
            maxScale = maxZoom,
            mapWidthPx = displayWidthPx,
            mapHeightPx = displayHeightPx,
            viewportWidthPx = viewportWidthPx,
            viewportHeightPx = viewportHeightPx,
            mapMarginFactor = mapMarginFactor,
            elasticityFactor = elasticityFactor,
        )
    }
    val nodesState by nodesViewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        nodesViewModel.loadNodes()

        snapshotFlow { nodesState?.nodes }
            .filterNotNull()
            .filter { it.isNotEmpty() }
            .first()
            .let { nodesMap ->
                val nodesList = nodesMap.values.toList()

                val lastOpen = nodesList.lastOrNull { it.status == NodeStatus.OPEN }

                val lastCompleted = nodesList.lastOrNull { it.status == NodeStatus.COMPLETED }

                val defaultNode = nodesList.firstOrNull()

                val selectedNode = lastOpen ?: lastCompleted ?: defaultNode

                selectedNode?.let { node ->
                    mapViewModel.selectNode(node.id, node.cordX, node.cordY)
                    mapViewModel.setPlayerNode(node.id)
                }
            }
    }

    val goalsModalRequest = remember { mutableStateOf<Pair<GoalNotificationType, List<IGoal>>?>(null) }

    LaunchedEffect(Unit) {
        goalsViewModel.checkAndNotifyGoals()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(kiwiColors.color2)
                    .testTag(CommonTestTags.HOME_SCREEN),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Kiwi_H2(
                KiwiTextArguments(
                    title,
                    color = kiwiColors.colorF,
                    modifier = Modifier.padding(0.dp, getResponsiveSizeHeight(Spacing.small)),
                ),
            )

            Kiwi_FixedSizeButton(
                horizontalMargin = Spacing.large,
                textArguments =
                    KiwiTextArguments(
                        "Cooldown skill",
                        color = kiwiColors.color7,
                        fontWeight = FontWeight.Bold,
                    ),
                color = kiwiColors.color5A,
                onClick = { skillsViewModel.putOnCooldown(2L) },
            )

            Kiwi_FixedSizeButton(
                horizontalMargin = Spacing.large,
                textArguments =
                    KiwiTextArguments(
                        "Remove cooldown skill",
                        color = kiwiColors.color7,
                        fontWeight = FontWeight.Bold,
                    ),
                color = kiwiColors.color5A,
                onClick = { skillsViewModel.removeCooldown(1L) },
            )

            Kiwi_FixedSizeButton(
                horizontalMargin = Spacing.large,
                textArguments =
                    KiwiTextArguments(
                        "Give new skill",
                        color = kiwiColors.color7,
                        fontWeight = FontWeight.Bold,
                    ),
                color = kiwiColors.color5A,
                onClick = { skillsViewModel.removeCooldown(1L) },
            )

            InteractiveMap(
                mapResourceId = mapResourceId,
                mapViewModel = mapViewModel,
                nodesViewModel = nodesViewModel,
                modifier = Modifier.fillMaxSize(),
            )
        }

        NotificationOverlay(
            notificationManager = notificationManager,
            onGoalClick = { type, goals ->
                goalsModalRequest.value = type to goals
                notificationManager.dismissCurrent()
            },
            onQuestClick = { type, quest, subquestId ->
                if (type != QuestNotificationType.QUEST_COMPLETED) {
                    navController.navigate("OBJECTIVES/${quest.id}")
                }
                notificationManager.dismissCurrent()
            },
            onSkillClick = { type, skill ->
                navController.navigate("SKILLS/${skill.id}")
                notificationManager.dismissCurrent()
            },
            modifier =
                Modifier
                    .fillMaxSize()
                    .zIndex(NOTIFICATION_OVERLAY_Z_ORDER),
        )

        goalsModalRequest.value?.let { (type, goals) ->
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .zIndex(NOTIFICATION_OVERLAY_Z_ORDER),
            ) {
                GoalsModal(
                    type,
                    goals,
                    goalsViewModel,
                    onDismiss = {
                        goalsModalRequest.value = null
                    },
                )
            }
        }
    }
}

@Composable
private fun InteractiveMap(
    mapResourceId: Int,
    mapViewModel: MapViewModel,
    nodesViewModel: INodesViewModel,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val mapState by mapViewModel.state.collectAsState()
    val nodesState by nodesViewModel.state.collectAsState()

    Box(
        modifier =
            modifier
                .pointerInput(Unit) {
                    detectTransformGesturesAndEnd(
                        onGesture = { centroid, pan, zoom, _ ->
                            mapViewModel.updateScale(zoom, centroid)
                            mapViewModel.updateOffset(pan)
                        },
                        onGestureEnd = {
                            mapViewModel.startFling()
                        },
                    )
                },
        contentAlignment = Alignment.Center,
    ) {
        Background()

        val imageWidthDp = with(LocalDensity.current) { mapState.mapWidthPx.toDp() }
        val imageHeightDp = with(LocalDensity.current) { mapState.mapHeightPx.toDp() }

        Box(
            modifier =
                Modifier
                    .size(width = imageWidthDp, height = imageHeightDp)
                    .graphicsLayer(
                        scaleX = mapState.scale,
                        scaleY = mapState.scale,
                        translationX = mapState.offset.x,
                        translationY = mapState.offset.y,
                    ),
            contentAlignment = Alignment.Center,
        ) {
            // MAP
            Kiwi_Image(
                painterResourceId = mapResourceId,
                alt = "Interactive Map",
                modifier =
                    Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            awaitEachGesture {
                                awaitFirstDown(requireUnconsumed = false)
                                val up = waitForUpOrCancellation()

                                if (up != null) {
                                    val tap = up.position

                                    val nodes = nodesState?.nodes?.values.orEmpty()
                                    if (nodes.isEmpty()) return@awaitEachGesture

                                    @Suppress("MagicNumber")
                                    val clickRadius = 50f / mapState.mapWidthPx
                                    val normalizedTap = screenToMap(tap, mapState)

                                    val clickedNode =
                                        nodes
                                            .minByOrNull {
                                                distance(Offset(it.cordX, it.cordY), normalizedTap)
                                            }?.takeIf {
                                                distance(Offset(it.cordX, it.cordY), normalizedTap) < clickRadius
                                            }

                                    clickedNode?.let {
                                        mapViewModel.selectNode(it.id, it.cordX, it.cordY)
                                    }
                                }
                            }
                        },
            )

            // NODE CONNECTIONS
            NodeConnections(
                nodes = nodesState?.nodes.orEmpty(),
                mapState = mapState,
                modifier = Modifier.fillMaxSize(),
            )
        }

        // NODES
        nodesState?.nodes?.values?.forEach { node ->
            NodeOnMap(
                node = node,
                mapState = mapState,
                isPlayerNode = node.id == mapState.playerNode,
                isSelected = node.id == mapState.selectedNodeId,
            )
        }

        // NODE ACTION BUTTON
        if (!mapState.isFocusingNode) {
            mapState.selectedNodeId?.let { selectedNodeId ->
                nodesState
                    ?.nodes[selectedNodeId]
                    ?.let { selectedNode ->
                        AudioManager.playSFX(context, R.raw.snd_fx_04_seleccion)
                        NodeAction(
                            selectedNodeId == mapState.playerNode,
                            node = selectedNode,
                            onUnlockNode = { id ->
                                nodesViewModel.unlockNode(id)
                                mapViewModel.setPlayerNode(id)
                            },
                            onCompleteNode = { id ->
                                nodesViewModel.completeNode(id)
                                AudioManager.playSFX(context, R.raw.snd_node_completed)
                            },
                        )
                    }
            }
        }
    }
}

@Composable
fun Background() {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(LocalKiwiColors.current.colorOcean),
    )
}
