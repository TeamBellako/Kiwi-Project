package com.kiwi.users;

import com.kiwi.metrics.MetricsDTO;
import com.kiwi.settings.SettingsDTO;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class UsersDTO {
    private String email;
    private String password;
    
    private SettingsDTO settingsDTO;
    private Set<MetricsDTO> metricsDTOs;
    
    public UsersDTO() {
    }

    public UsersDTO(String email, String password) {
        this.email = email;
        this.password = password;
        
        this.settingsDTO = new SettingsDTO(getEmail());
        this.metricsDTOs = new HashSet<>();
        this.metricsDTOs.add(new MetricsDTO(
                LocalDate.now(),
                0,
                Duration.ofSeconds(0)
        ));
    }

    public UsersDTO(String email, String password, SettingsDTO settingsDTO, Set<MetricsDTO> metricsDTOs) {
        this.email = email;
        this.password = password;
        this.settingsDTO = settingsDTO;
        this.metricsDTOs = metricsDTOs;
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

    public Set<MetricsDTO> getMetricsDTOs() {
        return metricsDTOs;
    }

    public void setMetricsDTOs(Set<MetricsDTO> metricsDTOs) {
        this.metricsDTOs = metricsDTOs;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UsersDTO usersDTO = (UsersDTO) o;
        return Objects.equals(email, usersDTO.email) && Objects.equals(password, usersDTO.password) && Objects.equals(settingsDTO, usersDTO.settingsDTO) && Objects.equals(metricsDTOs, usersDTO.metricsDTOs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, password, settingsDTO, metricsDTOs);
    }

    @Override
    public String toString() {
        return "UsersDTO{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", settingsDTO=" + settingsDTO +
                ", metricsDTOs=" + metricsDTOs +
                '}';
    }
}
