package com.kiwi.features.users.data;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @EqualsAndHashCode @ToString
public class UsersDTO {
    private String email;
    private String password;
    private String registerDate;
}
