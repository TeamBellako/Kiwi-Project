package com.kiwi.settings;

import com.kiwi.users.Email;

import java.util.Objects;

public class SettingsDTO {
    private Integer id;
    private String email = "";
    private int soundVolume = 67;
    private int musicVolume = 67;

    public SettingsDTO() {
    }

    public SettingsDTO(String email) {
        this.email = email;
    }

    public SettingsDTO(String email, int soundVolume, int musicVolume) {
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
        SettingsDTO that = (SettingsDTO) o;
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
        return "SettingsDTO{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", soundVolume=" + soundVolume +
                ", musicVolume=" + musicVolume +
                '}';
    }

    public Settings toDomainObject() {
        return new Settings(
                new Email(getEmail()),
                getSoundVolume(),
                getMusicVolume()
        );
    }
}