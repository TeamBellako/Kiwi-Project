package com.kiwi.usersettings;

import com.kiwi.users.Users;
import com.kiwi.utils.RegexUtils;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "user_settings")
public class UserSettings {
    @Id
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @OneToOne(mappedBy = "userSettings")
    private Users users;

    @Column(name = "are_notifications_enabled")
    private boolean areNotificationsEnabled;

    @Enumerated(EnumType.STRING)
    @Column(name = "theme", nullable = false)
    private UserSettingsEnums.Theme theme;

    public UserSettings() {
    }

    public UserSettings(String email, boolean areNotificationsEnabled, UserSettingsEnums.Theme theme) {
        setEmail(email);
        setAreNotificationsEnabled(areNotificationsEnabled);
        setTheme(theme);
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

    public Users getUser() {
        return users;
    }

    public void setUser(Users users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "UserSettings{" +
                "email='" + email + '\'' +
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
                getEmail(),
                isAreNotificationsEnabled(),
                getTheme()
        );
    }
}
