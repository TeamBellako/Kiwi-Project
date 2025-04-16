package com.bellako.kiwi.ui

import HelloCard
import HelloState
import HelloViewModel
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.bellako.kiwi.network.APIService
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class HelloCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Mock
    lateinit var apiService: APIService

    @InjectMocks
    lateinit var viewModel: HelloViewModel

    // Create a MutableState to mock the state
    lateinit var mockState: MutableState<HelloState>

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        // Initialize MutableState
        mockState = mutableStateOf(HelloState())
    }

    @Test
    fun testLoadingState() = runTest {
        mockState.value = HelloState(status = "loading")
        viewModel.state = mockState

        composeTestRule.setContent {
            HelloCard(viewModel = viewModel, id = 1)
        }

        composeTestRule.onNodeWithText("Loading...").assertExists()
    }

    @Test
    fun testSuccessState() = runTest {
        val mockMessage = "Hello World!"
        mockState.value = HelloState(status = "succeeded", message = mockMessage)
        viewModel.state = mockState

        composeTestRule.setContent {
            HelloCard(viewModel = viewModel, id = 1)
        }

        composeTestRule.onNodeWithText("Message: $mockMessage").assertExists()
    }

    @Test
    fun testFailureState() = runTest {
        val errorMessage = "Failed to fetch"
        mockState.value = HelloState(status = "failed", message = errorMessage)
        viewModel.state = mockState

        composeTestRule.setContent {
            HelloCard(viewModel = viewModel, id = 1)
        }

        composeTestRule.onNodeWithText("Error: $errorMessage").assertExists()
    }
}
