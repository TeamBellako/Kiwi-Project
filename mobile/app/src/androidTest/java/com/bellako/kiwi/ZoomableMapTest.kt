package com.bellako.kiwi

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bellako.kiwi.features.map.MapViewModel
import com.bellako.kiwi.ui.components.Kiwi_ZoomableMap
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ZoomableMapTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var viewModel: MapViewModel

    @Before
    fun setUp() {
        viewModel = MapViewModel()

        // Set up the ZoomableMap composable for testing
        composeTestRule.setContent {
            Kiwi_ZoomableMap(
                mapResourceId = R.drawable.ph_home_map,
                contentDescription = "Test Map",
                viewModel = viewModel
            )
        }

        // Wait for the composable to be laid out
        composeTestRule.waitForIdle()
    }

    @Test
    fun testZoomIn() {
        // Get the initial scale
        val initialScale = viewModel.state.value.scale

        // Perform a pinch-to-zoom gesture to zoom in
        composeTestRule.onRoot().performTouchInput {
            // First finger start and end positions
            val firstFingerStart = Offset(100f, 100f)
            val firstFingerEnd = Offset(50f, 50f)
            // Second finger start and end positions
            val secondFingerStart = Offset(200f, 200f)
            val secondFingerEnd = Offset(250f, 250f)

            // Perform pinch gesture
            pinch(
                firstFingerStart,
                secondFingerStart,
                firstFingerEnd,
                secondFingerEnd
            )
        }

        // Verify that the scale has increased
        assert(viewModel.state.value.scale > initialScale) {
            "Scale should increase after pinch-to-zoom in"
        }

        // Verify that the scale is within the allowed limits
        assert(viewModel.state.value.scale <= 4f) {
            "Scale should not exceed the maximum limit of 4f"
        }
    }

    @Test
    fun testZoomOut() {
        // Get the initial scale
        val initialScale = viewModel.state.value.scale

        // Perform a pinch-to-zoom gesture to zoom in
        composeTestRule.onRoot().performTouchInput {
            // First finger start and end positions
            val firstFingerStart = Offset(50f, 50f)
            val firstFingerEnd = Offset(100f, 100f)
            // Second finger start and end positions
            val secondFingerStart = Offset(250f, 250f)
            val secondFingerEnd = Offset(200f, 200f)

            // Perform pinch gesture
            pinch(
                firstFingerStart,
                secondFingerStart,
                firstFingerEnd,
                secondFingerEnd
            )
        }

        // Verify that the scale has increased
        assert(viewModel.state.value.scale > initialScale) {
            "Scale should increase after pinch-to-zoom in"
        }

        // Verify that the scale is within the allowed limits
        assert(viewModel.state.value.scale <= 4f) {
            "Scale should not exceed the maximum limit of 4f"
        }
    }

    @Test
    fun testDragWithinBounds() {
        // Get the initial offset
        val initialOffset = viewModel.state.value.offset

        // Perform a drag gesture
        composeTestRule.onRoot().performTouchInput {
            swipe(start = Offset(200f, 200f), end = Offset(100f, 100f))
        }

        // Verify that the offset has changed
        assert(viewModel.state.value.offset != initialOffset) {
            "Offset should change after drag"
        }
    }

    @Test
    fun testDragOutOfBounds() {
        // Set up the map dimensions and viewport size for testing
        viewModel.updateDimensions(
            mapWidth = 500f,
            mapHeight = 500f,
            viewportWidth = 300f,
            viewportHeight = 300f
        )

        // Calculate the maximum allowed offset
        val maxOffsetX = (500f * viewModel.state.value.scale - 300f) / 2
        val maxOffsetY = (500f * viewModel.state.value.scale - 300f) / 2


        // Perform a large drag gesture that would go out of bounds
        composeTestRule.onRoot().performTouchInput {
            swipe(start = Offset(200f, 200f), end = Offset(0f, 0f))
        }

        // Get the current offset after the drag
        val currentOffset = viewModel.state.value.offset

        // Recalculate max offset with current scale
        val currentMaxOffsetX = (500f * viewModel.state.value.scale - 300f) / 2
        val currentMaxOffsetY = (500f * viewModel.state.value.scale - 300f) / 2

        // Verify that the offset is constrained within the boundaries
        assert(currentOffset.x <= currentMaxOffsetX) {
            "X offset ($currentOffset.x) should not exceed the maximum allowed value ($currentMaxOffsetX)"
        }
        assert(currentOffset.y <= currentMaxOffsetY) {
            "Y offset ($currentOffset.y) should not exceed the maximum allowed value ($currentMaxOffsetY)"
        }
    }
}
