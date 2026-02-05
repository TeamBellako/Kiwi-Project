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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.bellako.kiwi.R
import com.bellako.kiwi.audio.AudioManager
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_H2
import com.bellako.kiwi.common.screens.components.Kiwi_Image
import com.bellako.kiwi.common.services.eventbus.EventBus
import com.bellako.kiwi.common.services.eventbus.EventPayload
import com.bellako.kiwi.common.services.eventbus.EventType
import com.bellako.kiwi.common.services.eventbus.listenToEvent
import com.bellako.kiwi.common.tests.CommonTestTags
import com.bellako.kiwi.common.utils.detectTransformGesturesAndEnd
import com.bellako.kiwi.features.goals.data.GoalModalType
import com.bellako.kiwi.features.goals.data.IGoal
import com.bellako.kiwi.features.goals.model.IGoalsViewModel
import com.bellako.kiwi.features.goals.screens.GoalsModal
import com.bellako.kiwi.features.map.data.MapInfo
import com.bellako.kiwi.features.map.model.MapViewModel
import com.bellako.kiwi.features.nodes.data.NodeStatus
import com.bellako.kiwi.features.nodes.model.INodesViewModel
import com.bellako.kiwi.features.nodes.screens.NodeAction
import com.bellako.kiwi.features.nodes.screens.NodeConnections
import com.bellako.kiwi.features.nodes.screens.NodeOnMap
import com.bellako.kiwi.features.nodes.screens.distance
import com.bellako.kiwi.features.nodes.screens.screenToMap
import com.bellako.kiwi.features.notifications.model.NotificationManager
import com.bellako.kiwi.features.notifications.screens.NotificationOverlay
import com.bellako.kiwi.features.quests.model.IQuestsViewModel
import com.bellako.kiwi.features.quests.screens.QuestNotificationsOverlay
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import com.bellako.kiwi.ui.getScreenHeight
import com.bellako.kiwi.ui.getScreenWidth
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.math.min

@RequiresApi(Build.VERSION_CODES.O)
@Composable
@Suppress("ComplexMethod", "LongMethod")
fun MapScreen(
    maxZoom: Float = 8f,
    mapMarginFactor: Float = 0.08f,
    elasticityFactor: Float = 1.4f,
    title: String = "MINDVEIL",
    mapViewModel: MapViewModel,
    nodesViewModel: INodesViewModel,
    questsViewModel: IQuestsViewModel,
    goalsViewModel: IGoalsViewModel,
    notificationManager: NotificationManager,
    navController: NavHostController,
) {
    val kiwiColors = LocalKiwiColors.current
    val density = LocalDensity.current

    @Suppress("MagicNumber")
    val viewportHeightPx =
        with(density) { getScreenHeight().dp.toPx() } * 0.84f // approximate usable space
    val viewportWidthPx = with(density) { getScreenWidth().dp.toPx() }

    val mapState by mapViewModel.state.collectAsState()
    val imageBitmap = ImageBitmap.imageResource(id = mapState.mapInfo.mapResourceId)
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
        nodesViewModel.loadNodes(0)

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

    val goalsModalRequest = remember { mutableStateOf<Pair<GoalModalType, List<IGoal>>?>(null) }

    LaunchedEffect(Unit) {
        goalsViewModel.checkAndNotifyGoals(
            onYesterdayClick = { goals ->
                goalsModalRequest.value = Pair(GoalModalType.YESTERDAY, goals)
            },
            onTodayClick = { goals ->
                goalsModalRequest.value = Pair(GoalModalType.NEW, goals)
            },
        )
    }

    LaunchedEffect(Unit) {
        mapViewModel.setBackgroundColor(kiwiColors.colorOcean)

        listenToEvent(EventType.SWITCH_MAP) { eventPayload ->
            val payload = eventPayload as EventPayload.SwitchMapPayload
            mapViewModel.switchMap(payload.mapInfo)
        }
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

            InteractiveMap(
                mapResourceId = mapState.mapInfo.mapResourceId,
                mapViewModel = mapViewModel,
                nodesViewModel = nodesViewModel,
                modifier = Modifier.fillMaxSize(),
            )
        }

        @Suppress("MagicNumber")
        QuestNotificationsOverlay(
            questsViewModel,
            navController,
            modifier =
                Modifier
                    .fillMaxSize()
                    .zIndex(10f),
        )

        @Suppress("MagicNumber")
        NotificationOverlay(
            notificationManager = notificationManager,
            modifier =
                Modifier
                    .fillMaxSize()
                    .zIndex(11f),
        )

        @Suppress("MagicNumber")
        goalsModalRequest.value?.let { (type, goals) ->
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .zIndex(12f),
            ) {
                GoalsModal(
                    type,
                    goals,
                    goalsViewModel,
                    onDismiss = {
                        goalsModalRequest.value = null
                        notificationManager.dismissRequests
                    },
                )
            }
        }
    }
}

@OptIn(DelicateCoroutinesApi::class)
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

    val kiwiColors = LocalKiwiColors.current

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
        Background(mapViewModel)

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

                                // TODO: Testing for map switching
                                if (id.toInt() == 7) {
                                    GlobalScope.launch(Dispatchers.Main) {
                                        EventBus.emitEvent(
                                            EventType.SWITCH_MAP,
                                            EventPayload.SwitchMapPayload(
                                                MapInfo(
                                                    mapResourceId = R.drawable.map_switch_test,
                                                    maxZoom = 16f,
                                                    backgroundColor = kiwiColors.colorF,
                                                    mapId = 1,
                                                ),
                                            ),
                                        )
                                    }
                                }
                            },
                        )
                    }
            }
        }
    }
}

@Composable
fun Background(mapViewModel: MapViewModel) {
    val mapState = mapViewModel.state.collectAsState()

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(mapState.value.mapInfo.backgroundColor),
    )
}
