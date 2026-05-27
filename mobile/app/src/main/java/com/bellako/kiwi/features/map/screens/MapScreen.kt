package com.bellako.kiwi.features.map.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInBack
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
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
import com.bellako.kiwi.common.screens.components.Kiwi_P1
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer_Horizontal
import com.bellako.kiwi.common.services.eventbus.EventBus
import com.bellako.kiwi.common.services.eventbus.EventPayload
import com.bellako.kiwi.common.services.eventbus.EventType
import com.bellako.kiwi.common.services.eventbus.listenToEvent
import com.bellako.kiwi.common.tests.CommonTestTags
import com.bellako.kiwi.common.utils.detectTransformGesturesAndEnd
import com.bellako.kiwi.features.dashboard.screens.DashboardLayout
import com.bellako.kiwi.features.goals.model.IGoalsViewModel
import com.bellako.kiwi.features.map.data.MapState
import com.bellako.kiwi.features.map.data.MapsInfo
import com.bellako.kiwi.features.map.model.MapViewModel
import com.bellako.kiwi.features.nodes.data.NodeStatus
import com.bellako.kiwi.features.nodes.data.NodeTransitionStyle
import com.bellako.kiwi.features.nodes.data.NodesDomain
import com.bellako.kiwi.features.nodes.model.INodesViewModel
import com.bellako.kiwi.features.nodes.screens.LocalNodeEntryTransition
import com.bellako.kiwi.features.nodes.screens.NodeAction
import com.bellako.kiwi.features.nodes.screens.NodeConnections
import com.bellako.kiwi.features.nodes.screens.NodeEntryTransitionController
import com.bellako.kiwi.features.nodes.screens.NodeOnMap
import com.bellako.kiwi.features.nodes.screens.distance
import com.bellako.kiwi.features.nodes.screens.rememberNodeReveal
import com.bellako.kiwi.features.nodes.screens.rememberUnlockRevealOverlay
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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.math.min

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MapScreen(
    mapMarginFactor: Float = 0.08f,
    elasticityFactor: Float = 1.4f,
    mapViewModel: MapViewModel,
    nodesViewModel: INodesViewModel,
    goalsViewModel: IGoalsViewModel,
    usersViewModel: IUsersViewModel,
    isDialogueOverlaid: Boolean = false,
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
    val nodesState by nodesViewModel.state.collectAsState()
    val nodesMap = nodesState?.nodes.orEmpty()

    val imageBitmap = ImageBitmap.imageResource(id = mapState.mapInfo.mapResourceId)
    val imageW = imageBitmap.width.toFloat()
    val imageH = imageBitmap.height.toFloat()

    val fitScale = min(viewportWidthPx / imageW, viewportHeightPx / imageH)

    val displayWidthPx = imageW * fitScale
    val displayHeightPx = imageH * fitScale
    val mapDisplayWidthDp = with(density) { displayWidthPx.toDp() }
    val mapDisplayHeightDp = with(density) { displayHeightPx.toDp() }

    LaunchedEffect(
        mapState.mapInfo.minZoom,
        mapState.mapInfo.maxZoom,
        displayWidthPx,
        displayHeightPx,
        viewportWidthPx,
        viewportHeightPx,
    ) {
        mapViewModel.setParameters(
            minScale = mapState.mapInfo.minZoom,
            maxScale = mapState.mapInfo.maxZoom,
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

    // Fresh-account safety net: after a brand-new sign-up the user can land
    // here with every node still INACCESSIBLE/LOCKED — all hidden under the
    // mist — so the map reads as empty. If no node has been touched yet, the
    // helper auto-unlocks and auto-executes the first one so the opening beat
    // fires. Sign-up normally drives this explicitly under the loading
    // curtain; this catches the case where that round-trip failed.
    LaunchedEffect(Unit) {
        runAutoExecuteFirstNodeIfNeeded(nodesViewModel, mapViewModel)
    }

    LaunchedEffect(Unit) {
        goalsViewModel.checkAndNotifyGoals()
    }

    // Refresh the player's points whenever the map is shown so the indicator
    // is in sync with the server — login already fetches once, but a stale
    // map re-entry (or a sign-up flow that bypassed the login refresh) would
    // otherwise show 0 until a node unlock / goal completion triggers a sync.
    LaunchedEffect(Unit) {
        usersViewModel.getMyUserPoints()
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

    // Measured height of the title overlay. Drives:
    //   (a) the top padding on InteractiveMap, so the map content stays where
    //       the Column-based layout used to put it (the play-button anchor
    //       depends on this offset);
    //   (b) the y-offset MapMist uses to align its holes with the
    //       InteractiveMap content center, since MapMist now draws full-screen.
    var topInsetPx by remember { mutableIntStateOf(0) }
    val topInsetDp = with(density) { topInsetPx.toDp() }

    // Full-screen conversations and combats emit MAP_COVERED / MAP_UNCOVERED
    // so we can shut off the per-frame VFX loops (mist drift, cloud frame
    // ticker, water shader) while the map is fully hidden. AND-ed with the
    // outer LocalMapVfxEnabled so tests that disable VFX globally still win.
    var mapCovered by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        listenToEvent(EventType.MAP_COVERED) { mapCovered = true }
    }
    LaunchedEffect(Unit) {
        listenToEvent(EventType.MAP_UNCOVERED) { mapCovered = false }
    }
    val baseVfxEnabled = LocalMapVfxEnabled.current
    val vfxEnabled = baseVfxEnabled && !mapCovered

    CompositionLocalProvider(LocalMapVfxEnabled provides vfxEnabled) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(kiwiColors.color2)
                    .testTag(CommonTestTags.HOME_SCREEN),
        ) {
            InteractiveMap(
                mapResourceId = mapState.mapInfo.mapResourceId,
                mapViewModel = mapViewModel,
                nodesViewModel = nodesViewModel,
                currentPoints = currentPoints,
                revealStarted = revealStarted,
                isDialogueOverlaid = isDialogueOverlaid,
                modifier = Modifier.fillMaxSize().padding(top = topInsetDp),
            )

            // Mist covers the FULL screen, including behind the title and the
            // points indicator. zIndex sits between the map content (default 0)
            // and the top-bar UI (zIndex 1).
            MapMist(
                nodes = nodesMap,
                mapState = mapState,
                topInsetPx = topInsetPx.toFloat(),
                modifier = Modifier.fillMaxSize().zIndex(MIST_Z_INDEX),
            )

            // Clouds sit above the mist (so they read as overhead sky reinforcing
            // the mist cover) but below the top-bar UI. The wrapper Box mirrors
            // InteractiveMap's positioning — top inset + center alignment — so the
            // graphicsLayer transform places the cloud canvas exactly over the
            // map content. The Canvas itself has no pointer modifier, so map
            // gestures continue to land on InteractiveMap below.
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(top = topInsetDp)
                        .zIndex(CLOUDS_Z_INDEX),
                contentAlignment = Alignment.Center,
            ) {
                MapClouds(
                    nodes = nodesMap,
                    mapState = mapState,
                    modifier =
                        Modifier
                            .size(width = mapDisplayWidthDp, height = mapDisplayHeightDp)
                            .graphicsLayer(
                                scaleX = mapState.scale,
                                scaleY = mapState.scale,
                                translationX = mapState.offset.x,
                                translationY = mapState.offset.y,
                            ),
                )
            }

            Kiwi_H2(
                KiwiTextArguments(
                    mapState.mapInfo.mapTitle,
                    color = kiwiColors.colorF,
                    // offset (not padding) so the title's measured size stays the
                    // pure text-plus-padding box — that's what feeds topInsetPx,
                    // and feeding the xLarge visual offset into the inset would
                    // push InteractiveMap down past where it used to sit.
                    modifier =
                        Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = getResponsiveSizeHeight(Spacing.xLarge))
                            .padding(0.dp, getResponsiveSizeHeight(Spacing.small))
                            .zIndex(1f)
                            .onSizeChanged { topInsetPx = it.height },
                ),
            )

            PointsIndicator(
                currentPoints = currentPoints,
                mapStateFlow = mapViewModel.state,
                modifier =
                    Modifier
                        .align(Alignment.TopEnd)
                        // Match the title's vertical placement so the indicator
                        // sits on the same line as the map name. The pill's own
                        // border + inner padding contributes the small lead the
                        // title gets from its Spacing.small padding, so no extra
                        // outer top padding is needed here.
                        .offset(y = getResponsiveSizeHeight(Spacing.xLarge))
                        .padding(end = getResponsiveSizeHeight(Spacing.medium))
                        .zIndex(1f),
            )
        }
    }
}

private const val MIST_Z_INDEX = 0.5f
private const val CLOUDS_Z_INDEX = 0.7f
private const val POINTS_ANIM_MS = 350

// Timing for the node action card's scale-out while a dialogue covers the
// bottom of the map. EaseInBack matches the locked→unlocked icon shrink so
// the two animations read as part of the same vocabulary.
private const val NODE_ACTION_DIALOGUE_HIDE_MS = 220
private const val NODE_ACTION_DIALOGUE_SHOW_MS = 320

// Hold the indicator at its mounting value until the map's entry focus
// animation has played, so the player can take in the zoom-in before the
// points start changing on screen. The timeout covers cases where the focus
// never starts (e.g., no nodes are available to focus on).
private const val POINTS_AFTER_FOCUS_DELAY_MS = 1000L
private const val POINTS_MAX_ENTRY_WAIT_MS = 3000L
private const val POINTS_POP_UP_MS = 180
private const val POINTS_POP_DOWN_MS = 360
private const val POINTS_POP_SCALE = 1.22f
private const val POINTS_GLOW_FADE_MS = 750
private const val POINTS_GLOW_PEAK_ALPHA = 0.75f
private const val POINTS_GLOW_SCALE = 2.6f
private val POINTS_INFO_BUTTON_SIZE = 24.dp
private val POINTS_INFO_ICON_SIZE = 16.dp

@Composable
private fun PointsIndicator(
    currentPoints: Int,
    mapStateFlow: StateFlow<MapState>,
    modifier: Modifier = Modifier,
) {
    val kiwiColors = LocalKiwiColors.current
    val shape = RoundedCornerShape(percent = 50)

    var showInfoModal by remember { mutableStateOf(false) }

    // displayPoints lags currentPoints until the entry focus animation
    // finishes, so the zoom-in plays before the number starts moving.
    var displayPoints by remember { mutableIntStateOf(currentPoints) }
    var entrySettled by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        withTimeoutOrNull(POINTS_MAX_ENTRY_WAIT_MS) {
            mapStateFlow.first { it.isFocusingNode }
            mapStateFlow.first { !it.isFocusingNode }
        }
        delay(POINTS_AFTER_FOCUS_DELAY_MS)
        entrySettled = true
    }
    LaunchedEffect(currentPoints, entrySettled) {
        if (entrySettled) displayPoints = currentPoints
    }

    // Pop + halo: scale the pill briefly and flash a radial halo around it
    // every time the visible value changes. Skipped on initial composition.
    val popScale = remember { Animatable(1f) }
    val glowAlpha = remember { Animatable(0f) }
    var previousPoints by remember { mutableIntStateOf(displayPoints) }
    LaunchedEffect(displayPoints) {
        if (displayPoints == previousPoints) return@LaunchedEffect
        previousPoints = displayPoints
        launch {
            popScale.animateTo(POINTS_POP_SCALE, tween(POINTS_POP_UP_MS, easing = EaseInOut))
            popScale.animateTo(1f, tween(POINTS_POP_DOWN_MS, easing = EaseOutBack))
        }
        launch {
            glowAlpha.snapTo(POINTS_GLOW_PEAK_ALPHA)
            glowAlpha.animateTo(0f, tween(POINTS_GLOW_FADE_MS, easing = EaseOut))
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        // Halo: an expanded radial gradient that pulses out from the pill on
        // every change. Sits behind the pill (drawn first in the Box).
        Box(
            modifier =
                Modifier
                    .matchParentSize()
                    .scale(POINTS_GLOW_SCALE)
                    .background(
                        brush =
                            Brush.radialGradient(
                                colors =
                                    listOf(
                                        kiwiColors.color7C.copy(alpha = glowAlpha.value),
                                        Color.Transparent,
                                    ),
                            ),
                    ),
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier
                    .scale(popScale.value)
                    .background(
                        color = kiwiColors.color2.copy(alpha = POINTS_BG_ALPHA),
                        shape = shape,
                    ).border(
                        width = 1.dp,
                        color = kiwiColors.color6,
                        shape = shape,
                    ).padding(
                        horizontal = getResponsiveSizeHeight(Spacing.medium),
                        vertical = getResponsiveSizeHeight(Spacing.small),
                    ),
        ) {
            // The number itself rides AnimatedContent. clipToBounds masks the
            // incoming/outgoing values to the slot's bounds, so the slide is
            // hidden behind the pill background — increases scroll down,
            // decreases scroll up.
            AnimatedContent(
                targetState = displayPoints,
                transitionSpec = {
                    val increased = targetState > initialState
                    if (increased) {
                        slideInVertically(
                            animationSpec = tween(POINTS_ANIM_MS, easing = EaseInOut),
                        ) { -it } togetherWith
                            slideOutVertically(
                                animationSpec = tween(POINTS_ANIM_MS, easing = EaseInOut),
                            ) { it }
                    } else {
                        slideInVertically(
                            animationSpec = tween(POINTS_ANIM_MS, easing = EaseInOut),
                        ) { it } togetherWith
                            slideOutVertically(
                                animationSpec = tween(POINTS_ANIM_MS, easing = EaseInOut),
                            ) { -it }
                    }
                },
                modifier = Modifier.clipToBounds(),
                label = "points_change",
            ) { points ->
                Kiwi_P1(
                    KiwiTextArguments(
                        "%,d".format(points),
                        color = kiwiColors.colorF,
                    ),
                )
            }

            Kiwi_Spacer_Horizontal(Spacing.xSmall)

            Box(
                modifier =
                    Modifier
                        .size(getResponsiveSizeHeight(POINTS_INFO_BUTTON_SIZE))
                        .clickable { showInfoModal = true },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = "About points",
                    tint = kiwiColors.color6,
                    modifier = Modifier.size(getResponsiveSizeHeight(POINTS_INFO_ICON_SIZE)),
                )
            }
        }
    }

    if (showInfoModal) {
        PointsInfoModal(onDismiss = { showInfoModal = false })
    }
}

private const val POINTS_BG_ALPHA = 0.7f

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
                    // Force the focus animation to play on every map entry by
                    // clearing the selection first — selectNode() short-circuits
                    // if the target is already selected.
                    mapViewModel.unSelectNode()
                    mapViewModel.selectNode(node.id, node.cordX, node.cordY, animate = true)
                    mapViewModel.setPlayerNode(node.id)
                }
            }
        }.launchIn(CoroutineScope(Dispatchers.Main))
}

private suspend fun runAutoExecuteFirstNodeIfNeeded(
    nodesViewModel: INodesViewModel,
    mapViewModel: MapViewModel,
) {
    val loaded = nodesViewModel.state.first { (it?.nodes?.isNotEmpty()) == true } ?: return
    val initialNodes = loaded.nodes.values.toList()
    if (initialNodes.any { it.status == NodeStatus.OPEN || it.status == NodeStatus.COMPLETED }) return

    val firstNode = initialNodes.first()

    nodesViewModel.unlockNode(firstNode.id)
    nodesViewModel.state.first { it?.nodes?.get(firstNode.id)?.status == NodeStatus.OPEN }
    mapViewModel.setPlayerNode(firstNode.id)

    // Completion is deferred: emitting the start event triggers the linked
    // conversation/combat, which fires COMPLETE_NODE on resolve. The node
    // sits OPEN until then.
    if (firstNode.onExecutionEvent.isNotBlank() && firstNode.onExecutionEvent != "_") {
        EventBus.emitEvent(
            EventType.valueOf(firstNode.onExecutionEvent),
            EventPayload.EntityIdPayload(firstNode.onExecutionEntityId),
        )
    }
}

@Suppress("LongMethod")
@Composable
private fun InteractiveMap(
    mapResourceId: Int,
    mapViewModel: MapViewModel,
    nodesViewModel: INodesViewModel,
    currentPoints: Int,
    revealStarted: Boolean,
    isDialogueOverlaid: Boolean = false,
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

    // Mini-cascade fired every time a node is played and unlocks neighbours.
    // Gated to start only after the initial reveal has handed over, so the
    // first auto-completion under the loading curtain doesn't collide with it.
    // Each cascade also waits for the node-entry veil to be fully lifted —
    // state updates (icon flip to tick, neighbours becoming OPEN) land while
    // the screen is covered, and the visible pop/lerp/pop only plays once
    // the user is looking at the map again.
    val unlockReveal =
        rememberUnlockRevealOverlay(
            nodes = nodesMap,
            enabled = revealConsumed,
            awaitReady = {
                if (nodeEntry != null) {
                    snapshotFlow { nodeEntry.veilAlpha }.first { it <= 0f }
                }
            },
        )

    // Scale-out the selected-node action card while a no-background dialogue
    // is overlaid on the map. The card is normally visible above the dialogue
    // box; popping it would feel abrupt, so we shrink it into the map.
    val dialogueHideScale = remember { Animatable(1f) }
    LaunchedEffect(isDialogueOverlaid) {
        if (isDialogueOverlaid) {
            dialogueHideScale.animateTo(
                targetValue = 0f,
                animationSpec = tween(NODE_ACTION_DIALOGUE_HIDE_MS, easing = EaseInBack),
            )
        } else {
            dialogueHideScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(NODE_ACTION_DIALOGUE_SHOW_MS, easing = EaseOutBack),
            )
        }
    }

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
                            mapViewModel.settleScale()
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

            // WATER VFX — shader overlay gated to water regions by a
            // runtime-generated mask. Sits inside the same transformed Box as
            // the map image so pan/zoom is inherited. Renders above the map
            // and below the node connections so nodes stay visible on top.
            // On API <33 this emits nothing and the map looks unchanged.
            MapWaterOverlay(
                maskResourceId = R.drawable.mindveil_4k_watermask,
                modifier = Modifier.fillMaxSize(),
            )

            // NODE CONNECTIONS
            NodeConnections(
                nodes = nodesMap,
                mapState = mapState,
                modifier = Modifier.fillMaxSize(),
                edgeReveal = { fromId, toId ->
                    unlockReveal.edgeReveal(fromId, toId)
                        ?: revealSchedule.edgeReveal(fromId, toId, revealClockMs)
                },
            )
        }

        // NODES
        // Node status flips (e.g. a completion event) land while the
        // node-entry veil is fully opaque. Gate Node's iconPopScale so the
        // shrink-and-pop only plays once the veil has cleared — otherwise
        // the user sees the new icon already settled in place.
        val iconAnimationReady = (nodeEntry?.veilAlpha ?: 0f) <= 0f
        nodesMap.values.forEach { node ->
            NodeOnMap(
                node = node,
                mapState = mapState,
                isPlayerNode = node.id == mapState.playerNode,
                isSelected = node.id == mapState.selectedNodeId,
                revealScale =
                    unlockReveal.nodeScale(node.id)
                        ?: revealSchedule.nodeScale(node.id, revealClockMs),
                nameAlpha =
                    unlockReveal.labelAlpha(node.id)
                        ?: revealSchedule.labelAlpha(node.id, revealClockMs),
                iconAnimationReady = iconAnimationReady,
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
                        // focused node's pop has finished, then fade in. Routed
                        // through the unlock overlay too so the button doesn't
                        // sit visible above a node that's mid re-pop.
                        val actionAlpha =
                            unlockReveal.labelAlpha(selectedNodeId)
                                ?: revealSchedule.labelAlpha(selectedNodeId, revealClockMs)

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
                                onCompleteNode = { _ ->
                                    AudioManager.playSFX(context, R.raw.snd_node_completed)

                                    // No inline completeNode — the node is now
                                    // completed by the COMPLETE_NODE event the
                                    // linked conversation/combat emits on
                                    // resolve. Just fire the start event.
                                    //
                                    // Nodes whose start event IS already
                                    // COMPLETE_NODE (self-completing, no
                                    // follow-up screen) skip the entry veil —
                                    // there's nothing for it to transition to.
                                    runNodeEntry(
                                        scope = nodeEntryScope,
                                        nodeEntry = nodeEntry,
                                        style = effectiveTransitionStyle(selectedNode),
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
                                        style = effectiveTransitionStyle(selectedNode),
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
                                    Modifier
                                        .offset(
                                            y = centerOffset + getResponsiveSizeHeight(26.dp),
                                        ).scale(dialogueHideScale.value),
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
// reveal. This is just a safety net for follow-ups that don't self-dismiss —
// generous enough to outlast a slow server load plus the conversation's
// slide-in, so it never wins the race against the conversation's own dismiss.
private const val FALLBACK_VEIL_HOLD_MS = 6_000L

// A node whose start event IS the completion event has no follow-up screen
// to fade up under the veil — running the veil over an instant state change
// only flashes the user with darkness for no reason. Treat those as
// IMMEDIATE regardless of how content authoring set the style.
private fun effectiveTransitionStyle(node: NodesDomain): NodeTransitionStyle =
    if (node.onExecutionEvent == EventType.COMPLETE_NODE.name) {
        NodeTransitionStyle.IMMEDIATE
    } else {
        node.transitionStyle
    }

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
