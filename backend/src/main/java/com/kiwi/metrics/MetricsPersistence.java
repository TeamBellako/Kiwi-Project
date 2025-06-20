package com.kiwi.metrics;

import com.kiwi.users.UsersPersistence;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "metrics", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "date"}))
public class MetricsPersistence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UsersPersistence user;

    @Column(name = "date", nullable = false, unique = true)
    private LocalDate date;
    
    @Column(name = "steps", nullable = false)
    private Integer steps;
    
    @Column(name = "screen_time_seconds", nullable = false)
    private Integer screenTimeSeconds;

    public MetricsPersistence() {
    }

    public MetricsPersistence(LocalDate date, PositiveOrZeroInteger steps, PositiveOrZeroInteger screenTimeSeconds) {
        this.date = date;
        setSteps(steps);
        setScreenTime(screenTimeSeconds);
    }

    public MetricsPersistence(UsersPersistence user, LocalDate date, PositiveOrZeroInteger steps, PositiveOrZeroInteger screenTimeSeconds) {
        this.user = user;
        this.date = date;
        setSteps(steps);
        setScreenTime(screenTimeSeconds);
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

    public PositiveOrZeroInteger getScreenTime() {
        return new PositiveOrZeroInteger(this.screenTimeSeconds);
    }

    public void setScreenTime(PositiveOrZeroInteger screenTimeSeconds) {
        this.screenTimeSeconds = screenTimeSeconds.value();
    }

    public void setUser(UsersPersistence user) {
        this.user = user;
    }

    public UsersPersistence getUser() {
        return user;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MetricsPersistence that = (MetricsPersistence) o;
        return Objects.equals(date, that.date) && Objects.equals(steps, that.steps) && Objects.equals(screenTimeSeconds, that.screenTimeSeconds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, steps, screenTimeSeconds);
    }

    @Override
    public String toString() {
        return "MetricsPersistence{" +
                "id=" + id +
                ", date=" + date +
                ", steps=" + steps +
                ", screenTimeSeconds=" + screenTimeSeconds +
                '}';
    }
}
