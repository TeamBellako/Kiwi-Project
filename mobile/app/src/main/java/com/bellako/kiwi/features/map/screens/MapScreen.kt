package com.bellako.kiwi.features.map.screens

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.R
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_H2
import com.bellako.kiwi.common.screens.components.Kiwi_Image
import com.bellako.kiwi.common.tests.CommonTestTags
import com.bellako.kiwi.common.utils.SECONDS_IN_HOUR
import com.bellako.kiwi.common.utils.detectTransformGesturesAndEnd
import com.bellako.kiwi.features.appbar.screens.AppBarScreen
import com.bellako.kiwi.features.dashboard.screens.DashboardScreen
import com.bellako.kiwi.features.map.model.MapViewModel
import com.bellako.kiwi.features.metrics.data.MetricsState
import com.bellako.kiwi.features.metrics.tests.MetricsFakeViewModel
import com.bellako.kiwi.features.nodes.model.NodesViewModel
import com.bellako.kiwi.features.nodes.screens.NodeOnMap
import com.bellako.kiwi.features.personality.data.PersonalityState
import com.bellako.kiwi.features.personality.tests.PersonalityFakeViewModel
import com.bellako.kiwi.features.personality.tests.PersonalityTestFactory.validPersonalityDTO
import com.bellako.kiwi.features.users.data.UsersState
import com.bellako.kiwi.features.users.tests.UsersFakeViewModel
import com.bellako.kiwi.features.users.tests.UsersTestFactory.validUsersDTO
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import com.bellako.kiwi.ui.getScreenHeight
import com.bellako.kiwi.ui.getScreenWidth

/**
 * @param maxZoom how big (zoom in) the map can be, considering 1 the full map on screen
 * @param dragLimitFactor (0,1) padding for the map limit
 * @param mapResourceId image to show as the map
 * @param title
 */
@Composable
fun MapScreen(
    maxZoom: Float = 8f,
    dragLimitFactor: Float = 1f,
    mapResourceId: Int = R.drawable.map_hd,
    title: String = "WORLD MAP",
) {
    val kiwiColors = LocalKiwiColors.current
    val mapViewModel = hiltViewModel<MapViewModel>()
    val density = LocalDensity.current
    val imageBitmap = ImageBitmap.imageResource(id = mapResourceId)
    val viewportWidthPx = with(density) { getScreenWidth().dp.toPx() }

    @Suppress("MagicNumber")
    val viewportHeightPx =
        with(density) { getScreenHeight().dp.toPx() } * 0.85f

    val scaledMapHeight = imageBitmap.height * (viewportHeightPx / imageBitmap.width)
    val minZoom = viewportHeightPx / scaledMapHeight

    val nodesViewModel = hiltViewModel<NodesViewModel>()
    LaunchedEffect(Unit) {
        nodesViewModel.loadNodes()
    }

    mapViewModel.setParameters(
        minScale = minZoom,
        maxScale = maxZoom,
        initialScale = minZoom,
        dragLimitFactor = dragLimitFactor,
        mapWidthPx = imageBitmap.width.toFloat(),
        mapHeightPx = imageBitmap.height.toFloat(),
        viewportWidthPx = viewportWidthPx,
        viewportHeightPx = viewportHeightPx,
    )

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
}

@Composable
private fun InteractiveMap(
    mapResourceId: Int,
    mapViewModel: MapViewModel,
    nodesViewModel: NodesViewModel,
    modifier: Modifier = Modifier,
) {
    val mapState by mapViewModel.state.collectAsState()
    val nodes by nodesViewModel.nodes.collectAsState()

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
        Kiwi_Image(
            painterResourceId = mapResourceId,
            alt = "Interactive Map",
            modifier =
                Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = mapState.scale * mapState.scaleBase,
                        scaleY = mapState.scale * mapState.scaleBase,
                        translationX = mapState.offset.x,
                        translationY = mapState.offset.y,
                    ),
        )

        nodes.forEach { node ->
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

// -------------------------------------------------------------------------------------------------

@SuppressLint("ViewModelConstructorInComposable")
@RequiresApi(Build.VERSION_CODES.O)
@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Composable
fun MapScreen_Preview() {
    Kiwi_Theme {
        Scaffold(
            bottomBar = {
                AppBarScreen(navController = rememberNavController())
            },
            content = { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues)) {
                    MapScreen()
                    DashboardScreen(
                        usersViewModel =
                            UsersFakeViewModel(
                                UsersState(
                                    validUsersDTO().email,
                                    validUsersDTO().password,
                                    validUsersDTO().registerDate,
                                ),
                            ),
                        metricsViewModel =
                            MetricsFakeViewModel(
                                MetricsState(
                                    date = "2025-06-12",
                                    maxGoodTimeSeconds = 6 * SECONDS_IN_HOUR,
                                    currentGoodTimeSeconds = 1 * SECONDS_IN_HOUR,
                                    maxBadTimeSeconds = 6 * SECONDS_IN_HOUR,
                                    currentBadTimeSeconds = 2 * SECONDS_IN_HOUR,
                                ),
                            ),
                        personalityViewModel =
                            PersonalityFakeViewModel(
                                PersonalityState(
                                    validPersonalityDTO().realName,
                                    validPersonalityDTO().knightName,
                                    validPersonalityDTO().build,
                                    validPersonalityDTO().goodApps,
                                    validPersonalityDTO().badApps,
                                    validPersonalityDTO().neutralApps,
                                ),
                            ),
                    )
                }
            },
        )
    }
}
