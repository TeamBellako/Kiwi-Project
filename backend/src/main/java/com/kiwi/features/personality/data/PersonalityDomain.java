package com.kiwi.features.personality.data;

import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @EqualsAndHashCode @ToString
public class PersonalityDomain {
    private String realName;
    private String knightName;
    private String build;
    private List<String> goodApps;
    private List<String> badApps;
}
