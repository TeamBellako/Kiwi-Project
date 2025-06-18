package com.kiwi.metrics;

import java.time.LocalDate;
import java.time.Duration;
import java.util.Objects;

public class MetricsDTO {
    private String email = "";
    private LocalDate date = LocalDate.now();
    private Integer steps = 0;
    private Duration screenTime = Duration.ofSeconds(0);

    public MetricsDTO(String email) {
        this.email = email;
    }

    public MetricsDTO(String email, LocalDate date, Integer steps, Duration screenTime) {
        this.email = email;
        this.date = date;
        this.steps = steps;
        this.screenTime = screenTime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
        return Objects.equals(email, that.email) && Objects.equals(date, that.date) && Objects.equals(steps, that.steps) && Objects.equals(screenTime, that.screenTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, date, steps, screenTime);
    }

    @Override
    public String toString() {
        return "MetricsDTO{" +
                "email='" + email + '\'' +
                ", date=" + date +
                ", steps=" + steps +
                ", screenTime=" + screenTime +
                '}';
    }
}