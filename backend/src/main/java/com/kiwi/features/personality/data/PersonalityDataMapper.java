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
                        dto.getBadApps(),
                        dto.getNeutralApps()
                );
    }

    public static PersonalityDomain toDomain(PersonalityPersistence persistence) {
        return (persistence == null) ? null :
                new PersonalityDomain(
                        persistence.getRealName(),
                        persistence.getKnightName(),
                        persistence.getBuild(),
                        persistence.getGoodApps(),
                        persistence.getBadApps(),
                        persistence.getNeutralApps()
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
                        .neutralApps(domain.getNeutralApps())
                        .build();
    }

    public static PersonalityPersistence toPersistence(UsersPersistence usersPersistence, PersonalityDTO dto) {
        return toPersistence(usersPersistence, toDomain(dto));
    }

    public static void updatePersistence(PersonalityPersistence persistence, PersonalityDomain domain) {
        persistence.setRealName(domain.getRealName());
        persistence.setKnightName(domain.getKnightName());
        persistence.setBuild(domain.getBuild());
        persistence.setGoodApps(domain.getGoodApps());
        persistence.setBadApps(domain.getBadApps());
        persistence.setNeutralApps(domain.getNeutralApps());
    }

    public static PersonalityDTO toDTO(PersonalityDomain domain) {
        if (domain == null) return null;
        return new PersonalityDTO(
                domain.getRealName(),
                domain.getKnightName(),
                domain.getBuild(),
                domain.getGoodApps(),
                domain.getBadApps(),
                domain.getNeutralApps()
        );
    }

    public static PersonalityDTO toDTO(PersonalityPersistence persistence) {
        return toDTO(toDomain(persistence));
    }
}
