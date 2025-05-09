package com.kiwi.users;

import com.kiwi.utils.RegexUtils;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "users")
public class Users {
    @Id
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    private String password;

    public Users() {
    }

    public Users(String email, String password) {
        setEmail(email);
        setPassword(password);
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Users users = (Users) o;
        return Objects.equals(email, users.email) && Objects.equals(password, users.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, password);
    }

    @Override
    public String toString() {
        return "Users{" +
                "email='" + email + '\'' +
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