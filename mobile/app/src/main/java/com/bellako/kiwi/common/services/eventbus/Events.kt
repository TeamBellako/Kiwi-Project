package com.bellako.kiwi.common.services.eventbus

enum class EventType {
    SWITCH_MAP,
}

sealed class EventPayload {
    data class SwitchMapPayload(
        val mapResourceId: Int,
    ) : EventPayload()
}
