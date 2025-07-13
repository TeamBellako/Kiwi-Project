package com.bellako.kiwi

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bellako.kiwi.features.map.MapScreen
import com.bellako.kiwi.features.map.MapViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MapScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var viewModel: MapViewModel

    @Before
    fun setUp() {
        viewModel = MapViewModel()

        viewModel.setParameters(
            initialScale = 4.0f,
            minScale = 2.0f,
            maxScale = 8.0f,
            initialPositionFactor = 0.8f,
            dragLimitFactor = 0.9f
        )

        composeTestRule.setContent {
            MapScreen(
                viewModel = viewModel,
                initialZoom = 4.0f,
                minZoom = 2.0f,
                maxZoom = 8.0f,
                initialPositionFactor = 0.8f,
                dragLimitFactor = 0.9f,
                mapResourceId = R.drawable.ph_home_map,
                contentDescription = "Test Map",
                title = "Test Map"
            )
        }

        composeTestRule.waitForIdle()
    }

    @Test
    fun testZoomIn() {
        val initialScale = viewModel.state.value.scale

        viewModel.updateScale(1.5f, Offset(150f, 150f))
        composeTestRule.waitForIdle()

        val newScale = viewModel.state.value.scale

        assert(newScale > initialScale)
        assert(newScale <= 8f)
    }

    @Test
    fun testZoomOut() {
        viewModel.updateScale(1.5f, Offset(150f, 150f))
        composeTestRule.waitForIdle()

        val initialScale = viewModel.state.value.scale
        viewModel.updateScale(0.7f, Offset(150f, 150f))
        composeTestRule.waitForIdle()

        val newScale = viewModel.state.value.scale
        assert(newScale < initialScale)
        assert(newScale >= 2f)
    }

    @Test
    fun testDragWithinBounds() {
        val initialOffset = viewModel.state.value.offset

        composeTestRule.onRoot().performTouchInput {
            swipe(start = Offset(200f, 200f), end = Offset(100f, 100f))
        }

        assert(viewModel.state.value.offset != initialOffset)
    }

    @Test
    fun testDragOutOfBounds() {
        viewModel.updateDimensions(
            mapWidth = 500f,
            mapHeight = 500f,
            viewportWidth = 300f,
            viewportHeight = 300f
        )

        val maxOffsetX = (500f * viewModel.state.value.scale - 300f) / 2
        val maxOffsetY = (500f * viewModel.state.value.scale - 300f) / 2
        composeTestRule.onRoot().performTouchInput {
            swipe(start = Offset(200f, 200f), end = Offset(0f, 0f))
        }

        val currentOffset = viewModel.state.value.offset
        val currentMaxOffsetX = (500f * viewModel.state.value.scale - 300f) / 2
        val currentMaxOffsetY = (500f * viewModel.state.value.scale - 300f) / 2
        assert(currentOffset.x <= currentMaxOffsetX)
        assert(currentOffset.y <= currentMaxOffsetY)
    }
}