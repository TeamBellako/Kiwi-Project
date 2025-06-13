package com.kiwi.users;

import com.kiwi.settings.SettingsDTO;

import java.util.Objects;

public class UsersDTO {
    private String email;
    private String password;
    private SettingsDTO settingsDTO;
    
    public UsersDTO() {
    }

    public UsersDTO(String email, String password) {
        this.email = email;
        this.password = password;
        this.settingsDTO = new SettingsDTO(getEmail());
    }

    public UsersDTO(String email, String password, SettingsDTO settingsDTO) {
        this.email = email;
        this.password = password;
        this.settingsDTO = settingsDTO;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public SettingsDTO getSettingsDTO() {
        return settingsDTO;
    }

    public void setSettingsDTO(SettingsDTO settingsDTO) {
        this.settingsDTO = settingsDTO;
    }

    @Override
    public String toString() {
        return "UsersDTO{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", settingsDTO=" + settingsDTO +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UsersDTO usersDTO = (UsersDTO) o;
        return Objects.equals(email, usersDTO.email) && Objects.equals(settingsDTO, usersDTO.settingsDTO);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, password, settingsDTO);
    }
}
