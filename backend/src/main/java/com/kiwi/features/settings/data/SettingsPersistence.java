package com.kiwi.features.settings.data;

import com.kiwi.features.users.data.UsersPersistence;
import jakarta.persistence.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @EqualsAndHashCode @ToString
@Entity
@Table(name = "settings")
public class SettingsPersistence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "sound_volume")
    private float soundVolume;

    @Column(name = "music_volume")
    private float musicVolume;

    @OneToOne()
    @JoinColumn(name = "user_id")
    private UsersPersistence user;
}
