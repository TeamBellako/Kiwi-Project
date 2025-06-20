package com.kiwi.features.metrics;

import com.kiwi.types.Email;
import com.kiwi.types.PositiveOrZeroInteger;

import java.time.LocalDate;
import java.util.Objects;

public class Metrics {
    private Email email;
    private LocalDate date;
    private PositiveOrZeroInteger steps;
    private PositiveOrZeroInteger screenTimeSeconds;
    
    public Metrics(Email email, LocalDate date, PositiveOrZeroInteger steps, PositiveOrZeroInteger screenTimeSeconds) {
        this.email = email;
        this.date = date;
        this.steps = steps;
        this.screenTimeSeconds = screenTimeSeconds;
    }

    public Email getEmail() {
        return email;
    }

    public void setEmail(Email email) {
        this.email = email;
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

    public PositiveOrZeroInteger getScreenTimeSeconds() {
        return screenTimeSeconds;
    }

    public void setScreenTime(PositiveOrZeroInteger screenTimeSeconds) {
        this.screenTimeSeconds = screenTimeSeconds;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Metrics metrics = (Metrics) o;
        return Objects.equals(email, metrics.email) && Objects.equals(date, metrics.date) && Objects.equals(steps, metrics.steps) && Objects.equals(screenTimeSeconds, metrics.screenTimeSeconds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, date, steps, screenTimeSeconds);
    }

    @Override
    public String toString() {
        return "Metrics{" +
                "email=" + email +
                ", date=" + date +
                ", steps=" + steps +
                ", screenTimeSeconds=" + screenTimeSeconds +
                '}';
    }
    
    public void merge(Metrics other) {
        this.steps = other.getSteps();
        this.screenTimeSeconds = other.getScreenTimeSeconds();
    }
}