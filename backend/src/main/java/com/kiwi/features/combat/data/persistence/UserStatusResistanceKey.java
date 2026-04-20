package com.kiwi.features.combat.data.persistence;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class UserStatusResistanceKey implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "state_id")
    private Long stateId;
}