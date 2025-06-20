package com.kiwi.features.settings;

import com.kiwi.types.Email;
import com.kiwi.features.users.UsersPersistence;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "settings")
public class Settings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "sound_volume")
    private int soundVolume;

    @Column(name = "music_volume")
    private int musicVolume;

    @OneToOne(mappedBy = "settings")
    private UsersPersistence user;

    public Settings() {
    }

    public Settings(Integer id, Email email, int soundVolume, int musicVolume) {
        setId(id);
        setEmail(email);
        setSoundVolume(soundVolume);
        setMusicVolume(musicVolume);
    }

    public Settings(Email email, int soundVolume, int musicVolume) {
        setEmail(email);
        setSoundVolume(soundVolume);
        setMusicVolume(musicVolume);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        if (id == null || id <= 0) throw new SettingsInvalidException("Settings Id's must be bigger than zero");
        this.id = id;
    }

    public Email getEmail() {
        return new Email(this.email);
    }

    public void setEmail(Email email) {
        this.email = email.value();
    }

    public int getSoundVolume() {
        return soundVolume;
    }

    public void setSoundVolume(int soundVolume) {
        if (soundVolume < 0 || soundVolume > 100) {
            throw new IllegalArgumentException("Sound volume must be between 0 and 100");
        }
        this.soundVolume = soundVolume;
    }

    public int getMusicVolume() {
        return musicVolume;
    }

    public void setMusicVolume(int musicVolume) {
        if (musicVolume < 0 || musicVolume > 100) {
            throw new IllegalArgumentException("Music volume must be between 0 and 100");
        }
        this.musicVolume = musicVolume;
    }

    public UsersPersistence getUser() {
        return user;
    }

    public void setUser(UsersPersistence user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Settings{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", soundVolume=" + soundVolume +
                ", musicVolume=" + musicVolume +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Settings that = (Settings) o;
        return soundVolume == that.soundVolume &&
                musicVolume == that.musicVolume &&
                Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, soundVolume, musicVolume);
    }

    public SettingsDTO toDTO() {
        return new SettingsDTO(
                getEmail().value(),
                getSoundVolume(),
                getMusicVolume()
        );
    }

    public void mergeFromDTO(SettingsDTO dto) {
        this.soundVolume = dto.getSoundVolume();
        this.musicVolume = dto.getMusicVolume();
    }
}