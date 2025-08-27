package com.kiwi.features.users.data;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @EqualsAndHashCode @ToString
public class LoginDTO {
    private String email;
    private String password;
}
