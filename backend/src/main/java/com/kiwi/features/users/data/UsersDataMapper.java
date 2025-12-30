package com.kiwi.features.users.data;

import com.kiwi.common.types.Email;
import com.kiwi.common.types.PositiveOrZeroInteger;

import java.time.LocalDate;

import static com.kiwi.common.utils.FormatUtils.formatDate;

public class UsersDataMapper {

    public static UsersDomain toDomainWithoutPoints(UsersDTO dto) {
        return (dto == null) ? null :
                new UsersDomain(
                        new Email(dto.getEmail()),
                        LocalDate.parse(dto.getRegisterDate())
                );
    }

    public static UsersDTO toUsersDTO(UsersDomain domain) {
        return (domain == null) ? null :
                new UsersDTO(
                        domain.getEmail().value(),
                        formatDate(domain.getRegisterDate())
                );
    }

    public static UsersDomain toDomain(UsersPersistence persistence) {
        if (persistence == null) return null;
        
        UsersDomain domain = new UsersDomain(
                new Email(persistence.getEmail()),
                persistence.getRegisterDate()
        );
        
        domain.setCurrentPoints(new PositiveOrZeroInteger(persistence.getCurrentPoints()));
        domain.setTotalPoints(new PositiveOrZeroInteger(persistence.getTotalPoints()));
        
        return domain;
    }

    public static UsersPersistence toPersistence(UsersDomain domain, String hashedPassword) {
        return (domain == null) ? null :
                UsersPersistence.builder()
                        .email(domain.getEmail().value())
                        .hashedPassword(hashedPassword)
                        .registerDate(domain.getRegisterDate())
                        .currentPoints(domain.getCurrentPoints().value())
                        .totalPoints(domain.getTotalPoints().value())
                        .build();
    }

    public static UsersPersistence toPersistenceWithoutPoints(UsersDTO dto, String hashedPassword) {
        return toPersistence(toDomainWithoutPoints(dto), hashedPassword);
    }

    public static UserPointsDTO toPointsDTO(UsersPersistence persistence) {
        return (persistence == null) ? null :
                UserPointsDTO.builder()
                        .currentPoints(persistence.getCurrentPoints())
                        .totalPoints(persistence.getTotalPoints())
                        .build();
    }

    public static UserPointsDTO toPointsDTO(UsersDomain domain) {
        return (domain == null) ? null :
                UserPointsDTO.builder()
                        .currentPoints(domain.getCurrentPoints().value())
                        .totalPoints(domain.getTotalPoints().value())
                        .build();
    }
}
