package com.kiwi.features.metrics.data;

import com.kiwi.common.types.PositiveOrZeroInteger;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Objects;

@Setter
@Getter
public class MetricsDomain {
    private LocalDate date;
    private PositiveOrZeroInteger maxGoodTimeSeconds;
    private PositiveOrZeroInteger currentGoodTimeSeconds;
    private PositiveOrZeroInteger maxBadTimeSeconds;
    private PositiveOrZeroInteger currentBadTimeSeconds;

    public MetricsDomain(LocalDate date, PositiveOrZeroInteger maxGoodTimeSeconds, PositiveOrZeroInteger currentGoodTimeSeconds, PositiveOrZeroInteger maxBadTimeSeconds, PositiveOrZeroInteger currentBadTimeSeconds) {
        this.date = date;
        this.maxGoodTimeSeconds = maxGoodTimeSeconds;
        this.currentGoodTimeSeconds = currentGoodTimeSeconds;
        this.maxBadTimeSeconds = maxBadTimeSeconds;
        this.currentBadTimeSeconds = currentBadTimeSeconds;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MetricsDomain other = (MetricsDomain) o;
        return Objects.equals(date, other.date) &&
                Objects.equals(maxGoodTimeSeconds, other.maxGoodTimeSeconds) &&
                Objects.equals(currentGoodTimeSeconds, other.currentGoodTimeSeconds) &&
                Objects.equals(maxBadTimeSeconds, other.maxBadTimeSeconds) &&
                Objects.equals(currentBadTimeSeconds, other.currentBadTimeSeconds);
    }

}
