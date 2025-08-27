package com.kiwi.features.users.data;

import com.kiwi.common.types.Email;
import com.kiwi.common.types.Password;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @EqualsAndHashCode @ToString
public class UsersDomain {
    private Email email;
    private Password password;
}