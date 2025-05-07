package com.kiwi.usersettings;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.Objects;

public class UserSettingsDTO {

    private Integer id;
    
    @Email(
            regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}",
            flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Invalid email format"
    )
    @NotBlank(message = "Email is required")
    private String email;
    
    private boolean areNotificationsEnabled;
    
    private UserSettingsEnums.Theme theme;

    public UserSettingsDTO(Integer id, String email, boolean areNotificationsEnabled, UserSettingsEnums.Theme theme) {
        this.id = id;
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
        return areNotificationsEnabled == that.areNotificationsEnabled && Objects.equals(id, that.id) && Objects.equals(email, that.email) && theme == that.theme;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, areNotificationsEnabled, theme);
    }

    @Override
    public String toString() {
        return "UserSettingsDTO{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", areNotificationsEnabled=" + areNotificationsEnabled +
                ", theme=" + theme +
                '}';
    }

    public UserSettings toDomainObject() {
        return new UserSettings(
            getId(),
            getEmail(),
            isAreNotificationsEnabled(),
            getTheme()
        );
    }
}

