package com.kiwi.features.metrics;

import com.kiwi.types.PositiveOrZeroInteger;
import com.kiwi.features.users.UsersPersistence;
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

    @Column(name = "max_good_time_seconds", nullable = false)
    private Integer maxGoodTimeSeconds;
    
    @Column(name = "current_good_time_seconds", nullable = false)
    private Integer currentGoodTimeSeconds;

    @Column(name = "max_bad_time_seconds", nullable = false)
    private Integer maxBadTimeSeconds;

    @Column(name = "current_bad_time_seconds", nullable = false)
    private Integer currentBadTimeSeconds;

    public MetricsPersistence() {
    }

    public MetricsPersistence(LocalDate date, PositiveOrZeroInteger maxGoodTimeSeconds, PositiveOrZeroInteger currentGoodTimeSeconds, PositiveOrZeroInteger maxBadTimeSeconds, PositiveOrZeroInteger currentBadTimeSeconds) {
        this.date = date;
        setMaxGoodTimeSeconds(maxGoodTimeSeconds);
        setCurrentGoodTimeSeconds(currentGoodTimeSeconds);
        setMaxBadTimeSeconds(maxBadTimeSeconds);
        setCurrentBadTimeSeconds(currentBadTimeSeconds);
    }

    public MetricsPersistence(UsersPersistence user, LocalDate date, PositiveOrZeroInteger maxGoodTimeSeconds, PositiveOrZeroInteger currentGoodTimeSeconds, PositiveOrZeroInteger maxBadTimeSeconds, PositiveOrZeroInteger currentBadTimeSeconds) {
        this.user = user;
        this.date = date;
        setMaxGoodTimeSeconds(maxGoodTimeSeconds);
        setCurrentGoodTimeSeconds(currentGoodTimeSeconds);
        setMaxBadTimeSeconds(maxBadTimeSeconds);
        setCurrentBadTimeSeconds(currentBadTimeSeconds);
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

    public PositiveOrZeroInteger  getMaxGoodTimeSeconds() { return new PositiveOrZeroInteger(maxGoodTimeSeconds); }
    public void setMaxGoodTimeSeconds(PositiveOrZeroInteger maxGoodTimeSeconds) { this.maxGoodTimeSeconds = maxGoodTimeSeconds.value(); }

    public PositiveOrZeroInteger  getCurrentGoodTimeSeconds() { return new PositiveOrZeroInteger(currentGoodTimeSeconds); }
    public void setCurrentGoodTimeSeconds(PositiveOrZeroInteger currentGoodTimeSeconds) { this.currentGoodTimeSeconds = currentGoodTimeSeconds.value(); }

    public PositiveOrZeroInteger  getMaxBadTimeSeconds() { return new PositiveOrZeroInteger(maxBadTimeSeconds); }
    public void setMaxBadTimeSeconds(PositiveOrZeroInteger maxBadTimeSeconds) { this.maxBadTimeSeconds = maxBadTimeSeconds.value(); }

    public PositiveOrZeroInteger  getCurrentBadTimeSeconds() { return new PositiveOrZeroInteger(currentBadTimeSeconds); }
    public void setCurrentBadTimeSeconds(PositiveOrZeroInteger currentBadTimeSeconds) { this.currentBadTimeSeconds = currentBadTimeSeconds.value(); }

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
        return Objects.equals(date, that.date) &&
                Objects.equals(maxGoodTimeSeconds, that.maxGoodTimeSeconds) &&
                Objects.equals(currentGoodTimeSeconds, that.currentGoodTimeSeconds) &&
                Objects.equals(maxBadTimeSeconds, that.maxBadTimeSeconds) &&
                Objects.equals(currentBadTimeSeconds, that.currentBadTimeSeconds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, maxGoodTimeSeconds, currentGoodTimeSeconds, maxBadTimeSeconds, currentBadTimeSeconds);
    }

    @Override
    public String toString() {
        return "MetricsPersistence{" +
                "id=" + id +
                ", date=" + date +
                ", maxGoodTimeSeconds=" + maxGoodTimeSeconds +
                ", currentGoodTimeSeconds=" + currentGoodTimeSeconds +
                ", maxBadTimeSeconds=" + maxBadTimeSeconds +
                ", currentBadTimeSeconds=" + currentBadTimeSeconds +
                '}';
    }
    
    public void mergeFromDomain(Metrics domain) {
        setMaxGoodTimeSeconds(domain.getMaxGoodTimeSeconds());
        setCurrentGoodTimeSeconds(domain.getCurrentGoodTimeSeconds());
        setMaxBadTimeSeconds(domain.getMaxBadTimeSeconds());
        setCurrentBadTimeSeconds(domain.getCurrentBadTimeSeconds());
    }
}
