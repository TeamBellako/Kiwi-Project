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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import com.bellako.kiwi.features.dashboard.screens.DashboardLayout
import com.bellako.kiwi.features.goals.model.IGoalsViewModel
import com.bellako.kiwi.features.map.data.MapsInfo
import com.bellako.kiwi.features.map.model.MapViewModel
import com.bellako.kiwi.features.nodes.data.NodeStatus
import com.bellako.kiwi.features.nodes.data.NodeTransitionStyle
import com.bellako.kiwi.features.nodes.model.INodesViewModel
import com.bellako.kiwi.features.nodes.screens.LocalNodeEntryTransition
import com.bellako.kiwi.features.nodes.screens.NodeAction
import com.bellako.kiwi.features.nodes.screens.NodeConnections
import com.bellako.kiwi.features.nodes.screens.NodeEntryTransitionController
import com.bellako.kiwi.features.nodes.screens.NodeOnMap
import com.bellako.kiwi.features.nodes.screens.distance
import com.bellako.kiwi.features.nodes.screens.rememberNodeReveal
import com.bellako.kiwi.features.nodes.screens.screenToMap
import com.bellako.kiwi.features.users.model.IUsersViewModel
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import com.bellako.kiwi.ui.getScreenHeight
import com.bellako.kiwi.ui.getScreenWidth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.math.min

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MapScreen(
    maxZoom: Float = 8f,
    mapMarginFactor: Float = 0.08f,
    elasticityFactor: Float = 1.4f,
    mapViewModel: MapViewModel,
    nodesViewModel: INodesViewModel,
    goalsViewModel: IGoalsViewModel,
    usersViewModel: IUsersViewModel,
) {
    val kiwiColors = LocalKiwiColors.current
    val density = LocalDensity.current

    val userState by usersViewModel.state.collectAsState()
    val currentPoints = userState?.currentPoints ?: 0

    @Suppress("MagicNumber")
    val viewportHeightPx =
        with(density) { getScreenHeight().dp.toPx() } * 0.84f // approximate usable space
    val viewportWidthPx = with(density) { getScreenWidth().dp.toPx() }

    var revealStarted by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        listenToEvent(EventType.MAP_REVEAL) {
            revealStarted = true
        }
    }

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

    LaunchedEffect(Unit) {
        loadNodes(mapViewModel, nodesViewModel, 0)
    }

    LaunchedEffect(Unit) {
        goalsViewModel.checkAndNotifyGoals()
    }

    LaunchedEffect(Unit) {
        mapViewModel.setBackgroundColor(kiwiColors.colorOcean)

        listenToEvent(EventType.SWITCH_MAP) { eventPayload ->
            val payload = eventPayload as EventPayload.EntityIdPayload
            val mapInfo = MapsInfo.findMapById(payload.targetEntityId)

            mapViewModel.switchMap(mapInfo)
            loadNodes(mapViewModel, nodesViewModel, mapInfo.mapId)
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
                    mapState.mapInfo.mapTitle,
                    color = kiwiColors.colorF,
                    modifier =
                        Modifier
                            .padding(0.dp, getResponsiveSizeHeight(Spacing.small))
                            .zIndex(1f),
                ),
            )

            InteractiveMap(
                mapResourceId = mapState.mapInfo.mapResourceId,
                mapViewModel = mapViewModel,
                nodesViewModel = nodesViewModel,
                currentPoints = currentPoints,
                revealStarted = revealStarted,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

private fun loadNodes(
    mapViewModel: MapViewModel,
    nodesViewModel: INodesViewModel,
    mapId: Int,
) {
    nodesViewModel.loadNodes(mapId)

    nodesViewModel.state
        .onEach { nodesState ->
            val nodesMap = nodesState?.nodes ?: return@onEach
            if (nodesMap.isNotEmpty()) {
                val nodesList = nodesMap.values.toList()

                val lastOpen = nodesList.lastOrNull { it.status == NodeStatus.OPEN }
                val lastCompleted = nodesList.lastOrNull { it.status == NodeStatus.COMPLETED }
                val defaultNode = nodesList.firstOrNull()

                val selectedNode = lastOpen ?: lastCompleted ?: defaultNode

                selectedNode?.let { node ->
                    mapViewModel.selectNode(node.id, node.cordX, node.cordY, animate = false)
                    mapViewModel.setPlayerNode(node.id)
                }
            }
        }.launchIn(CoroutineScope(Dispatchers.Main))
}

@Suppress("LongMethod")
@Composable
private fun InteractiveMap(
    mapResourceId: Int,
    mapViewModel: MapViewModel,
    nodesViewModel: INodesViewModel,
    currentPoints: Int,
    revealStarted: Boolean,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val mapState by mapViewModel.state.collectAsState()
    val nodesState by nodesViewModel.state.collectAsState()
    val revealConsumed by mapViewModel.revealConsumed.collectAsState()

    val nodesMap = nodesState?.nodes.orEmpty()
    val focusedNodeId = mapState.selectedNodeId ?: mapState.playerNode.takeIf { it != 0L }
    val (revealSchedule, revealClockMs) =
        rememberNodeReveal(
            nodes = nodesMap,
            rootNodeId = focusedNodeId,
            started = revealStarted,
            alreadyPlayed = revealConsumed,
            onRevealConsumed = mapViewModel::markRevealConsumed,
        )

    val nodeEntry = LocalNodeEntryTransition.current
    val nodeEntryScope = rememberCoroutineScope()

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

                                    clickedNode?.let { node ->
                                        mapViewModel.selectNode(node.id, node.cordX, node.cordY)

                                        CoroutineScope(Dispatchers.Main).launch {
                                            EventBus.emitEvent(
                                                EventType.CHANGE_DASHBOARD_LAYOUT,
                                                EventPayload.ChangeDashboardLayoutPayload(DashboardLayout.HIDDEN),
                                            )
                                        }
                                    }
                                }
                            }
                        },
            )

            // NODE CONNECTIONS
            NodeConnections(
                nodes = nodesMap,
                mapState = mapState,
                modifier = Modifier.fillMaxSize(),
                edgeReveal = { fromId, toId ->
                    revealSchedule.edgeReveal(fromId, toId, revealClockMs)
                },
            )
        }

        // NODES
        nodesMap.values.forEach { node ->
            NodeOnMap(
                node = node,
                mapState = mapState,
                isPlayerNode = node.id == mapState.playerNode,
                isSelected = node.id == mapState.selectedNodeId,
                revealScale = revealSchedule.nodeScale(node.id, revealClockMs),
                nameAlpha = revealSchedule.labelAlpha(node.id, revealClockMs),
            )
        }

        // NODE ACTION BUTTON
        if (!mapState.isFocusingNode) {
            mapState.selectedNodeId?.let { selectedNodeId ->
                nodesState
                    ?.nodes[selectedNodeId]
                    ?.let { selectedNode ->
                        AudioManager.playSFX(context, R.raw.snd_fx_04_seleccion)

                        // Same gating as the node's label: stay hidden until the
                        // focused node's pop has finished, then fade in.
                        val actionAlpha = revealSchedule.labelAlpha(selectedNodeId, revealClockMs)

                        Box(
                            modifier = Modifier.fillMaxSize().alpha(actionAlpha),
                            contentAlignment = Alignment.TopCenter,
                        ) {
                            val centerOffset =
                                with(LocalDensity.current) {
                                    (mapState.viewportHeightPx / 2f).toDp()
                                }

                            NodeAction(
                                node = selectedNode,
                                onUnlockNode = { id ->
                                    nodesViewModel.unlockNode(id)
                                    mapViewModel.setPlayerNode(id)
                                },
                                onCompleteNode = { id ->
                                    nodesViewModel.completeNode(id)
                                    AudioManager.playSFX(context, R.raw.snd_node_completed)

                                    runNodeEntry(
                                        scope = nodeEntryScope,
                                        nodeEntry = nodeEntry,
                                        style = selectedNode.transitionStyle,
                                    ) {
                                        if (selectedNode.onExecutionEvent != "_") {
                                            EventBus.emitEvent(
                                                EventType.valueOf(selectedNode.onExecutionEvent),
                                                EventPayload.EntityIdPayload(selectedNode.onExecutionEntityId),
                                            )
                                        }
                                    }
                                },
                                onRetryNode = { _ ->
                                    runNodeEntry(
                                        scope = nodeEntryScope,
                                        nodeEntry = nodeEntry,
                                        style = selectedNode.transitionStyle,
                                    ) {
                                        if (selectedNode.onExecutionEvent != "_") {
                                            EventBus.emitEvent(
                                                EventType.valueOf(selectedNode.onExecutionEvent),
                                                EventPayload.EntityIdPayload(selectedNode.onExecutionEntityId),
                                            )
                                        }
                                    }
                                },
                                modifier =
                                    Modifier.offset(
                                        y = centerOffset + getResponsiveSizeHeight(26.dp),
                                    ),
                                currentPoints = currentPoints,
                            )
                        }
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

// Fallback hold before auto-dismissing the veil. The follow-up screen (e.g.
// ConversationScreen) is expected to call fadeOut() itself once its own intro
// is settled, so the player never glimpses the map behind a half-faded
// reveal. This is just a safety net for follow-ups that don't self-dismiss.
private const val FALLBACK_VEIL_HOLD_MS = 1_500L

/**
 * Plays the veil transition (fade in + brief hold), runs [onVeilReached]
 * (typically emitting the event that mounts the next sequence), then
 * schedules a fallback fade-out. The follow-up screen can dismiss the veil
 * earlier by calling [NodeEntryTransitionController.fadeOut] itself —
 * [NodeEntryTransitionController.fadeOut] is idempotent, so the fallback
 * won't fight an earlier dismiss. If no controller is provided (preview/tests)
 * the callback runs immediately.
 */
private fun runNodeEntry(
    scope: CoroutineScope,
    nodeEntry: NodeEntryTransitionController?,
    style: NodeTransitionStyle,
    onVeilReached: suspend () -> Unit,
) {
    if (nodeEntry == null || style == NodeTransitionStyle.IMMEDIATE) {
        scope.launch { onVeilReached() }
        return
    }
    scope.launch {
        nodeEntry.enter()
        onVeilReached()
        delay(FALLBACK_VEIL_HOLD_MS)
        nodeEntry.fadeOut()
    }
}
