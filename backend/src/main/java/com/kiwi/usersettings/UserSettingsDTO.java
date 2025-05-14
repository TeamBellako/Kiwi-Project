package com.kiwi.usersettings;

import com.kiwi.users.Email;

import java.util.Objects;

public class UserSettingsDTO {
    private String email;
    private boolean areNotificationsEnabled;
    private UserSettingsEnums.Theme theme;

    public UserSettingsDTO(String email) {
        this.email = email;
    }

    public UserSettingsDTO(String email, boolean areNotificationsEnabled, UserSettingsEnums.Theme theme) {
        this.email = email;
        this.areNotificationsEnabled = areNotificationsEnabled;
        this.theme = theme;
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

    public UserSettingsEnums.Theme getTheme() {
        return theme;
    }

    public void setTheme(UserSettingsEnums.Theme theme) {
        this.theme = theme;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserSettingsDTO that = (UserSettingsDTO) o;
        return areNotificationsEnabled == that.areNotificationsEnabled && Objects.equals(email, that.email) && theme == that.theme;
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, areNotificationsEnabled, theme);
    }

    @Override
    public String toString() {
        return "UserSettingsDTO{" +
                "email='" + email + '\'' +
                ", areNotificationsEnabled=" + areNotificationsEnabled +
                ", theme=" + theme +
                '}';
    }

    public UserSettings toDomainObject() {
        return new UserSettings(
            new Email(getEmail()),
            isAreNotificationsEnabled(),
            getTheme()
        );
    }
}