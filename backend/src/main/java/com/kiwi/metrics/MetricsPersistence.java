package com.kiwi.metrics;

import com.kiwi.users.Users;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.Duration;
import java.util.Objects;

@Entity
@Table(name = "metrics")
public class MetricsPersistence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "date", nullable = false, unique = true)
    private LocalDate date;
    
    @Column(name = "steps", nullable = false)
    private Integer steps;
    
    @Column(name = "screen_time", nullable = false)
    @Convert(converter = DurationConverter.class)
    private Duration screenTime;

    @ManyToOne
    @JoinColumn(name = "user")
    private Users user;

    public MetricsPersistence() {
    }

    public MetricsPersistence(LocalDate date, PositiveOrZeroInteger steps, PositiveDuration screenTime) {
        this.date = date;
        setSteps(steps);
        setScreenTime(screenTime);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        if (id == null || id <= 0) throw new IllegalArgumentException("Id's must be bigger than zero");

        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public PositiveOrZeroInteger getSteps() {
        return new PositiveOrZeroInteger(this.steps);
    }

    public void setSteps(PositiveOrZeroInteger steps) {
        this.steps = steps.value();
    }

    public PositiveDuration getScreenTime() {
        return new PositiveDuration(this.screenTime);
    }

    public void setScreenTime(PositiveDuration screenTime) {
        this.screenTime = screenTime.value();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MetricsPersistence that = (MetricsPersistence) o;
        return Objects.equals(date, that.date) && Objects.equals(steps, that.steps) && Objects.equals(screenTime, that.screenTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, steps, screenTime);
    }

    @Override
    public String toString() {
        return "MetricsPersistence{" +
                "id=" + id +
                ", date=" + date +
                ", steps=" + steps +
                ", screenTime=" + screenTime +
                '}';
    }
}
