package com.kiwi.users;

import com.kiwi.usersettings.UserSettings;
import com.kiwi.utils.RegexUtils;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    private String password;

    @OneToOne(mappedBy = "user") 
    private UserSettings userSettings;

    public Users() {
    }

    public Users(Long id, String email, String password) {
        setId(id);
        setEmail(email);
        setPassword(password);
    }

    public Users(String email, String password) {
        setEmail(email);
        setPassword(password);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (!RegexUtils.isValidEmail(email)) throw new IllegalArgumentException("Invalid email format");
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserSettings getUserSettings() {
        return userSettings;
    }

    public void setUserSettings(UserSettings userSettings) {
        this.userSettings = userSettings;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Users users = (Users) o;
        return Objects.equals(id, users.id) && Objects.equals(email, users.email) && Objects.equals(password, users.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, password);
    }

    @Override
    public String toString() {
        return "Users{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public UsersDTO toDTO() {
        return new UsersDTO(
                getEmail(),
                getPassword()
        );
    }
}