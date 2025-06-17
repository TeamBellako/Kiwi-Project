package com.kiwi.users;

import com.kiwi.metrics.Metrics;
import com.kiwi.settings.Settings;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
public class UsersPersistence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "email", referencedColumnName = "email", insertable = false, updatable = false)
    private Settings settings;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Metrics> metrics = new HashSet<>();

    public UsersPersistence() {
    }

    public UsersPersistence(Email email, String password, Settings settings, Set<Metrics> metrics) {
        setEmail(email);
        this.password = password;
        setSettings(settings);
        setMetrics(metrics);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        if (id == null || id <= 0) throw new IllegalArgumentException("Id's must be bigger than zero");

        this.id = id;
    }

    public Email getEmail() {
        return new Email(this.email);
    }

    public void setEmail(Email email) {
        this.email = email.value();
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public Set<Metrics> getMetrics() {
        return metrics;
    }

    public void setMetrics(Set<Metrics> metrics) {
        this.metrics = metrics;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UsersPersistence that = (UsersPersistence) o;
        return Objects.equals(email, that.email) && Objects.equals(password, that.password) && Objects.equals(settings, that.settings) && Objects.equals(metrics, that.metrics);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, password, settings, metrics);
    }

    @Override
    public String toString() {
        return "UsersPersistence{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", settings=" + settings +
                ", metrics=" + metrics +
                '}';
    }
}