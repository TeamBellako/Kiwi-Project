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
public class UserElementalMultiplierKey implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "element_id")
    private Long elementId;
}