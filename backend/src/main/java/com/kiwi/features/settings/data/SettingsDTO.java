package com.kiwi.features.settings.data;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @EqualsAndHashCode @ToString
public class SettingsDTO {
    private float soundVolume;
    private float musicVolume;
}
