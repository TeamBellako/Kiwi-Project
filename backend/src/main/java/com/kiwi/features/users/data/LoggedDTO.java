package com.kiwi.features.users.data;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @EqualsAndHashCode @ToString
public class LoggedDTO {
    private String jwt;
    private String registerDate;
}
