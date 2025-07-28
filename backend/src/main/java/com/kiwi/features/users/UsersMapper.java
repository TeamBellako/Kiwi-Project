package com.kiwi.features.users;

import com.kiwi.features.metrics.MetricsMapper;
import com.kiwi.types.Email;
import com.kiwi.types.Password;

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
        Users user;
        try {
            user = new Users(
                    new Email(dto.getEmail()),
                    dto.getPassword() != null ? new Password(dto.getPassword()) : null,
                    dto.getSettingsDTO().toDomainObject(),
                    MetricsMapper.toDomainSetFromDtoSet(dto.getMetricsDTOs())
            );
        } catch (Exception e) {
            throw new UsersInvalidException(e.getMessage());
        }
        
        return user; 
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

