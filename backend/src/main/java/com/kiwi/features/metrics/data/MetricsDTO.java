package com.kiwi.features.metrics.data;

import java.util.Objects;

public class MetricsDTO {
    private String date;
    private Integer maxGoodTimeSeconds;
    private Integer currentGoodTimeSeconds;
    private Integer maxBadTimeSeconds;
    private Integer currentBadTimeSeconds;

    public MetricsDTO() {
    }

    public MetricsDTO(String date) {
        this.date = date;
        maxGoodTimeSeconds = 0;
        currentGoodTimeSeconds = 0;
        maxBadTimeSeconds = 0;
        currentBadTimeSeconds = 0;
    }

    public MetricsDTO(String date, Integer maxGoodTimeSeconds, Integer currentGoodTimeSeconds, Integer maxBadTimeSeconds, Integer currentBadTimeSeconds) {
        this.date = date;
        this.maxGoodTimeSeconds = maxGoodTimeSeconds;
        this.currentGoodTimeSeconds = currentGoodTimeSeconds;
        this.maxBadTimeSeconds = maxBadTimeSeconds;
        this.currentBadTimeSeconds = currentBadTimeSeconds;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public Integer getMaxGoodTimeSeconds() { return maxGoodTimeSeconds; }
    public void setMaxGoodTimeSeconds(Integer maxGoodTimeSeconds) { this.maxGoodTimeSeconds = maxGoodTimeSeconds; }

    public Integer getCurrentGoodTimeSeconds() { return currentGoodTimeSeconds; }
    public void setCurrentGoodTimeSeconds(Integer currentGoodTimeSeconds) { this.currentGoodTimeSeconds = currentGoodTimeSeconds; }

    public Integer getMaxBadTimeSeconds() { return maxBadTimeSeconds; }
    public void setMaxBadTimeSeconds(Integer maxBadTimeSeconds) { this.maxBadTimeSeconds = maxBadTimeSeconds; }

    public Integer getCurrentBadTimeSeconds() { return currentBadTimeSeconds; }
    public void setCurrentBadTimeSeconds(Integer currentBadTimeSeconds) { this.currentBadTimeSeconds = currentBadTimeSeconds; }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MetricsDTO that = (MetricsDTO) o;
        return Objects.equals(date, that.date) &&
                Objects.equals(maxGoodTimeSeconds, that.maxGoodTimeSeconds) &&
                Objects.equals(currentGoodTimeSeconds, that.currentGoodTimeSeconds) &&
                Objects.equals(maxBadTimeSeconds, that.maxBadTimeSeconds) &&
                Objects.equals(currentBadTimeSeconds, that.currentBadTimeSeconds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, maxGoodTimeSeconds, currentGoodTimeSeconds, maxBadTimeSeconds, currentBadTimeSeconds);
    }

    @Override
    public String toString() {
        return "MetricsDTO{" +
                ", date='" + date + '\'' +
                ", maxGoodTimeSeconds=" + maxGoodTimeSeconds +
                ", currentGoodTimeSeconds=" + currentGoodTimeSeconds +
                ", maxBadTimeSeconds=" + maxBadTimeSeconds +
                ", currentBadTimeSeconds=" + currentBadTimeSeconds +
                '}';
    }
    
    public MetricsDTO copy() {
        return new MetricsDTO(
                getDate(),
                getMaxGoodTimeSeconds(),
                getCurrentGoodTimeSeconds(),
                getMaxBadTimeSeconds(),
                getCurrentBadTimeSeconds()
        );
    }
}