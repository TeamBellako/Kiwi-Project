package com.kiwi.features.users.data;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @EqualsAndHashCode @ToString
@Entity
@Table(name = "user_app_usage", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "app_type"}))
public class UserAppUsagePersistence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UsersPersistence user;

    @Enumerated(EnumType.STRING)
    @Column(name = "app_type", nullable = false)
    private AppUsageType appType;

    @Column(name = "avg_daily_usage_ms", nullable = false)
    private Long avgDailyUsageMs;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;
}
