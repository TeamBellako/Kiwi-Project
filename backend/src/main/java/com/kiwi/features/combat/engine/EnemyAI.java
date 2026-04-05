package com.kiwi.features.combat.engine;

import com.kiwi.features.combat.data.domain.ActorDomain;
import com.kiwi.features.skills.data.domain.SkillEffectDomain;
import com.kiwi.features.skills.data.domain.SkillCombatDomain;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//TODO CAMBIAR A LA IA PROPUESTA EN EL NOTION
@Component
public class EnemyAI {

    private final Random random = new Random();

    public Long chooseSkill(CombatContext context) {

        ActorDomain enemy = context.getEnemy();
        ActorDomain user = context.getUser();

        List<SkillCombatDomain> skills =
                new ArrayList<>(enemy.getSkills().values());

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
            ActorDomain enemy,
            ActorDomain user
    ) {

        float score = 0;

        for (SkillEffectDomain effect : skill.getEffects()) {

            switch (effect.getEffectType()) {

                case DAMAGE -> {

                    float atk =
                            effect.getAttackType().isPhysical()
                                    ? enemy.getPatk()
                                    : enemy.getMatk();

                    float def =
                            effect.getAttackType().isPhysical()
                                    ? user.getPdef()
                                    : user.getMdef();

                    float damage =
                            (atk / def) * effect.getPower();

                    score += damage;
                }

                case HEAL -> {

                    if(enemy.getHp() < enemy.getMaxHp() * 0.4f) {
                        score += 50;
                    }
                }

                case APPLY_STATUS -> {

                    float resistance =
                            user.getStatusResistances()
                                    .getOrDefault(effect.getStateId(),0f);

                    score += (1f - resistance) * 30;
                }
            }
        }

        score += random.nextFloat() * 5;

        return score;
    }

    // ----------------------------------------------------------------------------------------------------------------
}