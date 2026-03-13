package com.bellako.kiwi

import com.bellako.kiwi.common.data.UIState
import com.bellako.kiwi.features.nodes.data.NodeStatus
import com.bellako.kiwi.features.nodes.model.NodesRepository
import com.bellako.kiwi.features.nodes.model.NodesViewModel
import com.bellako.kiwi.features.nodes.tests.NodesTestFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class NodesIntegrationTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: NodesRepository
    private lateinit var viewModel: NodesViewModel

    @Before
    fun setUp() {
        repository = mock(NodesRepository::class.java)
        viewModel = NodesViewModel(repository)
    }

    // -------------------------------------------------------------------------
    // Load nodes
    // -------------------------------------------------------------------------

    @Test
    fun `loadNodes loads nodes with correct statuses`() =
        runTest(mainDispatcherRule.dispatcher) {
            val nodesState = NodesTestFactory.validNodesState()
            val nodes = nodesState.nodes.values.toList()

            whenever(repository.getNodesByMapId(0)).thenReturn(nodes)

            viewModel.loadNodes(0)
            advanceUntilIdle()

            val state = viewModel.state.value
            assertEquals(4, state.nodes.size)

            assertEquals(NodeStatus.COMPLETED, state.nodes[1L]?.status)
            assertEquals(NodeStatus.COMPLETED, state.nodes[2L]?.status)
            assertEquals(NodeStatus.OPEN, state.nodes[3L]?.status)
            assertEquals(NodeStatus.LOCKED, state.nodes[4L]?.status)
        }

    // -------------------------------------------------------------------------
    // Unlock node
    // -------------------------------------------------------------------------

    @Test
    fun `unlockNode changes status from LOCKED to OPEN`() =
        runTest(mainDispatcherRule.dispatcher) {
            val initialState = NodesTestFactory.validNodesState()

            whenever(repository.getNodesByMapId(0))
                .thenReturn(initialState.nodes.values.toList())

            val lockedNode = initialState.nodes[4L]!!

            whenever(repository.unlockNode(4L))
                .thenReturn(
                    arrayOf(Array) lockedNode.copy(status = NodeStatus.OPEN),
                )

            viewModel.loadNodes(0)
            advanceUntilIdle()

            viewModel.unlockNode(4L)
            advanceUntilIdle()

            val state = viewModel.state.value
            assertEquals(NodeStatus.OPEN, state.nodes[4L]?.status)
            assertTrue(viewModel.uiState.value is UIState.Success)
        }

    // -------------------------------------------------------------------------
    // Complete node
    // -------------------------------------------------------------------------

    @Test
    fun `completeNode changes status from OPEN to COMPLETED`() =
        runTest(mainDispatcherRule.dispatcher) {
            val initialState = NodesTestFactory.validNodesState()

            whenever(repository.getNodesByMapId(0))
                .thenReturn(initialState.nodes.values.toList())

            val openNode = initialState.nodes[3L]!!
            assertEquals(NodeStatus.OPEN, openNode.status)

            whenever(repository.completeNode(3L))
                .thenReturn(
                    listOf(
                        openNode.copy(status = NodeStatus.COMPLETED),
                    ),
                )

            viewModel.loadNodes(0)
            advanceUntilIdle()

            viewModel.completeNode(3L)
            advanceUntilIdle()

            val state = viewModel.state.value
            assertEquals(NodeStatus.COMPLETED, state.nodes[3L]?.status)
            assertTrue(viewModel.uiState.value is UIState.Success)
        }
}
