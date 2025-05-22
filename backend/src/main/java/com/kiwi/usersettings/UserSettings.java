package com.kiwi.usersettings;

import com.kiwi.users.Email;
import com.kiwi.users.UsersPersistence;
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

    @Column(name = "sound_volume")
    private int soundVolume;

    @Column(name = "music_volume")
    private int musicVolume;

    @Column(name = "is_rumbling_on")
    private boolean isRumblingOn;

    @OneToOne(mappedBy = "userSettings")
    private UsersPersistence user;

    public UserSettings() {
    }

    public UserSettings(Integer id, Email email, int soundVolume, int musicVolume, boolean isRumblingOn) {
        setId(id);
        setEmail(email);
        setSoundVolume(soundVolume);
        setMusicVolume(musicVolume);
        setRumblingOn(isRumblingOn);
    }

    public UserSettings(Email email, int soundVolume, int musicVolume, boolean isRumblingOn) {
        setEmail(email);
        setSoundVolume(soundVolume);
        setMusicVolume(musicVolume);
        setRumblingOn(isRumblingOn);
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

    public boolean isRumblingOn() {
        return isRumblingOn;
    }

    public void setRumblingOn(boolean rumblingOn) {
        isRumblingOn = rumblingOn;
    }

    public UsersPersistence getUser() {
        return user;
    }

    public void setUser(UsersPersistence user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "UserSettings{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", soundVolume=" + soundVolume +
                ", musicVolume=" + musicVolume +
                ", isRumblingOn=" + isRumblingOn +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserSettings that = (UserSettings) o;
        return soundVolume == that.soundVolume &&
                musicVolume == that.musicVolume &&
                isRumblingOn == that.isRumblingOn &&
                Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, soundVolume, musicVolume, isRumblingOn);
    }

    public UserSettingsDTO toDTO() {
        return new UserSettingsDTO(
                getEmail().value(),
                getSoundVolume(),
                getMusicVolume(),
                isRumblingOn()
        );
    }

    public void mergeFromDTO(UserSettingsDTO dto) {
        this.soundVolume = dto.getSoundVolume();
        this.musicVolume = dto.getMusicVolume();
        this.isRumblingOn = dto.isRumblingOn();
    }
}