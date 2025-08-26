package com.kiwi.features.metrics.data;

import com.kiwi.features.users.data.UsersPersistence;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @EqualsAndHashCode @ToString
@Entity
@Table(name = "metrics", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "date"}))
public class MetricsPersistence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UsersPersistence user;

    @Column(name = "date", nullable = false, unique = true)
    private LocalDate date;

    @Column(name = "max_good_time_seconds", nullable = false)
    private Integer maxGoodTimeSeconds;
    
    @Column(name = "current_good_time_seconds", nullable = false)
    private Integer currentGoodTimeSeconds;

    @Column(name = "max_bad_time_seconds", nullable = false)
    private Integer maxBadTimeSeconds;

    @Column(name = "current_bad_time_seconds", nullable = false)
    private Integer currentBadTimeSeconds;
}
