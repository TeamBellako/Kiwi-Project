package com.kiwi.entity;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "user_settings")
public class UserSettings {
    public enum Theme {
        LIGHT,
        DARK,
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "are_notifications_enabled")
    private boolean areNotificationsEnabled;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "theme", nullable = false)
    private Theme theme;

    public UserSettings() {
    }

    public UserSettings(Integer id, String email, boolean areNotificationsEnabled, Theme theme) {
        this.id = id;
        this.email = email;
        this.areNotificationsEnabled = areNotificationsEnabled;
        this.theme = theme;
    }

    public UserSettings(String email, boolean areNotificationsEnabled, Theme theme) {
        this.email = email;
        this.areNotificationsEnabled = areNotificationsEnabled;
        this.theme = theme;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isAreNotificationsEnabled() {
        return areNotificationsEnabled;
    }

    public void setAreNotificationsEnabled(boolean areNotificationsEnabled) {
        this.areNotificationsEnabled = areNotificationsEnabled;
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    @Override
    public String toString() {
        return "UserSettings{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", areNotificationsEnabled=" + areNotificationsEnabled +
                ", theme=" + theme +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserSettings that = (UserSettings) o;
        return areNotificationsEnabled == that.areNotificationsEnabled && Objects.equals(email, that.email) && theme == that.theme;
    }
}