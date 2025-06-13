package com.kiwi.users;

import com.kiwi.settings.Settings;

import java.util.Objects;

public class Users {
    private Email email;
    private Password password;
    private Settings settings;

    public Users(Email email, Password password, Settings settings) {
        this.email = email;
        this.password = password;
        this.settings = settings;
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

    @Override
    public String toString() {
        return "Users{" +
                "email=" + email +
                ", password=" + password +
                ", settings=" + settings +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Users users = (Users) o;
        return Objects.equals(email, users.email) && Objects.equals(settings, users.settings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, password, settings);
    }
}
