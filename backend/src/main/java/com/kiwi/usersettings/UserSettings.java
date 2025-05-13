package com.kiwi.usersettings;

import com.kiwi.users.Email;
import jakarta.persistence.*;

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

    public UserSettings(Integer id, Email email, boolean areNotificationsEnabled, UserSettingsEnums.Theme theme) {
        setId(id);
        setEmail(email);
        setAreNotificationsEnabled(areNotificationsEnabled);
        setTheme(theme);
    }

    public UserSettings(Email email, boolean areNotificationsEnabled, UserSettingsEnums.Theme theme) {
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

    public Email getEmail() {
        return new Email(this.email);
    }

    public void setEmail(Email email) {
        this.email = email.value();
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
                getEmail().value(),
                isAreNotificationsEnabled(),
                getTheme()
        );
    }

    public void mergeFromDTO(UserSettingsDTO dto) {
        this.areNotificationsEnabled = dto.isAreNotificationsEnabled();
        this.theme = dto.getTheme();
    }

}
