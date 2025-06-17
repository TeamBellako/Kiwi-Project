package com.kiwi.users;

import com.kiwi.metrics.Metrics;
import com.kiwi.settings.Settings;

import java.util.Objects;
import java.util.Set;

public class Users {
    private Email email;
    private Password password;
    private Settings settings;
    private Set<Metrics> metrics;

    public Users(Email email, Password password, Settings settings, Set<Metrics> metrics) {
        this.email = email;
        this.password = password;
        this.settings = settings;
        this.metrics = metrics;
    }

    public Email getEmail() {
        return email;
    }

    public void setEmail(Email email) {
        this.email = email;
    }

    public Password getPassword() {
        return password;
    }

    public void setPassword(Password password) {
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
        Users users = (Users) o;
        return Objects.equals(email, users.email) && Objects.equals(password, users.password) && Objects.equals(settings, users.settings) && Objects.equals(metrics, users.metrics);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, password, settings, metrics);
    }

    @Override
    public String toString() {
        return "Users{" +
                "email=" + email +
                ", password=" + password +
                ", settings=" + settings +
                ", metrics=" + metrics +
                '}';
    }
}