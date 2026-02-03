package com.bellako.kiwi

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipe
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bellako.kiwi.audio.AudioManager
import com.bellako.kiwi.common.tests.CommonTestTags
import com.bellako.kiwi.features.goals.tests.GoalsFakeViewModel
import com.bellako.kiwi.features.map.model.MapViewModel
import com.bellako.kiwi.features.map.screens.MapScreen
import com.bellako.kiwi.features.nodes.tests.NodesFakeViewModel
import com.bellako.kiwi.features.notifications.controller.NotificationManager
import com.bellako.kiwi.features.quests.tests.QuestsFakeViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MapScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var viewModel: MapViewModel
    private lateinit var nodesModel: NodesFakeViewModel
    private lateinit var questsFakeViewModel: QuestsFakeViewModel
    private lateinit var goalsFakeViewModel: GoalsFakeViewModel

    private val maxZoom = 8f

    @Before
    fun setUp() {
        AudioManager.setEnabled(false)

        viewModel = MapViewModel()
        nodesModel = NodesFakeViewModel()
        goalsFakeViewModel = GoalsFakeViewModel()
        questsFakeViewModel = QuestsFakeViewModel()

        composeTestRule.setContent {
            val navController = rememberNavController()
            MapScreen(
                maxZoom = maxZoom,
                nodesViewModel = nodesModel,
                mapViewModel = viewModel,
                questsViewModel = questsFakeViewModel,
                goalsViewModel = goalsFakeViewModel,
                navController = navController,
                notificationManager = NotificationManager(),
            )
        }

        composeTestRule.waitForIdle()
    }

    @Test
    fun testZoomIn() {
        val initialScale = viewModel.state.value.scale

        viewModel.updateScale(1.5f, Offset(0f, 0f))
        composeTestRule.waitForIdle()

        val newScale = viewModel.state.value.scale

        assert(newScale > initialScale)
        assert(newScale <= maxZoom)
    }

    @Test
    fun testZoomOut() {
        viewModel.updateScale(1.5f, Offset(0f, 0f))
        composeTestRule.waitForIdle()

        val initialScale = viewModel.state.value.scale
        viewModel.updateScale(0.7f, Offset(0f, 0f))
        composeTestRule.waitForIdle()

        val newScale = viewModel.state.value.scale
        assert(newScale < initialScale)
    }

    @Test
    fun testDragOutOfBounds() {
        val initialOffset = viewModel.state.value.offset

        val screenWidth = viewModel.state.value.viewportWidthPx
        val screenHeight = viewModel.state.value.viewportHeightPx
        val mapHeight = viewModel.state.value.mapHeightPx
        val initialSwipe = Offset(screenWidth / 2f, screenHeight / 2f)

        composeTestRule.onNodeWithTag(CommonTestTags.HOME_SCREEN).performTouchInput {
            swipe(start = initialSwipe + Offset(mapHeight * 2, 0f), end = initialSwipe)
        }

        assert(viewModel.state.value.offset == initialOffset)
    }
}
