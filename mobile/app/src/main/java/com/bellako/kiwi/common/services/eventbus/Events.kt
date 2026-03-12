package com.bellako.kiwi.common.services.eventbus

import com.bellako.kiwi.features.dashboard.screens.DashboardLayout

enum class EventType {
    SWITCH_MAP,
    CHANGE_DASHBOARD_LAYOUT,
    START_CNV,
    START_COMBAT,
    START_QUEST,
    COMPLETE_QUEST,
    COMPLETE_GOAL,
    GAIN_SKILL,
    THROW_SKILL,
    UNLOCK_NODE,
    START_TIP,
}

sealed class EventPayload {
    data class ChangeDashboardLayoutPayload(
        val newLayout: DashboardLayout,
    ) : EventPayload()

    data class EntityIdPayload(
        val targetEntityId: Int,
    ) : EventPayload()
}
