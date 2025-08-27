package com.kiwi.features.users.data;

import com.kiwi.common.types.Email;
import com.kiwi.common.types.Password;

public class UsersDataMapper {

    public static UsersDomain toDomain(UsersDTO dto) {
        return (dto == null) ? null :
                new UsersDomain(
                        new Email(dto.getEmail()),
                        new Password(dto.getPassword())
                );
    }

    public static UsersDTO toDTO(UsersDomain domain) {
        return (domain == null) ? null :
                new UsersDTO(
                        domain.getEmail().value(),
                        domain.getPassword().value()
                );
    }

    // Cannot convert from persistence to anything because the password is hashed

    public static UsersPersistence toPersistence(UsersDomain domain, String hashedPassword) {
        return (domain == null) ? null :
                UsersPersistence.builder()
                        .email(domain.getEmail().value())
                        .hashedPassword(hashedPassword)
                        .build();
    }

    public static UsersPersistence toPersistence(UsersDTO dto, String hashedPassword) {
        return toPersistence(toDomain(dto), hashedPassword);
    }
}
