package com.kiwi.users;

import com.kiwi.usersettings.UserSettings;

import java.util.Objects;

public class Users {
    private Email email;
    private Password password;
    private UserSettings userSettings;

    public Users(Email email, Password password, UserSettings userSettings) {
        this.email = email;
        this.password = password;
        this.userSettings = userSettings;
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

    public UserSettings getUserSettings() {
        return userSettings;
    }

    public void setUserSettings(UserSettings userSettings) {
        this.userSettings = userSettings;
    }

    @Override
    public String toString() {
        return "Users{" +
                "email=" + email +
                ", password=" + password +
                ", userSettings=" + userSettings +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Users users = (Users) o;
        return Objects.equals(email, users.email) && Objects.equals(password, users.password) && Objects.equals(userSettings, users.userSettings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, password, userSettings);
    }

    public UsersDTO toDTO() {
        return new UsersDTO(
                getEmail().value(),
                getPassword().value(),
                getUserSettings().toDTO()
        );
    }
    
    public UsersPersistence toPersistence() {
        return new UsersPersistence(
                getEmail(),
                getPassword(),
                getUserSettings()
        );
    }
}
