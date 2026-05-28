package com.bellako.kiwi.common.services.eventbus

import com.bellako.kiwi.features.dashboard.screens.DashboardLayout

enum class EventType {
    SWITCH_MAP,
    CHANGE_DASHBOARD_LAYOUT,
    START_CNV,
    START_COMBAT,
    START_QUEST,
    COMPLETE_NODE,
    COMPLETE_QUEST,
    COMPLETE_GOAL,
    GAIN_SKILL,
    THROW_SKILL,
    UNLOCK_NODE,
    START_TIP,
    DAILY_GOALS_UPDATED,
    QUESTS_UPDATED,
    MAP_REVEAL,
    MAP_CONTENT_AVAILABLE,

    // Fired by features that fully cover the map (full-screen conversation,
    // combat) so the map can pause its VFX (mist drift, cloud frame loop,
    // water shader). Each START fires a MAP_COVERED; the matching dismissal
    // fires MAP_UNCOVERED. Small dialogues don't fire these — the map stays
    // visible behind them.
    MAP_COVERED,
    MAP_UNCOVERED,
}

sealed class EventPayload {
    data class EmptyPayload(
        val value: Int? = null,
    ) : EventPayload()

    data class ChangeDashboardLayoutPayload(
        val newLayout: DashboardLayout,
    ) : EventPayload()

    data class EntityIdPayload(
        val targetEntityId: Int,
    ) : EventPayload()
}
