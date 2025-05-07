package com.kiwi.usersettings;

import com.kiwi.utils.RegexUtils;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.Objects;

@Entity
@Table(name = "user_settings")
public class UserSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "are_notifications_enabled")
    private boolean areNotificationsEnabled;

    @Enumerated(EnumType.STRING)
    @Column(name = "theme", nullable = false)
    private UserSettingsEnums.Theme theme;

    public UserSettings() {
    }

    public UserSettings(Integer id, String email, boolean areNotificationsEnabled, UserSettingsEnums.Theme theme) {
        setId(id);
        setEmail(email);
        setAreNotificationsEnabled(areNotificationsEnabled);
        setTheme(theme);
    }

    public UserSettings(String email, boolean areNotificationsEnabled, UserSettingsEnums.Theme theme) {
        setEmail(email);
        setAreNotificationsEnabled(areNotificationsEnabled);
        setTheme(theme);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        if (id == null || id <= 0) throw new UserSettingsInvalidException("UserSettings Id's must be bigger than zero");
        
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (!RegexUtils.isValidEmail(email)) throw new UserSettingsInvalidException("Invalid email format");
        
        this.email = email;
    }
    
    public boolean isAreNotificationsEnabled() {
        return areNotificationsEnabled;
    }

    public void setAreNotificationsEnabled(boolean areNotificationsEnabled) {
        this.areNotificationsEnabled = areNotificationsEnabled;
    }

    public UserSettingsEnums.Theme getTheme() {
        return theme;
    }

    public void setTheme(UserSettingsEnums.Theme theme) {
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

    public UserSettingsDTO toDTO() {
        return new UserSettingsDTO(
            getId(),
            getEmail(),
            isAreNotificationsEnabled(),
            getTheme()
        );
    }
}