package com.kiwi.metrics;

import java.time.LocalDate;
import java.util.Objects;

public class Metrics {
    private LocalDate date;
    private PositiveOrZeroInteger steps;
    private PositiveDuration screenTime;

    public Metrics(LocalDate date, PositiveOrZeroInteger steps, PositiveDuration screenTime) {
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

    public PositiveOrZeroInteger getSteps() {
        return steps;
    }

    public void setSteps(PositiveOrZeroInteger steps) {
        this.steps = steps;
    }

    public PositiveDuration getScreenTime() {
        return screenTime;
    }

    public void setScreenTime(PositiveDuration screenTime) {
        this.screenTime = screenTime;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Metrics metrics = (Metrics) o;
        return Objects.equals(date, metrics.date) && Objects.equals(steps, metrics.steps) && Objects.equals(screenTime, metrics.screenTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, steps, screenTime);
    }

    @Override
    public String toString() {
        return "Metrics{" +
                "date=" + date +
                ", steps=" + steps +
                ", screenTime=" + screenTime +
                '}';
    }
}