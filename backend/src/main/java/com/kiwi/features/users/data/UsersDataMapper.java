package com.kiwi.features.users.data;

import com.kiwi.features.metrics.data.MetricsDataMapper;
import com.kiwi.features.users.exceptions.UsersInvalidException;
import com.kiwi.common.types.Email;
import com.kiwi.common.types.Password;

public class UsersDataMapper {
    // Persistence → Domain
    public static UsersDomain toDomain(UsersPersistence entity) {
        return new UsersDomain(
                entity.getEmail(),
                null,
                entity.getSettings(),
                MetricsDataMapper.toDomainSetFromPersistenceSet(entity.getMetrics())
        );
    }

    // Domain → Persistence
    public static UsersPersistence toPersistence(UsersDomain domain, String hashedPassword) {
        return new UsersPersistence(
                domain.getEmail(),
                hashedPassword,
                domain.getSettings()
        );
    }

    // DTO → Domain
    public static UsersDomain toDomain(UsersDTO dto) {
        UsersDomain user;
        try {
            user = new UsersDomain(
                    new Email(dto.getEmail()),
                    dto.getPassword() != null ? new Password(dto.getPassword()) : null,
                    dto.getSettingsDTO().toDomainObject(),
                    MetricsDataMapper.toDomainSetFromDtoSet(dto.getMetricsDTOs())
            );
        } catch (Exception e) {
            throw new UsersInvalidException(e.getMessage());
        }
        
        return user; 
    }

    // Domain → DTO
    public static UsersDTO toDTO(UsersDomain domain) {
        return new UsersDTO(
                domain.getEmail().value(),
                null,
                domain.getSettings().toDTO(),
                MetricsDataMapper.toDTOSet(domain.getMetrics())
        );
    }
}

