package com.kiwi.features.settings.data;

import com.kiwi.features.settings.exceptions.SettingsInvalidException;
import com.kiwi.common.types.Email;

import java.util.Objects;

public class SettingsDTO {
    private Integer id;
    private String email = "";
    private float soundVolume = 1;
    private float musicVolume = 1;

    public SettingsDTO() {
    }

    public SettingsDTO(String email) {
        this.email = email;
    }

    public SettingsDTO(String email, float soundVolume, float musicVolume) {
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

    public float getSoundVolume() {
        return soundVolume;
    }

    public void setSoundVolume(float soundVolume) {
        this.soundVolume = soundVolume;
    }

    public float getMusicVolume() {
        return musicVolume;
    }

    public void setMusicVolume(float musicVolume) {
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
        Settings settings;
        try {
            settings = new Settings(
                    new Email(getEmail()),
                    getSoundVolume(),
                    getMusicVolume()
            );
        } catch (IllegalArgumentException e) {
            throw new SettingsInvalidException(e.getMessage());
        }
        return settings;
    }
}