package com.bellako.kiwi.features.skills.data

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant

object SkillDataMapper {
    @RequiresApi(Build.VERSION_CODES.O)
    fun toDomain(dto: SkillDTO): SkillDomain =
        when (CooldownType.valueOf(dto.cooldownType)) {
            CooldownType.OTHER ->
                SkillDomain.Other(
                    id = dto.skillId,
                    name = dto.name,
                    description = dto.description,
                    quote = dto.quote,
                    icon = dto.icon,
                    levelupSkillId = dto.levelupSkillId,
                    isCooldown = dto.cooldown,
                    deckSlot = dto.deckSlot,
                    cooldownOtherDescription =
                        requireNotNull(dto.cooldownOtherDescription) {
                            "cooldownOtherDescription is required for OTHER cooldown"
                        },
                )

            CooldownType.TIME ->
                SkillDomain.Time(
                    id = dto.skillId,
                    name = dto.name,
                    description = dto.description,
                    quote = dto.quote,
                    icon = dto.icon,
                    levelupSkillId = dto.levelupSkillId,
                    isCooldown = dto.cooldown,
                    deckSlot = dto.deckSlot,
                    cooldownTimeMinutes =
                        requireNotNull(dto.cooldownTimeMinutes) {
                            "cooldownTimeMinutes is required for TIME cooldown"
                        },
                    cooldownUntil = dto.cooldownUntil?.let { Instant.ofEpochMilli(it) },
                )

            CooldownType.GOAL ->
                SkillDomain.Goal(
                    id = dto.skillId,
                    name = dto.name,
                    description = dto.description,
                    quote = dto.quote,
                    icon = dto.icon,
                    levelupSkillId = dto.levelupSkillId,
                    isCooldown = dto.cooldown,
                    deckSlot = dto.deckSlot,
                    cooldownGoalId =
                        requireNotNull(dto.cooldownGoalId) {
                            "cooldownGoalId is required for GOAL cooldown"
                        },
                    goalData = null,
                )
        }
}
