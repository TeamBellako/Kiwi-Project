package com.bellako.kiwi.features.nodes.model

import androidx.lifecycle.viewModelScope
import com.bellako.kiwi.analytics.FirebaseEventNames
import com.bellako.kiwi.analytics.firebaseLogEvent
import com.bellako.kiwi.common.data.UIState
import com.bellako.kiwi.common.model.BaseViewModel
import com.bellako.kiwi.common.services.eventbus.EventPayload
import com.bellako.kiwi.common.services.eventbus.EventType
import com.bellako.kiwi.common.services.eventbus.listenToEvent
import com.bellako.kiwi.common.utils.Logger.warn
import com.bellako.kiwi.features.nodes.data.NodeStatus
import com.bellako.kiwi.features.nodes.data.NodesDomain
import com.bellako.kiwi.features.nodes.data.NodesState
import com.bellako.kiwi.features.users.model.UsersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.security.GeneralSecurityException
import javax.inject.Inject

@HiltViewModel
class NodesViewModel
    @Inject
    constructor(
        private val repository: NodesRepository,
        private val usersRepository: UsersRepository,
    ) : BaseViewModel(),
        INodesViewModel {
        private val _state = MutableStateFlow(NodesState())
        override val state: StateFlow<NodesState> = _state.asStateFlow()

        init {
            viewModelScope.launch {
                listenToEvent(EventType.UNLOCK_NODE) { eventPayload ->
                    val payload = eventPayload as EventPayload.EntityIdPayload
                    unlockNode(payload.targetEntityId.toLong())
                }
            }
            // Completion is now data-driven: a node "starts" something via its
            // onExecutionEvent (START_CNV / START_COMBAT / …) and the started
            // thing emits COMPLETE_NODE on resolve, carrying the originating
            // node id in the payload. Mirrors the start-side dispatch so the
            // node lifecycle is uniformly event-shaped.
            viewModelScope.launch {
                listenToEvent(EventType.COMPLETE_NODE) { eventPayload ->
                    val payload = eventPayload as EventPayload.EntityIdPayload
                    completeNode(payload.targetEntityId.toLong())
                }
            }
        }

        // -----------------------------------------------------------------------------------------

        override fun loadNodes(mapId: Int) {
            viewModelScope.launch {
                setIsLoading(true)
                setUiState(UIState.Loading)
                try {
                    val nodes = repository.getNodesByMapId(mapId)
                    _state.value = NodesState(nodes = nodes.associateBy { it.id })
                    setUiState(UIState.Idle)
                } catch (e: GeneralSecurityException) {
                    warn("Encryption error: ${e.message}")
                } catch (e: IOException) {
                    warn("DataStore error: ${e.message}")
                } finally {
                    setIsLoading(false)
                }
            }
        }

        override fun unlockNode(nodeId: Long) {
            updateNodesSafe {
                val unlockedNode = repository.unlockNode(nodeId)
                usersRepository.getMyUserPoints() // Sincronizar puntos tras el gasto exitoso
                unlockedNode
            }
        }

        @Suppress("MagicNumber")
        override fun completeNode(nodeId: Long) {
            // Business logic: We understand that a user is activated once she completes, at least, 3 nodes
            if (nodeId == 1013L) {
                firebaseLogEvent(FirebaseEventNames.USER_ACTIVATED)
            }

            // Business logic: We understand that a user is retained once she completes act 1
            if (nodeId == 1031L) {
                firebaseLogEvent(FirebaseEventNames.USER_RETAINED)
            }

            updateNodesSafe {
                repository.completeNode(nodeId)
            }
        }

        override suspend fun autoExecuteFirstNode(mapId: Int): NodesDomain? {
            setIsLoading(true)
            // Swallow network failures on purpose: the sign-up flow needs to keep
            // moving and land the user on the map regardless. If this fails, the
            // map's own LaunchedEffect retries on next mount.
            return try {
                val nodes = repository.getNodesByMapId(mapId)
                when {
                    nodes.isEmpty() -> null
                    nodes.any { it.status == NodeStatus.OPEN || it.status == NodeStatus.COMPLETED } -> {
                        _state.value = NodesState(nodes = nodes.associateBy { it.id })
                        null
                    }
                    else -> performAutoExecuteFirstNode(nodes)
                }
            } catch (e: HttpException) {
                warn("autoExecuteFirstNode HTTP error: ${e.message}")
                null
            } catch (e: IOException) {
                warn("autoExecuteFirstNode network error: ${e.message}")
                null
            } catch (e: GeneralSecurityException) {
                warn("autoExecuteFirstNode encryption error: ${e.message}")
                null
            } finally {
                setIsLoading(false)
            }
        }

        private suspend fun performAutoExecuteFirstNode(nodes: List<NodesDomain>): NodesDomain? {
            _state.value = NodesState(nodes = nodes.associateBy { it.id })

            // Unlock only; completion is deferred to whichever event the node's
            // onExecutionEvent triggers (its conversation/combat will emit
            // COMPLETE_NODE when it resolves). The first-node arrives at the
            // map as OPEN and finalizes after the auto-emitted first beat
            // plays out.
            val firstNode = nodes.first()
            val afterUnlock = repository.unlockNode(firstNode.id)
            usersRepository.getMyUserPoints()

            val merged = _state.value.nodes.toMutableMap()
            afterUnlock.forEach { merged[it.id] = it }
            _state.value = NodesState(nodes = merged)

            return merged[firstNode.id]
        }

        // -----------------------------------------------------------------------------------------

        private fun updateNodesSafe(block: suspend () -> List<NodesDomain>) {
            viewModelScope.launch {
                setIsLoading(true)
                setUiState(UIState.Loading)
                try {
                    val updatedNodes = block()
                    val currentNodes = _state.value.nodes.toMutableMap()

                    updatedNodes.forEach { node ->
                        currentNodes[node.id] = node
                    }

                    _state.value =
                        _state.value.copy(
                            nodes = currentNodes,
                        )

                    setUiState(UIState.Success(Unit))
                } catch (e: GeneralSecurityException) {
                    warn("Encryption error: ${e.message}")
                } catch (e: IOException) {
                    warn("DataStore error: ${e.message}")
                } finally {
                    setIsLoading(false)
                }
            }
        }
    }
