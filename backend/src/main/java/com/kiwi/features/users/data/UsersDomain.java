package com.kiwi.features.users.data;

import com.kiwi.features.metrics.data.MetricsDomain;
import com.kiwi.features.settings.data.Settings;
import com.kiwi.common.types.Email;
import com.kiwi.common.types.Password;

import java.util.Objects;
import java.util.Set;

public class UsersDomain {
    private Email email;
    private Password password;
    private Settings settings;
    private Set<MetricsDomain> metrics;

    public UsersDomain(Email email, Password password, Settings settings, Set<MetricsDomain> metrics) {
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

    public Set<MetricsDomain> getMetrics() {
        return metrics;
    }

    public void setMetrics(Set<MetricsDomain> metrics) {
        this.metrics = metrics;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UsersDomain usersDomain = (UsersDomain) o;
        return Objects.equals(email, usersDomain.email);
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