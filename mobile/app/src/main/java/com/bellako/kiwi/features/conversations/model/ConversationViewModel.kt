package com.bellako.kiwi.features.conversations.model

import androidx.lifecycle.viewModelScope
import com.bellako.kiwi.common.model.BaseViewModel
import com.bellako.kiwi.common.utils.Logger.warn
import com.bellako.kiwi.features.conversations.data.ConversationDomain
import com.bellako.kiwi.features.conversations.data.ConversationOptionDomain
import com.bellako.kiwi.features.conversations.data.NextEventType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.security.GeneralSecurityException
import javax.inject.Inject

@HiltViewModel
class ConversationViewModel
    @Inject
    constructor(
        private val repository: ConversationsRepository,
    ) : BaseViewModel() {
        // -------------------------------------------------------------------------

        private val _active = MutableStateFlow<ConversationDomain?>(null)
        val active: StateFlow<ConversationDomain?> = _active.asStateFlow()

        private val _isVisible = MutableStateFlow(false)
        val isVisible: StateFlow<Boolean> = _isVisible.asStateFlow()

        /** Lista de option ids seleccionados durante la sesión actual */
        private val _selectedOptions = MutableStateFlow<List<Long>>(emptyList())
        val selectedOptions: StateFlow<List<Long>> = _selectedOptions.asStateFlow()

        fun start(conversationId: Long) {
            viewModelScope.launch {
                try {
                    val conversation = repository.getById(conversationId)
                    _active.value = conversation
                    _isVisible.value = true
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
            val conversation = _active.value ?: return
            handleNextEvent(conversation, conversation.eventId)
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
                _isVisible.value = false
                delay(ANIMATION_DURATION_MS)
                _active.value = null
                _selectedOptions.value = emptyList()
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

                NextEventType.BATTLE -> {
                    end()
                }
            }
        }
    }
