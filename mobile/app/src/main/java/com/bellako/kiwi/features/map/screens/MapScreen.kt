package com.bellako.kiwi.features.map.screens

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
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
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bellako.kiwi.R
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_H2
import com.bellako.kiwi.common.screens.components.Kiwi_Image
import com.bellako.kiwi.common.tests.CommonTestTags
import com.bellako.kiwi.common.utils.DateUtils.dateToString
import com.bellako.kiwi.common.utils.detectTransformGesturesAndEnd
import com.bellako.kiwi.features.dashboard.screens.LocalGoalsViewModel
import com.bellako.kiwi.features.goals.data.GoalCategory
import com.bellako.kiwi.features.goals.data.GoalDomain
import com.bellako.kiwi.features.goals.data.GoalModalType
import com.bellako.kiwi.features.goals.model.GoalsViewModel
import com.bellako.kiwi.features.goals.model.IGoalsViewModel
import com.bellako.kiwi.features.goals.screens.GoalsModal
import com.bellako.kiwi.features.map.model.MapViewModel
import com.bellako.kiwi.features.nodes.data.NodeStatus
import com.bellako.kiwi.features.nodes.model.INodesViewModel
import com.bellako.kiwi.features.nodes.screens.NodeOnMap
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import com.bellako.kiwi.ui.getScreenHeight
import com.bellako.kiwi.ui.getScreenWidth
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import kotlin.math.min

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MapScreen(
    maxZoom: Float = 8f,
    dragLimitFactor: Float = 1f,
    mapMarginFactor: Float = 0.08f,
    elasticityFactor: Float = 0.4f,
    mapResourceId: Int = R.drawable.mindveil_4k,
    title: String = "WORLD MAP",
    nodesViewModel: INodesViewModel,
    mapViewModel: MapViewModel = hiltViewModel(),
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

    // ensure setParameters is called once per composition with stable inputs
    LaunchedEffect(maxZoom, dragLimitFactor, displayWidthPx, displayHeightPx, viewportWidthPx, viewportHeightPx) {
        mapViewModel.setParameters(
            maxScale = maxZoom,
            dragLimitFactor = dragLimitFactor,
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
            .filter { it?.isNotEmpty() == true }
            .first()
            .let { nodesList ->
                val firstOpenOrLocked =
                    nodesList?.firstOrNull { it.status == NodeStatus.OPEN || it.status == NodeStatus.LOCKED }
                firstOpenOrLocked?.let { node ->
                    mapViewModel.selectNode(node.id, node.cordX, node.cordY)
                }
            }
    }

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
            mapResourceId = mapResourceId,
            mapViewModel = mapViewModel,
            nodesViewModel = nodesViewModel,
            modifier = Modifier.fillMaxSize(),
        )
    }
    val localGoalsViewModel = LocalGoalsViewModel.current
    val goalsViewModel: IGoalsViewModel = localGoalsViewModel ?: hiltViewModel<GoalsViewModel>()
    var notTodaygoals by remember { mutableStateOf<List<GoalDomain>>(emptyList()) }
    var goals by remember { mutableStateOf<List<GoalDomain>>(emptyList()) }
//    var newGoals by remember { mutableStateOf<List<GoalDomain>>(emptyList()) }

    @Suppress("ForbiddenComment")
    LaunchedEffect(Unit) {
        val today = dateToString(LocalDate.now())
        val result = goalsViewModel.getGoalsInProgress()
        if (result.isSuccess) {
            goals = result.getOrNull() ?: emptyList()
            if (goals.isNotEmpty()) {
                notTodaygoals = goals.filter { it.date != today }
                goals = goals.filter { it.date == today }
            }
            // TODO: call to defaultGoals for new Goals
        }
//        val prefs = context.getSharedPreferences("kiwi_goals_prefs", Context.MODE_PRIVATE)
//        val lastShownDate = prefs.getString("last_yesterday_goals_check", "")
//        val today = dateToString(LocalDate.now())
//        if (lastShownDate != today) {
//            val result = goalsViewModel.getGoalsByDate(dateToString(LocalDate.now().minusDays(1)))
//            if (result.isSuccess) {
//                goals = result.getOrNull() ?: emptyList()
//                if (goals.isNotEmpty()) {
//                    goals = goals.filter { it.category == GoalCategory.DAILY_CHALLENGES }
//                }
//            }
// //            // Guardar que ya se mostró hoy
// //            prefs.edit().putString("last_yesterday_goals_check", today).apply()
//        }
    }
    GoalsModal(GoalModalType.YESTERDAY, notTodaygoals)
//    GoalsModal(GoalModalType.NEW, newGoals)
}

@Composable
private fun InteractiveMap(
    mapResourceId: Int,
    mapViewModel: MapViewModel,
    nodesViewModel: INodesViewModel,
    modifier: Modifier = Modifier,
) {
    val mapState by mapViewModel.state.collectAsState()
    val nodesState by nodesViewModel.state.collectAsState()

    Box(
        modifier =
            modifier
                .clipToBounds()
                .pointerInput(Unit) {
                    detectTransformGesturesAndEnd(
                        onGesture = { centroid, pan, zoom, _ ->
                            mapViewModel.updateScale(zoom, centroid)
                            mapViewModel.updateOffset(pan)
                        },
                        onGestureEnd = {
                            mapViewModel.startFling()
                            mapViewModel.updatePreviousState()
                        },
                    )
                },
        contentAlignment = Alignment.Center,
    ) {
        Background()

        val imageWidthDp = with(LocalDensity.current) { mapState.mapWidthPx.toDp() }
        val imageHeightDp = with(LocalDensity.current) { mapState.mapHeightPx.toDp() }

        Kiwi_Image(
            painterResourceId = mapResourceId,
            alt = "Interactive Map",
            modifier =
                Modifier
                    .size(width = imageWidthDp, height = imageHeightDp)
                    .graphicsLayer(
                        scaleX = mapState.scale,
                        scaleY = mapState.scale,
                        translationX = mapState.offset.x,
                        translationY = mapState.offset.y,
                    ),
        )

        nodesState?.nodes?.forEach { node ->
            NodeOnMap(
                node = node,
                mapState = mapState,
                isSelected = node.id == mapViewModel.getSelectedNode(),
                onNodeClick = { x, y, id -> mapViewModel.selectNode(id, x, y) },
                onUnlockNode = { id -> nodesViewModel.unlockNode(id) },
                onCompleteNode = { id -> nodesViewModel.completeNode(id) },
            )
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
    // val imageBitmap: ImageBitmap = ImageBitmap.imageResource(id = R.drawable.tile_texture)
    //  Canvas(modifier = Modifier.fillMaxSize()) {
    //    drawTiledBitmap(imageBitmap)
    // }
}
/*
private fun DrawScope.drawTiledBitmap(bitmap: ImageBitmap) {
    val tileWidth = bitmap.width.toFloat()
    val tileHeight = bitmap.height.toFloat()

    var y = 0f
    while (y < size.height) {
        var x = 0f
        while (x < size.width) {
            translate(left = x, top = y) {
                drawImage(bitmap)
            }
            x += tileWidth
        }
        y += tileHeight
    }
}
*/
