package com.kiwi.users;

import java.util.Objects;

public class Users {
    private Email email;
    private Password password;

    public Users(Email email, Password password) {
        this.email = email;
        this.password = password;
    }

    public Email getEmail() {
        return email;
    }

    public void setEmail(Email email) {
        this.email = email;
    }

    public Password getPassword() {
        return password;
    }

    public void setPassword(Password password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Users{" +
                "email=" + email +
                ", password=" + password +
                '}';
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
    
    public UsersDTO toDTO() {
        return new UsersDTO(
                getEmail().value(),
                getPassword().value()
        );
    }
    
    public UsersPersistence toPersistence() {
        return new UsersPersistence(
                getEmail(),
                getPassword()
        );
    }
}
