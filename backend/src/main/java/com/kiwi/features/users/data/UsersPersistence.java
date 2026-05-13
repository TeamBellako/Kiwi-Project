package com.kiwi.features.users.data;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @EqualsAndHashCode @ToString
@Entity
@Table(name = "users")
public class UsersPersistence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String hashedPassword;

    @Column(name = "register_date", nullable = false)
    private LocalDate registerDate;

    @Builder.Default
    @Column(name = "current_points", nullable = false)
    private Integer currentPoints = 0;

    @Builder.Default
    @Column(name = "total_points", nullable = false)
    private Integer totalPoints = 0;
}