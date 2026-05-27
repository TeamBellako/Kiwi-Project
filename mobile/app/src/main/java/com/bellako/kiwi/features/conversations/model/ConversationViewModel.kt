package com.bellako.kiwi.features.conversations.model

import androidx.lifecycle.viewModelScope
import com.bellako.kiwi.common.model.BaseViewModel
import com.bellako.kiwi.common.services.PersonalityScriptVariableResolver
import com.bellako.kiwi.common.services.eventbus.EventBus
import com.bellako.kiwi.common.services.eventbus.EventPayload
import com.bellako.kiwi.common.services.eventbus.EventType
import com.bellako.kiwi.common.services.eventbus.listenToEvent
import com.bellako.kiwi.common.utils.Logger.warn
import com.bellako.kiwi.features.conversations.data.ConversationDomain
import com.bellako.kiwi.features.conversations.data.ConversationOptionDomain
import com.bellako.kiwi.features.conversations.data.ConversationType
import com.bellako.kiwi.features.conversations.data.NextEventType
import com.bellako.kiwi.features.incidences.model.UserIncidenceManager
import com.bellako.kiwi.features.personality.model.IPersonalityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.security.GeneralSecurityException
import javax.inject.Inject

@OptIn(DelicateCoroutinesApi::class)
@HiltViewModel
class ConversationViewModel
    @Inject
    constructor(
        private val repository: ConversationsRepository,
        private val personalityRepository: IPersonalityRepository,
        private val userIncidenceManager: UserIncidenceManager,
    ) : BaseViewModel() {
        // -------------------------------------------------------------------------

        private val _active = MutableStateFlow<ConversationDomain?>(null)
        val active: StateFlow<ConversationDomain?> = _active.asStateFlow()

        private val _isVisible = MutableStateFlow(false)
        val isVisible: StateFlow<Boolean> = _isVisible.asStateFlow()

        /** Lista de option ids seleccionados durante la sesión actual */
        private val _selectedOptions = MutableStateFlow<List<Long>>(emptyList())
        val selectedOptions: StateFlow<List<Long>> = _selectedOptions.asStateFlow()

        private val scriptVariableResolver: PersonalityScriptVariableResolver by lazy {
            PersonalityScriptVariableResolver(personalityRepository)
        }

        // Optional exit-veil hook. When set, end() defers the dismissal until
        // the veil has fully covered the screen, so the user sees the same
        // veil transition as a node entry instead of the conversation lerping
        // off the bottom. Null in tests / previews — those keep the original
        // slide-out timing.
        private var exitVeilRunner: (suspend (finalize: suspend () -> Unit) -> Unit)? = null

        fun setExitVeilRunner(runner: (suspend (finalize: suspend () -> Unit) -> Unit)?) {
            exitVeilRunner = runner
        }

        init {
            GlobalScope.launch(Dispatchers.Main) {
                listenToEvent(EventType.START_CNV) { eventPayload ->
                    val payload = eventPayload as EventPayload.EntityIdPayload
                    start(payload.targetEntityId.toLong())
                }
            }
        }

        fun start(conversationId: Long) {
            viewModelScope.launch {
                try {
                    val conversation = repository.getById(conversationId)

                    _active.value =
                        conversation.copy(
                            dialog = conversation.readDialog(scriptVariableResolver),
                            // Filter options based on incidences
                            options =
                                conversation.options.filter { optionDomain ->
                                    optionDomain.incidenceToShow == null ||
                                        optionDomain.incidenceToShow.isEmpty() ||
                                        userIncidenceManager.getIncidence(optionDomain.incidenceToShow)
                                },
                        )

                    _isVisible.value = true

                    // Tell the map to pause its VFX while a full-screen
                    // conversation covers it. Small dialogues sit over the
                    // map without hiding it, so they don't emit this.
                    if (conversation.type == ConversationType.FULL) {
                        EventBus.emitEvent(EventType.MAP_COVERED, EventPayload.EmptyPayload())
                    }

                    if (conversation.incidenceNameToSet != null && !conversation.incidenceNameToSet.isEmpty()) {
                        userIncidenceManager.setIncidence(
                            conversation.incidenceNameToSet,
                            conversation.incidenceValueToSet,
                        )
                    }
                } catch (e: GeneralSecurityException) {
                    warn("Encryption error: ${e.message}")
                } catch (e: IOException) {
                    warn("DataStore error: ${e.message}")
                }
            }
        }

        /**
         * Avanza al siguiente evento tras pulsar la flecha (sin opciones).
         * Usa el nextEvent y eventId de la conversación activa.
         */
        fun next() {
            val conversation: ConversationDomain = _active.value ?: return

            viewModelScope.launch {
                handleNextEvent(
                    conversation.copy(
                        dialog =
                            conversation.readDialog(
                                scriptVariableResolver,
                            ),
                    ),
                    if (shouldPlayNextEvent(conversation)) conversation.eventId else conversation.fallbackEventId,
                )
            }
        }

        /**
         * Avanza al siguiente evento tras elegir una opción.
         * Registra la opción seleccionada en el diccionario de la sesión.
         */
        fun next(option: ConversationOptionDomain) {
            val conversation = _active.value ?: return
            option.id?.let { _selectedOptions.value = _selectedOptions.value + it }
            handleNextEvent(conversation, option.nextEventId)
        }

        /** Termina la conversación activa y limpia las selecciones acumuladas */
        fun end() {
            viewModelScope.launch {
                val options = _selectedOptions.value
                if (options.isNotEmpty()) {
                    try {
                        repository.saveOptions(options)
                    } catch (e: GeneralSecurityException) {
                        warn("Encryption error saving options: ${e.message}")
                    } catch (e: IOException) {
                        warn("IO error saving options: ${e.message}")
                    }
                }

                val clearActiveAndEmit: suspend () -> Unit = {
                    // Uncover the map BEFORE firing the follow-up event so any
                    // chained covering screen (another FULL conv / combat) can
                    // emit its own MAP_COVERED last and win the final state.
                    if (_active.value?.type == ConversationType.FULL) {
                        EventBus.emitEvent(EventType.MAP_UNCOVERED, EventPayload.EmptyPayload())
                    }
                    if (_active.value != null && _active.value?.onCompletedEvent != "_") {
                        EventBus.emitEvent(
                            EventType.valueOf(_active.value!!.onCompletedEvent),
                            EventPayload.EntityIdPayload(_active.value!!.onCompletedEntityId),
                        )
                    }
                    _active.value = null
                    _selectedOptions.value = emptyList()
                }

                val runner = exitVeilRunner
                if (runner != null) {
                    // The runner plays the veil enter, calls the finalize block
                    // while the veil is fully opaque (so the swap is invisible),
                    // then plays the veil fade-out to reveal the map (or a
                    // chained follow-up screen).
                    runner {
                        _isVisible.value = false
                        clearActiveAndEmit()
                    }
                } else {
                    // Fallback for previews / tests: keep the original slide-out
                    // timing — hide content, wait for the slide, then clean up.
                    _isVisible.value = false
                    delay(ANIMATION_DURATION_MS)
                    clearActiveAndEmit()
                }
            }
        }

        companion object {
            private const val ANIMATION_DURATION_MS = 500L
        }

        // -------------------------------------------------------------------------

        private fun handleNextEvent(
            conversation: ConversationDomain,
            nextEventId: Long?,
        ) {
            when (conversation.nextEvent) {
                NextEventType.CONVERSATION -> {
                    if (nextEventId == null) {
                        end()
                    } else {
                        start(nextEventId)
                    }
                }

                NextEventType.END -> {
                    end()
                }
            }
        }

        private suspend fun shouldPlayNextEvent(conversationDomain: ConversationDomain): Boolean {
            val isConditionalVariableEmpty =
                conversationDomain.incidenceForNextEvent == null || conversationDomain.incidenceForNextEvent.isEmpty()
            return isConditionalVariableEmpty ||
                userIncidenceManager.getIncidence(conversationDomain.incidenceForNextEvent)
        }
    }
