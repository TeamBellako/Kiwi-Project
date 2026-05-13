package com.kiwi.users;

import com.kiwi.features.users.data.LoginDTO;
import com.kiwi.features.users.data.UsersDTO;

import java.time.LocalDate;

import static com.kiwi.common.utils.FormatUtils.formatDate;

public class UsersTestFactory {
    public static LoginDTO validLoginDTO() {
        return new LoginDTO("finn@thehuman.com", "Math3matic!");
    }

    public static LoginDTO invalidLoginDTO() {
        return new LoginDTO("football", "kk");
    }

    private static final String DATE = formatDate(LocalDate.of(2025, 1, 1));

    public static UsersDTO validUserDTO() {
        return new UsersDTO(validLoginDTO().getEmail(), DATE);
    }

    public static UsersDTO invalidUserDTO() {
        return new UsersDTO(invalidLoginDTO().getEmail(), DATE);
    }
}
