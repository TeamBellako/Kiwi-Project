package com.kiwi.features.combat.engine;

import com.kiwi.features.combat.data.domain.CombatActorDomain;
import com.kiwi.features.combat.data.domain.StatusResistanceDomain;
import com.kiwi.features.skills.data.domain.SkillEffectDomain;
import com.kiwi.features.skills.data.domain.SkillCombatDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//TODO CAMBIAR A LA IA PROPUESTA EN EL NOTION
@Component
@RequiredArgsConstructor
public class EnemyAI {

    private final Random random;

    //------------------------------------------------------------------------------------------------------------------

    public Long chooseSkill(CombatContext context) {

        CombatActorDomain enemy = context.getEnemy();
        CombatActorDomain user = context.getUser();

        List<SkillCombatDomain> skills = new ArrayList<>(enemy.getSkills().values());

        Long bestSkillId = -1L;

        float bestScore = -999;

        for (SkillCombatDomain skill : skills) {

            float score =
                    evaluateSkill(skill, enemy, user);

            if(score > bestScore) {
                bestScore = score;
                bestSkillId = skill.getId();
            }
        }

        return bestSkillId;
    }

    // ----------------------------------------------------------------------------------------------------------------

    private float evaluateSkill(
            SkillCombatDomain skill,
            CombatActorDomain enemy,
            CombatActorDomain user
    ) {

        float score = 0;

        for (SkillEffectDomain effect : skill.getEffects()) {

            switch (effect.getEffectType()) {

                case DAMAGE -> {

                    float atk =
                            effect.getAttackType().isPhysical()
                                    ? enemy.getStats().getPatk()
                                    : enemy.getStats().getMatk();

                    float def =
                            effect.getAttackType().isPhysical()
                                    ? user.getStats().getPdef()
                                    : user.getStats().getMdef();

                    float damage =
                            (atk / def) * effect.getPower();

                    score += damage;
                }

                case HEAL -> {

                    if(enemy.getStats().getCurrentHp() < enemy.getStats().getMaxHp() * 0.4f) {
                        score += 50;
                    }
                }

                case APPLY_STATUS -> {

                    StatusResistanceDomain statusSResistance =
                            user.getStatusResistances()
                                    .getOrDefault(effect.getStateId(), null);

                    float resistanceValue = (statusSResistance != null)
                            ? statusSResistance.getResistance()
                            : 0f;

                    score += (1f - resistanceValue) * 30;
                }
            }
        }

        score += random.nextFloat() * 5;

        return score;
    }

    // ----------------------------------------------------------------------------------------------------------------
}