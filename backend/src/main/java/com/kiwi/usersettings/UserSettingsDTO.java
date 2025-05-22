package com.kiwi.usersettings;

import com.kiwi.users.Email;

import java.util.Objects;

public class UserSettingsDTO {
    private Integer id;
    private String email = "";
    private int soundVolume = 50;
    private int musicVolume = 50;

    public UserSettingsDTO() {
    }

    public UserSettingsDTO(String email) {
        this.email = email;
    }

    public UserSettingsDTO(String email, int soundVolume, int musicVolume) {
        this.email = email;
        this.soundVolume = soundVolume;
        this.musicVolume = musicVolume;
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

    public int getSoundVolume() {
        return soundVolume;
    }

    public void setSoundVolume(int soundVolume) {
        this.soundVolume = soundVolume;
    }

    public int getMusicVolume() {
        return musicVolume;
    }

    public void setMusicVolume(int musicVolume) {
        this.musicVolume = musicVolume;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserSettingsDTO that = (UserSettingsDTO) o;
        return soundVolume == that.soundVolume &&
                musicVolume == that.musicVolume &&
                Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, soundVolume, musicVolume);
    }

    @Override
    public String toString() {
        return "UserSettingsDTO{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", soundVolume=" + soundVolume +
                ", musicVolume=" + musicVolume +
                '}';
    }

    public UserSettings toDomainObject() {
        return new UserSettings(
                new Email(getEmail()),
                getSoundVolume(),
                getMusicVolume()
        );
    }
}