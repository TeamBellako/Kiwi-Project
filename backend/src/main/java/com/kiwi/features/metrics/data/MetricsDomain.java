package com.kiwi.features.metrics.data;

import com.kiwi.common.types.PositiveOrZeroInteger;

import java.time.LocalDate;

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

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public PositiveOrZeroInteger getMaxGoodTimeSeconds() { return maxGoodTimeSeconds; }
    public void setMaxGoodTimeSeconds(PositiveOrZeroInteger maxGoodTimeSeconds) { this.maxGoodTimeSeconds = maxGoodTimeSeconds; }

    public PositiveOrZeroInteger getCurrentGoodTimeSeconds() { return currentGoodTimeSeconds; }
    public void setCurrentGoodTimeSeconds(PositiveOrZeroInteger currentGoodTimeSeconds) { this.currentGoodTimeSeconds = currentGoodTimeSeconds; }

    public PositiveOrZeroInteger getMaxBadTimeSeconds() { return maxBadTimeSeconds; }
    public void setMaxBadTimeSeconds(PositiveOrZeroInteger maxBadTimeSeconds) { this.maxBadTimeSeconds = maxBadTimeSeconds; }

    public PositiveOrZeroInteger getCurrentBadTimeSeconds() { return currentBadTimeSeconds; }
    public void setCurrentBadTimeSeconds(PositiveOrZeroInteger currentBadTimeSeconds) { this.currentBadTimeSeconds = currentBadTimeSeconds; }

}
