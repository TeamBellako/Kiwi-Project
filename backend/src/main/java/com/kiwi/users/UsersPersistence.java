package com.kiwi.users;

import com.kiwi.usersettings.UserSettings;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "users")
public class UsersPersistence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "email", referencedColumnName = "email", insertable = false, updatable = false)
    private UserSettings userSettings;

    public UsersPersistence() {
    }

    public UsersPersistence(Email email, String password, UserSettings userSettings) {
        setEmail(email);
        this.password = password;
        setUserSettings(userSettings);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        if (id == null || id <= 0) throw new IllegalArgumentException("User Id's must be bigger than zero");

        this.id = id;
    }

    public Email getEmail() {
        return new Email(this.email);
    }

    public void setEmail(Email email) {
        this.email = email.value();
    }

    public String getPassword() {
        return this.password;
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
    public String toString() {
        return "UsersPersistence{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", userSettings=" + userSettings +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UsersPersistence that = (UsersPersistence) o;
        return Objects.equals(email, that.email) && Objects.equals(password, that.password) && Objects.equals(userSettings, that.userSettings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, password, userSettings);
    }
}