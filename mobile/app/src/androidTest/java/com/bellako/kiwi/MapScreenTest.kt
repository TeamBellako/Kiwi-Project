package com.bellako.kiwi

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipe
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bellako.kiwi.audio.AudioManager
import com.bellako.kiwi.features.map.model.MapViewModel
import com.bellako.kiwi.features.map.screens.MapScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MapScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var viewModel: MapViewModel

    private val minZoom = 1.5f
    private val maxZoom = 6f

    @Before
    fun setUp() {
        AudioManager.setEnabled(false)

        viewModel = MapViewModel()

        composeTestRule.setContent {
            MapScreen(
                minZoom = minZoom,
                maxZoom = maxZoom,
                initialZoom = 2f,
                initialPosition = Offset(0f, 0f),
                dragLimitFactor = 1f,
                viewModel = viewModel,
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
        assert(newScale >= minZoom)
    }

    @Test
    fun testDragWithinBounds() {
        val initialOffset = viewModel.state.value.offset

        val screenWidth = viewModel.state.value.viewportWidthPx
        val screenHeight = viewModel.state.value.viewportHeightPx
        val mapHeight = viewModel.state.value.mapHeightPx
        val initialSwipe = Offset(screenWidth / 2f, screenHeight / 2f)

        composeTestRule.onRoot().performTouchInput {
            swipe(start = initialSwipe, end = initialSwipe + Offset(0f, mapHeight * 0.25f))
        }

        assert(viewModel.state.value.offset != initialOffset)
        val maxOffsetX = viewModel.getMaxOffset(viewModel.state.value).x
        assert(viewModel.state.value.offset.x > -maxOffsetX)
        assert(viewModel.state.value.offset.x < maxOffsetX)
    }

    @Test
    fun testDragOutOfBounds() {
        val initialOffset = viewModel.state.value.offset

        val screenWidth = viewModel.state.value.viewportWidthPx
        val screenHeight = viewModel.state.value.viewportHeightPx
        val mapHeight = viewModel.state.value.mapHeightPx
        val initialSwipe = Offset(screenWidth / 2f, screenHeight / 2f)

        composeTestRule.onRoot().performTouchInput {
            swipe(start = initialSwipe + Offset(0f, mapHeight * 2), end = initialSwipe)
        }

        assert(viewModel.state.value.offset == initialOffset)
    }
}
