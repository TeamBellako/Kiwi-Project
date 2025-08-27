package com.kiwi.features.personality.data;

import com.kiwi.features.users.data.UsersPersistence;

public class PersonalityDataMapper {

    public static PersonalityDomain toDomain(PersonalityDTO dto) {
        return (dto == null) ? null :
                new PersonalityDomain(
                        dto.getRealName(),
                        dto.getKnightName(),
                        dto.getBuild(),
                        dto.getGoodApps(),
                        dto.getBadApps()
                );
    }

    public static PersonalityDomain toDomain(PersonalityPersistence persistence) {
        return (persistence == null) ? null :
                new PersonalityDomain(
                        persistence.getRealName(),
                        persistence.getKnightName(),
                        persistence.getBuild(),
                        persistence.getGoodApps(),
                        persistence.getBadApps()
                );
    }

    public static PersonalityPersistence toPersistence(UsersPersistence usersPersistence, PersonalityDomain domain) {
        return (domain == null) ? null :
                PersonalityPersistence.builder()
                        .user(usersPersistence)
                        .realName(domain.getRealName())
                        .knightName(domain.getKnightName())
                        .build(domain.getBuild())
                        .goodApps(domain.getGoodApps())
                        .badApps(domain.getBadApps())
                        .build();
    }

    public static PersonalityPersistence toPersistence(UsersPersistence usersPersistence, PersonalityDTO dto) {
        return toPersistence(usersPersistence, toDomain(dto));
    }

    public static PersonalityDTO toDTO(PersonalityDomain domain) {
        if (domain == null) return null;
        return new PersonalityDTO(
                domain.getRealName(),
                domain.getKnightName(),
                domain.getBuild(),
                domain.getGoodApps(),
                domain.getBadApps()
        );
    }

    public static PersonalityDTO toDTO(PersonalityPersistence persistence) {
        return toDTO(toDomain(persistence));
    }
}
