package com.kiwi.users;

public class UsersMapper {
    // Persistence → Domain
    public static Users toDomain(UsersPersistence entity) {
        return new Users(
                entity.getEmail(),
                null,
                entity.getSettings()
        );
    }

    // Domain → Persistence
    public static UsersPersistence toPersistence(Users domain, String hashedPassword) {
        return new UsersPersistence(
                domain.getEmail(),
                hashedPassword,
                domain.getSettings()
        );
    }

    // DTO → Domain
    public static Users toDomain(UsersDTO dto) {
        return new Users(
                new Email(dto.getEmail()),
                dto.getPassword() != null ? new Password(dto.getPassword()) : null,
                dto.getSettingsDTO().toDomainObject()
        );
    }

    // Domain → DTO
    public static UsersDTO toDTO(Users domain) {
        return new UsersDTO(
                domain.getEmail().value(),
                null,
                domain.getSettings().toDTO()
        );
    }

    // Persistence → DTO
    public static UsersDTO toDTO(UsersPersistence entity) {
        return new UsersDTO(
                entity.getEmail().value(),
                null,
                entity.getSettings().toDTO()
        );
    }
}

