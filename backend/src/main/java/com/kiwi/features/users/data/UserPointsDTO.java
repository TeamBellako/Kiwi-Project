package com.kiwi.features.users.data;

import lombok.*;

/**
 * DTO de solo lectura para consultar los puntos del usuario desde la API.
 * No permite modificar puntos directamente.
 */
@Getter @NoArgsConstructor @AllArgsConstructor @Builder @EqualsAndHashCode @ToString
public class UserPointsDTO {
    private int currentPoints;
    private int totalPoints;
}
