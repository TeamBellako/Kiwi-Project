package com.kiwi.users;

import java.util.Objects;

public class UsersDTO {
    private String email;
    private String password;

    public UsersDTO() {
    }

    public UsersDTO(String email, String password) {
        this.email = email;
        this.password = password;
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UsersDTO usersDTO = (UsersDTO) o;
        return Objects.equals(email, usersDTO.email) && Objects.equals(password, usersDTO.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, password);
    }

    @Override
    public String toString() {
        return "UsersDTO{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
    
    public Users toDomainObject() {
        return new Users(
            new Email(getEmail()),
            new Password(getPassword())
        );
    }
    
    public UsersPersistence toPersistenceObject() {
        return new UsersPersistence(
            new Email(getEmail()),
            new Password(getPassword())
        );
    }
}
