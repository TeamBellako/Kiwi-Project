package com.bellako.kiwi.common.services.eventbus

suspend fun listenToEvent(
    targetEventType: EventType,
    onEventConsumed: suspend (EventPayload) -> Unit,
) {
    EventBus.eventFlow.collect { (eventType, eventPayload) ->
        if (targetEventType == eventType) {
            onEventConsumed(eventPayload)
        }
    }
}
