package com.bellako.kiwi.common.services.eventbus

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

object EventBus {
    private val _eventFlow = MutableSharedFlow<Pair<EventType, EventPayload>>(replay = 0)
    val eventFlow: SharedFlow<Pair<EventType, EventPayload>> = _eventFlow

    suspend fun emitEvent(
        eventType: EventType,
        payload: EventPayload,
    ) {
        _eventFlow.emit(Pair(eventType, payload))
    }
}
