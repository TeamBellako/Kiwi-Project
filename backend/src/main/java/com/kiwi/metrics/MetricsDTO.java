package com.kiwi.metrics;

import java.util.Objects;

public class MetricsDTO {
    private String email;
    private String date;
    private Integer steps;
    private Integer screenTimeSeconds;

    public MetricsDTO() {
    }

    public MetricsDTO(String email) {
        this.email = email;
    }

    public MetricsDTO(String email, String date, Integer steps, Integer screenTimeSeconds) {
        this.email = email;
        this.date = date;
        this.steps = steps;
        this.screenTimeSeconds = screenTimeSeconds;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public Integer getscreenTimeSeconds() {
        return screenTimeSeconds;
    }

    public void setScreenTimeSeconds(Integer screenTimeSeconds) {
        this.screenTimeSeconds = screenTimeSeconds;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MetricsDTO that = (MetricsDTO) o;
        return Objects.equals(email, that.email) && Objects.equals(date, that.date) && Objects.equals(steps, that.steps) && Objects.equals(screenTimeSeconds, that.screenTimeSeconds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, date, steps, screenTimeSeconds);
    }

    @Override
    public String toString() {
        return "MetricsDTO{" +
                "email='" + email + '\'' +
                ", date='" + date + '\'' +
                ", steps=" + steps +
                ", screenTimeSeconds=" + screenTimeSeconds +
                '}';
    }
    
    public MetricsDTO copy() {
        return new MetricsDTO(
                getEmail(),
                getDate(),
                getSteps(),
                getscreenTimeSeconds()
        );
    }
}