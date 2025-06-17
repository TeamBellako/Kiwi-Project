package com.kiwi.metrics;

import java.time.LocalDate;
import java.time.Duration;
import java.util.Objects;

public class MetricsDTO {
    private LocalDate date;
    private Integer steps;
    private Duration screenTime;

    public MetricsDTO(LocalDate date, Integer steps, Duration screenTime) {
        this.date = date;
        this.steps = steps;
        this.screenTime = screenTime;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getSteps() {
        return steps;
    }

    public void setSteps(Integer steps) {
        this.steps = steps;
    }

    public Duration getScreenTime() {
        return screenTime;
    }

    public void setScreenTime(Duration screenTime) {
        this.screenTime = screenTime;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MetricsDTO that = (MetricsDTO) o;
        return Objects.equals(date, that.date) && Objects.equals(steps, that.steps) && Objects.equals(screenTime, that.screenTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, steps, screenTime);
    }

    @Override
    public String toString() {
        return "MetricsDTO{" +
                "date=" + date +
                ", steps=" + steps +
                ", screenTime=" + screenTime +
                '}';
    }
}