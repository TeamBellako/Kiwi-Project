package com.kiwi.features.metrics.data;

import com.kiwi.common.types.PositiveOrZeroInteger;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @EqualsAndHashCode @ToString
public class MetricsDomain {
    private LocalDate date;
    private PositiveOrZeroInteger maxGoodTimeSeconds;
    private PositiveOrZeroInteger currentGoodTimeSeconds;
    private PositiveOrZeroInteger maxBadTimeSeconds;
    private PositiveOrZeroInteger currentBadTimeSeconds;

    public void update(MetricsDTO dto) {
        setCurrentGoodTimeSeconds(new PositiveOrZeroInteger(dto.getCurrentGoodTimeSeconds()));
        setCurrentBadTimeSeconds(new PositiveOrZeroInteger(dto.getCurrentBadTimeSeconds()));
    }
}
