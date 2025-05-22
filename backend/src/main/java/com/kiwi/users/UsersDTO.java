package com.kiwi.users;

import com.kiwi.usersettings.UserSettingsDTO;

import java.util.Objects;

public class UsersDTO {
    private String email;
    private String password;
    private UserSettingsDTO userSettingsDTO;
    
    public UsersDTO() {
    }

    public UsersDTO(String email, String password) {
        this.email = email;
        this.password = password;
        this.userSettingsDTO = new UserSettingsDTO(getEmail());
    }

    public UsersDTO(String email, String password, UserSettingsDTO userSettingsDTO) {
        this.email = email;
        this.password = password;
        this.userSettingsDTO = userSettingsDTO;
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

    public UserSettingsDTO getUserSettingsDTO() {
        return userSettingsDTO;
    }

    public void setUserSettingsDTO(UserSettingsDTO userSettingsDTO) {
        this.userSettingsDTO = userSettingsDTO;
    }

    @Override
    public String toString() {
        return "UsersDTO{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", userSettingsDTO=" + userSettingsDTO +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UsersDTO usersDTO = (UsersDTO) o;
        return Objects.equals(email, usersDTO.email) && Objects.equals(userSettingsDTO, usersDTO.userSettingsDTO);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, password, userSettingsDTO);
    }
}
