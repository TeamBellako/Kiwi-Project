package com.kiwi.features.settings.data;

public class SettingsDomain {
    private float soundVolume;
    private float musicVolume;

    public SettingsDomain(float soundVolume, float musicVolume) {
        this.soundVolume = soundVolume;
        this.musicVolume = musicVolume;
    }

    public float getSoundVolume() { return soundVolume; }
    public void setSoundVolume(float soundVolume) { this.soundVolume = soundVolume; }

    public float getMusicVolume() { return musicVolume; }
    public void setMusicVolume(float musicVolume) { this.musicVolume = musicVolume; }

}
