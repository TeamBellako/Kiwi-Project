package com.kiwi.features.combat.data;

import com.kiwi.features.combat.data.state.CombatStateAppliedDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CombatActorStateDTO {

    private String actor; // USER / ENEMY

    private int hp;

    private List<CombatStateAppliedDTO> activeStates;
}