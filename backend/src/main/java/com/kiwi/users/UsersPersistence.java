package com.kiwi.users;

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

    public UsersPersistence() {
    }

    public UsersPersistence(Email email, Password password) {
        setEmail(email);
        setPassword(password);
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

    public Password getPassword() {
        return new Password(this.password);
    }

    public void setPassword(Password password) {
        this.password = password.value();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UsersPersistence usersPersistence = (UsersPersistence) o;
        return Objects.equals(email, usersPersistence.email) && Objects.equals(password, usersPersistence.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, password);
    }

    @Override
    public String toString() {
        return "UsersPersistence{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public UsersDTO toDTO() {
        return new UsersDTO(
                getEmail().value(),
                getPassword().value()
        );
    }

    public Users toDomainObject() {
        return new Users(
                getEmail(),
                getPassword()
        );
    }
}