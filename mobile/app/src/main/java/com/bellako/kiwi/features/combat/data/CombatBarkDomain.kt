package com.bellako.kiwi.features.combat.data

import com.bellako.kiwi.features.conversations.data.ConversationDomain
import kotlinx.serialization.Serializable

@Serializable
enum class BarkTriggerType {
    ENEMY_HP_PERCENT,
    PLAYER_HP_PERCENT,
    SKILL_USED,
    COMBAT_ELAPSED_SECONDS,
}

@Serializable
enum class BarkDismissMode {
    AUTO,
    CLICK,
}

@Serializable
data class CombatBarkTriggerDomain(
    val id: Long,
    val type: BarkTriggerType,
    val threshold: Float? = null,
    val skillId: Long? = null,
    val conversationId: Long,
    val dismissMode: BarkDismissMode = BarkDismissMode.AUTO,
    val priority: Int = 0,
)

data class ActiveBarkDomain(
    val triggerId: Long,
    val conversation: ConversationDomain,
    val dismissMode: BarkDismissMode,
)
