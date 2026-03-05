package com.bellako.kiwi.common.services.eventbus

import com.bellako.kiwi.features.dashboard.screens.DashboardLayout
import com.bellako.kiwi.features.map.data.MapInfo

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
}

sealed class EventPayload {
    data class SwitchMapPayload(
        val mapInfo: MapInfo,
    ) : EventPayload()

    data class ChangeDashboardLayoutPayload(
        val newLayout: DashboardLayout,
    ) : EventPayload()

    data class EntityIdPayload(
        val targetEntityId: Int,
    ) : EventPayload()
}
