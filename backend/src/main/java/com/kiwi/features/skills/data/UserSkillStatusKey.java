package com.kiwi.features.skills.data;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@Embeddable
public class UserSkillStatusKey implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "skill_id")
    private Long skillId;
}
