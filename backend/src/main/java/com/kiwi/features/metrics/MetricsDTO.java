package com.kiwi.features.metrics;

import java.util.Objects;

public class MetricsDTO {
    private String date;
    private Integer steps;
    private Integer screenTimeSeconds;

    public MetricsDTO() {
    }
    
    public MetricsDTO(String date, Integer steps, Integer screenTimeSeconds) {
        this.date = date;
        this.steps = steps;
        this.screenTimeSeconds = screenTimeSeconds;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getSteps() {
        return steps;
    }

    public void setSteps(Integer steps) {
        this.steps = steps;
    }

    public Integer getScreenTimeSeconds() {
        return screenTimeSeconds;
    }

    public void setScreenTimeSeconds(Integer screenTimeSeconds) {
        this.screenTimeSeconds = screenTimeSeconds;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MetricsDTO that = (MetricsDTO) o;
        return Objects.equals(date, that.date) && Objects.equals(steps, that.steps) && Objects.equals(screenTimeSeconds, that.screenTimeSeconds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, steps, screenTimeSeconds);
    }

    @Override
    public String toString() {
        return "MetricsDTO{" +
                ", date='" + date + '\'' +
                ", steps=" + steps +
                ", screenTimeSeconds=" + screenTimeSeconds +
                '}';
    }
    
    public MetricsDTO copy() {
        return new MetricsDTO(
                getDate(),
                getSteps(),
                getScreenTimeSeconds()
        );
    }
}