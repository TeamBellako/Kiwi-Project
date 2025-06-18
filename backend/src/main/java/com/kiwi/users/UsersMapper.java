package com.kiwi.users;

import com.kiwi.metrics.MetricsMapper;

public class UsersMapper {
    // Persistence → Domain
    public static Users toDomain(UsersPersistence entity) {
        return new Users(
                entity.getEmail(),
                null,
                entity.getSettings(),
                MetricsMapper.toDomainSetFromPersistenceSet(entity.getMetrics())
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
                dto.getSettingsDTO().toDomainObject(),
                MetricsMapper.toDomainSetFromDtoSet(dto.getMetricsDTOs())
        );
    }

    // Domain → DTO
    public static UsersDTO toDTO(Users domain) {
        return new UsersDTO(
                domain.getEmail().value(),
                null,
                domain.getSettings().toDTO(),
                MetricsMapper.toDTOSet(domain.getMetrics())
        );
    }
}

