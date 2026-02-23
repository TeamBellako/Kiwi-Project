package com.bellako.kiwi.features.conversations.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bellako.kiwi.features.conversations.data.ConversationDataMapper
import com.bellako.kiwi.features.conversations.data.ConversationDomain
import com.bellako.kiwi.features.conversations.data.ConversationOptionDomain
import com.bellako.kiwi.features.conversations.data.NextEventType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConversationViewModel
    @Inject
    constructor(
        private val repository: ConversationsRepository,
    ) : ViewModel() {
        // -------------------------------------------------------------------------

        private val _active = MutableStateFlow<ConversationDomain?>(null)
        val active: StateFlow<ConversationDomain?> = _active.asStateFlow()

        fun start(conversationId: Long) {
            viewModelScope.launch {
                repository.getById(conversationId).fold(
                    onSuccess = { dto ->
                        ConversationDataMapper.toDomain(dto).onSuccess { domain ->
                            _active.value = domain
                        }
                    },
                    onFailure = { /* TODO: manejar error */ },
                )
            }
        }

        /**
         * SOLO PARA DESARROLLO
         */
        fun start(conversation: ConversationDomain) {
            viewModelScope.launch {
                _active.value = conversation
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
         */
        fun next(option: ConversationOptionDomain) {
            val conversation = _active.value ?: return
            handleNextEvent(conversation, option.nextEventId)
        }

        /** Termina la conversación activa */
        fun end() {
            _active.value = null
        }

        // -------------------------------------------------------------------------

        private fun handleNextEvent(
            conversation: ConversationDomain,
            nextEventId: Long,
        ) {
            when (conversation.nextEvent) {
                NextEventType.CONVERSATION -> start(nextEventId)
                NextEventType.END -> end()
                NextEventType.BATTLE -> end() // TODO: lanzar batalla
            }
        }
    }
