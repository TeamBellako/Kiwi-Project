package com.kiwi.features.combat.data.domain;

import com.kiwi.features.combat.data.enums.CombatActionType;
import com.kiwi.features.combat.data.enums.CombatActorType;
import com.kiwi.features.combat.data.enums.CombatStateTypes;
import com.kiwi.features.combat.data.enums.StatType;
import com.kiwi.features.skills.data.domain.SkillCombatDomain;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Builder
public class CombatActorDomain {

    private CombatActorType type;

    private StatsDomain stats;

    private Map<Long, ElementMultiplierDomain> elementMultipliers;
    private Map<Long, StatusResistanceDomain> statusResistances;

    private List<CombatActiveStatusDomain> activeStatuses;

    @Setter
    private  Map<Long, SkillCombatDomain> skills;

    @Setter
    private  List<Long> blockedSkills;

    @Setter
    private CombatActionType actionModifierType;

    @Setter
    private Long lastSkillUsed;

    public int damage(int amount, boolean useShield) {

        int realDamage = amount;

        if(useShield) {

            // SHIELD
            int shield = stats.getStat(StatType.SHIELD);

            if (shield > 0) {

                int absorbed = Math.min(shield, realDamage);
                stats.setStat(StatType.SHIELD, shield - absorbed);
                realDamage -= absorbed;
            }
        }

        //CHECK SURVIVE
        if (realDamage > 0) {

            boolean hasSurvive = getActiveStatuses().stream()
                    .anyMatch(s -> s.getStateId() == CombatStateTypes.SURVIVE.getId());

            if (hasSurvive && stats.getStat(StatType.CURRENT_HP) - realDamage <= 0) {
                stats.setStat(StatType.CURRENT_HP, 1);
            } else {
                stats.setStat(StatType.CURRENT_HP, Math.max(0, stats.getStat(StatType.CURRENT_HP) - realDamage));
            }
        }

        return realDamage;
    }

    public void heal(int amount) {

        stats.setStat(StatType.CURRENT_HP, Math.min(stats.getStat(StatType.MAX_HP), stats.getStat(StatType.CURRENT_HP) + amount));
    }

}