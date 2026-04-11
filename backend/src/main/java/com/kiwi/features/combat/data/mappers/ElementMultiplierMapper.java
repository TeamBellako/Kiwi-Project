package com.kiwi.features.combat.data.mappers;

import com.kiwi.features.combat.data.domain.ElementMultiplierDomain;
import com.kiwi.features.combat.data.dto.ElementMultiplierDTO;
import com.kiwi.features.combat.data.persistence.CombatElementPersistence;
import com.kiwi.features.combat.data.persistence.EnemyElementMultiplierPersistence;
import com.kiwi.features.combat.data.persistence.UserElementMultiplierPersistence;

import java.util.List;

public class ElementMultiplierMapper {

    public static ElementMultiplierDTO toDTO(ElementMultiplierDomain element) {
        return ElementMultiplierDTO.builder()
                .elementId(element.getElementId())
                .name(element.getName())
                .icon(element.getIcon())
                .multiplier(element.getMultiplier())
                .description(element.getDescription())
                .build();
    }

    public static List<ElementMultiplierDTO> toDTOList(List<ElementMultiplierDomain> list) {
        return list.stream()
                .map(ElementMultiplierMapper::toDTO)
                .toList();
    }

    public static ElementMultiplierDomain toDomain(EnemyElementMultiplierPersistence multiplier, CombatElementPersistence element) {
        return ElementMultiplierDomain.builder()
                .elementId(element.getId())
                .name(element.getName())
                .icon(element.getIcon())
                .multiplier(multiplier.getMultiplier())
                .description(element.getDescription())
                .build();
    }

    public static ElementMultiplierDomain toDomain(UserElementMultiplierPersistence multiplier, CombatElementPersistence element) {
        return ElementMultiplierDomain.builder()
                .elementId(element.getId())
                .name(element.getName())
                .icon(element.getIcon())
                .multiplier(multiplier.getMultiplier())
                .description(element.getDescription())
                .build();
    }
}
